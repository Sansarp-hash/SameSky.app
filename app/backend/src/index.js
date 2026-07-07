require('dotenv').config();
const express = require('express');
const cors = require('cors');
const jwt = require('jsonwebtoken');
const prisma = require('./lib/prismaClient');
const { apiRateLimiter } = require('./middleware/rateLimiter');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// Apply rate limiting to all API routes
app.use('/api', apiRateLimiter);

// Authentication Middleware
const authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];

  if (token == null) return res.sendStatus(401);

  jwt.verify(token, process.env.JWT_SECRET || 'fallback_secret', (err, user) => {
    if (err) return res.sendStatus(403);
    req.user = user;
    next();
  });
};

// Use routes
app.use('/api/auth', require('./routes/auth')(prisma));
app.use('/api/mbti', authenticateToken, require('./routes/mbti')(prisma));
app.use('/api/tarot', authenticateToken, require('./routes/tarot')(prisma));
app.use('/api/astrology', authenticateToken, require('./routes/astrology')(prisma));
app.use('/api/community', authenticateToken, require('./routes/community')(prisma));
app.use('/api/content', authenticateToken, require('./routes/content')(prisma));
app.use('/api/upload', authenticateToken, require('./routes/upload')(prisma));
app.use('/api/admin', authenticateToken, require('./routes/admin')(prisma));
app.use('/api/user', authenticateToken, require('./routes/user')(prisma));
app.use('/api/movies', authenticateToken, require('./routes/movies')(prisma));

const path = require('path');
app.use('/uploads', express.static(path.join(__dirname, '../public/uploads')));

const cron = require('node-cron');
const { generateDailyStats } = require('./services/analyticsService');
const { performBackup } = require('./scripts/dbBackup');

// Schedule analytics cron job to run daily at midnight
cron.schedule('0 0 * * *', async () => {
  console.log('Running daily analytics cron job...');
  try {
    await generateDailyStats();
    console.log('Daily analytics generated successfully.');
  } catch (error) {
    console.error('Error in daily analytics cron job:', error);
  }
});

// Schedule database backup to run weekly on Sunday at 1:00 AM
cron.schedule('0 1 * * 0', async () => {
  console.log('Running weekly database backup cron job...');
  try {
    await performBackup();
  } catch (error) {
    console.error('Error in weekly database backup cron job:', error);
  }
});

app.listen(PORT, () => {
  console.log(`SameSky API running on port ${PORT}`);
});
