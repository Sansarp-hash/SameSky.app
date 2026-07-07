const express = require('express');
const router = express.Router();

module.exports = (prisma) => {
  // GET tarot readings for user
  router.get('/:userId', async (req, res) => {
    try {
      const readings = await prisma.tarotReading.findMany({
        where: { userId: req.params.userId },
        orderBy: { createdAt: 'desc' }
      });
      res.json(readings);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  });

  // POST draw tarot cards
  router.post('/:userId/draw', async (req, res) => {
    try {
      const allCards = ["The Fool", "The Magician", "The High Priestess", "The Empress", "The Emperor", "The Hierophant", "The Lovers", "The Chariot", "Strength", "The Hermit", "Wheel of Fortune", "Justice", "The Hanged Man", "Death", "Temperance", "The Devil", "The Tower", "The Star", "The Moon", "The Sun", "Judgement", "The World"];
      const shuffled = allCards.sort(() => 0.5 - Math.random());
      const drawnCards = shuffled.slice(0, 3).join(", ");
      
      // Simulate Gemini reading generation for the backend
      const reading = `Your cards are ${drawnCards}. This signifies a journey of growth, harmony, and new beginnings in your fandom life.`;

      const savedReading = await prisma.tarotReading.create({
        data: {
          userId: req.params.userId,
          cards: drawnCards,
          reading: reading
        }
      });
      res.json(savedReading);
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  });

  return router;
};
