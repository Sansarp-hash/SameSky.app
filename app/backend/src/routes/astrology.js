const express = require('express');
const router = express.Router();

module.exports = (prisma) => {
  // GET user astrology info
  router.get('/:userId', async (req, res) => {
    try {
      const user = await prisma.user.findUnique({
        where: { id: req.params.userId },
        select: { sunSign: true, moonSign: true, risingSign: true }
      });
      if (!user) return res.status(404).json({ error: 'User not found' });
      res.json(user);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  });

  // POST update user astrology info
  router.post('/:userId', async (req, res) => {
    try {
      const { sunSign, moonSign, risingSign } = req.body;
      const updatedUser = await prisma.user.update({
        where: { id: req.params.userId },
        data: { sunSign, moonSign, risingSign }
      });
      res.json({ sunSign: updatedUser.sunSign, moonSign: updatedUser.moonSign, risingSign: updatedUser.risingSign });
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  });

  return router;
};
