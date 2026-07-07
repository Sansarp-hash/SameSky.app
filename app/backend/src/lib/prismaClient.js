const { PrismaClient } = require('@prisma/client');

// Initialize Prisma Client singleton
let prisma;

// Define slow query warning threshold in milliseconds (e.g., 200ms)
const SLOW_QUERY_THRESHOLD_MS = 200;

if (process.env.NODE_ENV === 'production') {
  prisma = new PrismaClient({
    log: ['error', 'warn'],
  });
} else {
  // In development, keep global instance to avoid hot-reloading leaking connections
  if (!global.globalPrisma) {
    global.globalPrisma = new PrismaClient({
      log: ['info', 'warn', 'error'],
    });
  }
  prisma = global.globalPrisma;
}

// Implement middleware for automatic logging of slow queries
prisma.$use(async (params, next) => {
  const startTime = Date.now();
  const result = await next(params);
  const duration = Date.now() - startTime;

  if (duration >= SLOW_QUERY_THRESHOLD_MS) {
    console.warn(
      `\x1b[33m[PRISMA SLOW QUERY ALERT]\x1b[0m Model: \x1b[36m${params.model || 'N/A'}\x1b[0m, Action: \x1b[35m${params.action}\x1b[0m, Took: \x1b[31m${duration}ms\x1b[0m`
    );
    if (process.env.NODE_ENV !== 'production') {
      console.warn(`[PRISMA DETAILS] Params:`, JSON.stringify(params.args || {}));
    }
  }

  return result;
});

module.exports = prisma;
