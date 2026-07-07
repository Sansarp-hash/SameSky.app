const rateLimit = require('express-rate-limit');

/**
 * 1. Authentication rate limiter (protects Login and Registration)
 * Strict limit: Max 5 attempts per 15-minute window to mitigate brute-force.
 */
const authRateLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 5,
  message: {
    success: false,
    error: 'Too many login or registration attempts. Please try again after 15 minutes.'
  },
  standardHeaders: true, // Return rate limit info in the `RateLimit-*` headers
  legacyHeaders: false, // Disable the `X-RateLimit-*` headers
});

/**
 * 2. Raffle submission rate limiter (protects ticket generation and entries)
 * Strict limit: Max 3 entries per 15 minutes to prevent scripting/automated submissions.
 */
const raffleRateLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 3,
  message: {
    success: false,
    error: 'Too many raffle entry attempts detected. Please wait 15 minutes before submitting again.'
  },
  standardHeaders: true,
  legacyHeaders: false,
});

/**
 * 3. General API rate limiter (protects other routes)
 * Standard limit: Max 100 requests per 15 minutes.
 */
const apiRateLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100,
  message: {
    success: false,
    error: 'Too many requests. Please throttle your connections and try again later.'
  },
  standardHeaders: true,
  legacyHeaders: false,
});

module.exports = {
  authRateLimiter,
  raffleRateLimiter,
  apiRateLimiter
};
