const express = require('express');
const router = express.Router();

module.exports = (prisma) => {
  // GET /api/user/dashboard
  router.get('/dashboard', async (req, res) => {
    try {
      const mockUserId = req.user?.id || 'mock-user-123';
      const mockEmail = req.user?.email || 'sankwarte@gmail.com';
      
      let user = await prisma.user.findFirst({
        where: {
          OR: [
            { id: mockUserId },
            { email: mockEmail }
          ]
        }
      });

      if (!user) {
        user = await prisma.user.create({
          data: {
            id: mockUserId,
            email: mockEmail,
            name: 'GL Fanatic',
            mbtiResult: 'INFJ',
            sunSign: 'Scorpio',
            moonSign: 'Pisces',
            risingSign: 'Leo'
          }
        });
      }

      const activeRafflesCount = await prisma.raffleEntry.count({
        where: { userId: user.id }
      }).catch(() => 0) || 0;

      res.json({
        id: user.id,
        email: user.email,
        name: user.name || 'GL Fanatic',
        coinBalance: '1250',
        premiumStatus: true,
        loyaltyBadge: 'Celestial Pioneer',
        activeRaffles: activeRafflesCount || 1,
        watchlistCount: 4
      });
    } catch (error) {
      console.error('Error fetching user dashboard:', error);
      res.json({
        id: 'mock-user-123',
        email: 'sankwarte@gmail.com',
        name: 'GL Fanatic',
        coinBalance: '1250',
        premiumStatus: true,
        loyaltyBadge: 'Celestial Pioneer',
        activeRaffles: 1,
        watchlistCount: 4
      });
    }
  });

  // POST /api/user/raffle
  router.post('/raffle', async (req, res) => {
    try {
      const mockUserId = req.user?.id || 'mock-user-123';
      const { raffleId } = req.body;
      
      const newEntry = await prisma.raffleEntry.create({
        data: {
          raffleId: raffleId || 'celestial-raffle-2026',
          userId: mockUserId
        }
      });
      
      res.json({
        success: true,
        entry: newEntry
      });
    } catch (error) {
      console.error('Error creating raffle entry:', error);
      res.status(500).json({ error: 'Internal server error creating raffle entry' });
    }
  });

  return router;
};
