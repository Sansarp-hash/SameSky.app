const prisma = require('../lib/prismaClient');
const { getCache, setCache } = require('../services/cacheService');

/**
 * SEARCH & FETCH (with SWR Cache):
 * Accepts a movie search query, securely forwards it to the TMDb API,
 * and caches results using a Stale-While-Revalidate (SWR) strategy in Redis.
 * 
 * SWR behavior:
 * - If cache is less than 1 minute old (FRESH): returns cache instantly (HIT_FRESH).
 * - If cache is between 1 minute and 24 hours old (STALE): returns cache instantly (HIT_STALE)
 *   and triggers background fetch from TMDb in parallel to refresh the cache.
 * - If cache is missing or older than 24 hours (MISS): fetches synchronously from TMDb.
 * 
 * Route: GET /api/movies/search?query=name
 */
const searchMovies = async (req, res) => {
  try {
    const { query, page } = req.query;

    if (!query || query.trim() === '') {
      return res.status(400).json({ error: 'Query parameter "query" is required and cannot be empty' });
    }

    const tmdbApiKey = process.env.TMDB_API_KEY;
    if (!tmdbApiKey) {
      console.error('TMDB_API_KEY environment variable is not defined.');
      return res.status(500).json({ error: 'TMDb API is not configured on the server.' });
    }

    const trimmedQuery = query.trim().toLowerCase();
    const pageNum = parseInt(page, 10) || 1;
    const cacheKey = `swr:movies:search:${trimmedQuery}:${pageNum}`;
    const FRESH_TTL_MS = 60 * 1000; // 1 minute (Fresh window)
    const STALE_TTL_SEC = 24 * 60 * 60; // 24 hours (Stale/Max cache lifetime)

    // SWR fetch helper function
    const fetchAndFormatFromTmdb = async () => {
      const url = `https://api.themoviedb.org/3/search/movie?api_key=${tmdbApiKey}&query=${encodeURIComponent(query)}&language=en-US&page=${pageNum}&include_adult=false`;
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`TMDb API returned status ${response.status}`);
      }
      const data = await response.json();
      
      // Limit to 20 results per request and use optimized w342 width for posters
      const rawResults = data.results || [];
      const limitedResults = rawResults.slice(0, 20);

      const formattedMovies = limitedResults.map(movie => ({
        tmdb_id: movie.id,
        title: movie.title,
        description: movie.overview,
        release_date: movie.release_date,
        poster_path: movie.poster_path ? `https://image.tmdb.org/t/p/w342${movie.poster_path}` : null,
        backdrop_path: movie.backdrop_path ? `https://image.tmdb.org/t/p/w1280${movie.backdrop_path}` : null,
        vote_average: movie.vote_average,
      }));

      const payload = {
        results: formattedMovies,
        page: data.page,
        total_results: Math.min(data.total_results || 0, 1000), // Cap at 1000 for safety
        total_pages: data.total_pages
      };

      const cacheData = {
        payload,
        cachedAt: Date.now()
      };

      // Background cache update with complete stale TTL window
      await setCache(cacheKey, cacheData, STALE_TTL_SEC);
      return payload;
    };

    // Attempt to read from cache
    const cachedObj = await getCache(cacheKey);

    if (cachedObj && cachedObj.payload && cachedObj.cachedAt) {
      const age = Date.now() - cachedObj.cachedAt;
      if (age < FRESH_TTL_MS) {
        // Cache is fresh, return immediately
        res.setHeader('X-Cache-SWR', 'HIT_FRESH');
        return res.status(200).json(cachedObj.payload);
      } else {
        // Cache is stale, return immediately and revalidate in background
        res.setHeader('X-Cache-SWR', 'HIT_STALE');
        
        // Background revalidation
        fetchAndFormatFromTmdb().then(() => {
          console.log(`[SWR Cache] Background revalidation completed for query: "${trimmedQuery}" page: ${pageNum}`);
        }).catch((err) => {
          console.error(`[SWR Cache] Background revalidation failed for query: "${trimmedQuery}" page: ${pageNum}:`, err.message);
        });

        return res.status(200).json(cachedObj.payload);
      }
    }

    // Cache miss, synchronous fetch from TMDb
    res.setHeader('X-Cache-SWR', 'MISS');
    const freshPayload = await fetchAndFormatFromTmdb();
    return res.status(200).json(freshPayload);

  } catch (error) {
    console.error('Error searching movies:', error);
    return res.status(500).json({ error: 'Internal server error while searching movies' });
  }
};

/**
 * WATCHLIST SAVE LOGIC: Saves a movie to a user's watchlist.
 * Follows database best practices: only saves user's ID and tmdb_id to Supabase (watchlists table).
 * 
 * Route: POST /api/movies/watchlist
 */
const addToWatchlist = async (req, res) => {
  try {
    const userId = req.user?.id;
    if (!userId) {
      return res.status(401).json({ error: 'Unauthorized: User authentication required' });
    }

    const { tmdb_id } = req.body;
    if (!tmdb_id) {
      return res.status(400).json({ error: 'Field "tmdb_id" is required in request body' });
    }

    const tmdbIdInt = parseInt(tmdb_id, 10);
    if (isNaN(tmdbIdInt)) {
      return res.status(400).json({ error: 'Field "tmdb_id" must be a valid integer' });
    }

    // Check if the movie already exists in the user's watchlist to prevent duplicates
    const existingWatchlistEntry = await prisma.watchlist.findUnique({
      where: {
        userId_tmdbId: {
          userId: userId,
          tmdbId: tmdbIdInt
        }
      }
    });

    if (existingWatchlistEntry) {
      return res.status(409).json({ 
        message: 'Movie is already in your watchlist',
        entry: existingWatchlistEntry 
      });
    }

    // Insert a single row into 'watchlists' table
    const newWatchlistEntry = await prisma.watchlist.create({
      data: {
        userId: userId,
        tmdbId: tmdbIdInt
      }
    });

    return res.status(201).json({
      message: 'Movie successfully added to your watchlist',
      entry: newWatchlistEntry
    });
  } catch (error) {
    console.error('Error adding movie to watchlist:', error);
    return res.status(500).json({ error: 'Internal server error while adding to watchlist' });
  }
};

/**
 * REMOVE FROM WATCHLIST: Deletes a saved movie from the authenticated user's watchlist.
 * 
 * Route: DELETE /api/movies/watchlist
 */
const removeFromWatchlist = async (req, res) => {
  try {
    const userId = req.user?.id;
    if (!userId) {
      return res.status(401).json({ error: 'Unauthorized: User authentication required' });
    }

    const { tmdb_id } = req.body;
    if (!tmdb_id) {
      return res.status(400).json({ error: 'Field "tmdb_id" is required in request body' });
    }

    const tmdbIdInt = parseInt(tmdb_id, 10);
    if (isNaN(tmdbIdInt)) {
      return res.status(400).json({ error: 'Field "tmdb_id" must be a valid integer' });
    }

    await prisma.watchlist.delete({
      where: {
        userId_tmdbId: {
          userId: userId,
          tmdbId: tmdbIdInt
        }
      }
    });

    return res.status(200).json({
      success: true,
      message: 'Movie successfully removed from your watchlist'
    });
  } catch (error) {
    console.error('Error removing movie from watchlist:', error);
    return res.status(500).json({ error: 'Internal server error while removing from watchlist' });
  }
};

/**
 * FETCH WATCHLIST: Retrieves all saved tmdb_ids for the authenticated user
 * and fetches the corresponding metadata from TMDb in parallel (utilizing caching).
 * 
 * Route: GET /api/movies/watchlist
 */
const getWatchlist = async (req, res) => {
  try {
    const userId = req.user?.id;
    if (!userId) {
      return res.status(401).json({ error: 'Unauthorized: User authentication required' });
    }

    // Fetch saved tmdb_ids from Prisma
    const watchlist = await prisma.watchlist.findMany({
      where: { userId: userId },
      orderBy: { createdAt: 'desc' }
    });

    const tmdbApiKey = process.env.TMDB_API_KEY;
    if (!tmdbApiKey) {
      console.warn('TMDB_API_KEY is not defined. Returning watchlist without metadata.');
      return res.status(200).json({
        watchlist: watchlist.map(item => ({
          id: item.id,
          user_id: item.userId,
          tmdb_id: item.tmdbId,
          created_at: item.createdAt,
          movie: null
        }))
      });
    }

    // Fetch movie details from TMDb API with caching to prevent hitting TMDb repeatedly
    const moviesWithMetadata = await Promise.all(
      watchlist.map(async (item) => {
        const movieCacheKey = `swr:movies:detail:${item.tmdbId}`;
        try {
          let movieData = await getCache(movieCacheKey);

          if (!movieData) {
            const url = `https://api.themoviedb.org/3/movie/${item.tmdbId}?api_key=${tmdbApiKey}&language=en-US`;
            const response = await fetch(url);
            if (response.ok) {
              const movie = await response.json();
              movieData = {
                tmdb_id: movie.id,
                title: movie.title,
                description: movie.overview,
                release_date: movie.release_date,
                poster_path: movie.poster_path ? `https://image.tmdb.org/t/p/w342${movie.poster_path}` : null,
                backdrop_path: movie.backdrop_path ? `https://image.tmdb.org/t/p/w1280${movie.backdrop_path}` : null,
                vote_average: movie.vote_average,
                runtime: movie.runtime,
                genres: movie.genres?.map(g => g.name) || []
              };
              // Cache movie metadata details for 7 days
              await setCache(movieCacheKey, movieData, 7 * 24 * 60 * 60);
            }
          }

          return {
            id: item.id,
            user_id: item.userId,
            tmdb_id: item.tmdbId,
            created_at: item.createdAt,
            movie: movieData
          };
        } catch (err) {
          console.error(`Failed to fetch TMDb details for movie ID ${item.tmdbId}:`, err);
          return {
            id: item.id,
            user_id: item.userId,
            tmdb_id: item.tmdbId,
            created_at: item.createdAt,
            movie: null
          };
        }
      })
    );

    return res.status(200).json({ watchlist: moviesWithMetadata });
  } catch (error) {
    console.error('Error fetching watchlist:', error);
    return res.status(500).json({ error: 'Internal server error while fetching watchlist' });
  }
};

module.exports = {
  searchMovies,
  addToWatchlist,
  removeFromWatchlist,
  getWatchlist
};
