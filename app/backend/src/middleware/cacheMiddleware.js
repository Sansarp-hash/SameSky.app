const { getCache, setCache } = require('../services/cacheService');

/**
 * Express middleware to cache GET requests in Redis.
 * @param {number} ttlSeconds Expiration time for cached responses in seconds (defaults to 300s/5m).
 */
const cacheMiddleware = (ttlSeconds = 300) => {
  return async (req, res, next) => {
    // We only cache GET requests
    if (req.method !== 'GET') {
      return next();
    }

    // Generate a unique cache key based on the original URL (including path and query params)
    const cacheKey = `cache:${req.originalUrl || req.url}`;

    try {
      const cachedResponse = await getCache(cacheKey);

      if (cachedResponse) {
        // Set response headers to indicate a cache hit
        res.setHeader('X-Cache', 'HIT');
        res.setHeader('Content-Type', 'application/json');
        return res.json(cachedResponse);
      }

      // Set header to indicate a cache miss
      res.setHeader('X-Cache', 'MISS');

      // Intercept res.json to capture and cache the data
      const originalJson = res.json;
      res.json = function (body) {
        // Only cache successful JSON responses
        if (res.statusCode >= 200 && res.statusCode < 300) {
          setCache(cacheKey, body, ttlSeconds).catch((err) => {
            console.error('Async background cache set failed:', err);
          });
        }
        return originalJson.call(this, body);
      };

      next();
    } catch (error) {
      console.error('Cache middleware error:', error);
      next();
    }
  };
};

module.exports = cacheMiddleware;
