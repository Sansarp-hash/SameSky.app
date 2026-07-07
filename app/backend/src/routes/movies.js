const express = require('express');
const router = express.Router();
const movieController = require('../controllers/movieController');

module.exports = (prisma) => {
  /**
   * @route   GET /api/movies/search
   * @desc    Search for movies via the TMDb API
   * @access  Private (or Public, but protected here via route middleware in index.js)
   */
  router.get('/search', movieController.searchMovies);

  /**
   * @route   POST /api/movies/watchlist
   * @desc    Add a movie tmdb_id to the user's watchlist in Supabase
   * @access  Private (Authenticated)
   */
  router.post('/watchlist', movieController.addToWatchlist);

  /**
   * @route   GET /api/movies/watchlist
   * @desc    Get the authenticated user's saved watchlist from Supabase
   * @access  Private (Authenticated)
   */
  router.get('/watchlist', movieController.getWatchlist);

  /**
   * @route   DELETE /api/movies/watchlist
   * @desc    Remove a movie tmdb_id from the user's watchlist in Supabase
   * @access  Private (Authenticated)
   */
  router.delete('/watchlist', movieController.removeFromWatchlist);

  return router;
};
