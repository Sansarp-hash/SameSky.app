const { createClient } = require('redis');

const redisUrl = process.env.REDIS_URL || 'redis://localhost:6379';
const client = createClient({
  url: redisUrl
});

client.on('error', (err) => {
  console.error('Redis client error:', err);
});

client.on('connect', () => {
  console.log('Successfully connected to Redis');
});

// Immediately invoke connection in the background
let isConnected = false;
(async () => {
  try {
    await client.connect();
    isConnected = true;
  } catch (error) {
    console.error('Failed to initialize Redis connection:', error);
  }
})();

/**
 * Safely get a value from cache.
 * Falls back to null on Redis failure.
 */
const getCache = async (key) => {
  if (!isConnected) return null;
  try {
    const value = await client.get(key);
    return value ? JSON.parse(value) : null;
  } catch (error) {
    console.error(`Error getting cache for key ${key}:`, error);
    return null;
  }
};

/**
 * Safely set a value in cache with an expiration time in seconds.
 * Falls back to doing nothing on Redis failure.
 */
const setCache = async (key, value, ttlSeconds = 300) => {
  if (!isConnected) return false;
  try {
    const serializedValue = JSON.stringify(value);
    await client.set(key, serializedValue, {
      EX: ttlSeconds
    });
    return true;
  } catch (error) {
    console.error(`Error setting cache for key ${key}:`, error);
    return false;
  }
};

/**
 * Safely delete a key from cache.
 */
const deleteCache = async (key) => {
  if (!isConnected) return false;
  try {
    await client.del(key);
    return true;
  } catch (error) {
    console.error(`Error deleting cache for key ${key}:`, error);
    return false;
  }
};

module.exports = {
  client,
  getCache,
  setCache,
  deleteCache
};
