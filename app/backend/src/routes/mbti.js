const express = require('express');
const router = express.Router();

module.exports = (prisma) => {
  // GET user MBTI
  router.get('/:userId/result', async (req, res) => {
    try {
      const user = await prisma.user.findUnique({
        where: { id: req.params.userId },
        select: { mbtiResult: true }
      });
      if (!user) return res.status(404).json({ error: 'User not found' });
      res.json({ result: user.mbtiResult });
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  });

  // POST update user MBTI
  router.post('/:userId/result', async (req, res) => {
    try {
      const { result } = req.body;
      const updatedUser = await prisma.user.update({
        where: { id: req.params.userId },
        data: { mbtiResult: result }
      });
      res.json({ result: updatedUser.mbtiResult });
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  });

  return router;
};
