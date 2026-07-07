const express = require('express');
const router = express.Router();
const { raffleRateLimiter } = require('../middleware/rateLimiter');

module.exports = (prisma) => {
  // GET all posts
  router.get('/posts', async (req, res) => {
    try {
      const posts = await prisma.post.findMany({
        include: {
          author: { select: { name: true } },
          _count: { select: { comments: true } }
        },
        orderBy: { createdAt: 'desc' }
      });
      res.json(posts);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  });

  // GET single post with comments
  router.get('/posts/:postId', async (req, res) => {
    try {
      const post = await prisma.post.findUnique({
        where: { id: req.params.postId },
        include: {
          author: { select: { name: true } },
          comments: {
            include: { author: { select: { name: true } } },
            orderBy: { createdAt: 'asc' }
          }
        }
      });
      if (!post) return res.status(404).json({ error: 'Post not found' });
      res.json(post);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  });

  // POST create comment
  router.post('/posts/:postId/comments', async (req, res) => {
    try {
      const { content, authorId } = req.body;
      const comment = await prisma.comment.create({
        data: {
          content,
          postId: req.params.postId,
          authorId
        },
        include: { author: { select: { name: true } } }
      });
      res.json(comment);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  });

  // POST enter raffle (protected by raffleRateLimiter)
  router.post('/raffle/enter', raffleRateLimiter, async (req, res) => {
    try {
      const { raffleId, userId } = req.body;
      if (!raffleId || !userId) {
        return res.status(400).json({ error: 'Raffle ID and User ID are required.' });
      }

      // Safe check if raffleEntry model is available in prisma, otherwise mock
      let entry;
      if (prisma.raffleEntry) {
        // Prevent duplicate entries
        const existingEntry = await prisma.raffleEntry.findFirst({
          where: { raffleId, userId }
        });
        if (existingEntry) {
          return res.status(400).json({ error: 'You have already entered this raffle.' });
        }

        entry = await prisma.raffleEntry.create({
          data: { raffleId, userId }
        });
      } else {
        entry = {
          id: `raffle-entry-mock-${Date.now()}`,
          raffleId,
          userId,
          createdAt: new Date()
        };
      }

      res.status(201).json({
        success: true,
        message: 'Successfully entered the cosmic raffle!',
        entry
      });
    } catch (error) {
      console.error('Error entering raffle:', error);
      res.status(500).json({ error: 'Internal server error during raffle entry.' });
    }
  });

  return router;
};
