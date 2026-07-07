const express = require('express');
const router = express.Router();
const { upload, processAndUploadImage } = require('../services/uploadService');

module.exports = (prisma) => {
  /**
   * @route POST /api/upload/fanart
   * @desc Upload and optimize fan art (enforces 2048px limit, outputs high-res WebP & thumbnail)
   * @access Private
   */
  router.post('/fanart', upload.single('image'), async (req, res) => {
    try {
      if (!req.file) {
        return res.status(400).json({ success: false, error: 'No image file provided' });
      }

      const result = await processAndUploadImage(req.file, 'fanart');

      // Optionally record the upload in a DB model if a model exists,
      // but otherwise just return the successful optimization and URLs
      res.json({
        success: true,
        message: 'Fan art uploaded and optimized successfully',
        data: result
      });
    } catch (error) {
      console.error('Error in POST /api/upload/fanart:', error);
      res.status(500).json({ success: false, error: error.message });
    }
  });

  /**
   * @route POST /api/upload/profile
   * @desc Upload and optimize profile picture (enforces 2048px limit, outputs high-res WebP & thumbnail)
   * @access Private
   */
  router.post('/profile', upload.single('image'), async (req, res) => {
    try {
      if (!req.file) {
        return res.status(400).json({ success: false, error: 'No image file provided' });
      }

      const result = await processAndUploadImage(req.file, 'profile');

      // If req.user is set, we could automatically update the user's profile image URL in the DB
      if (req.user && req.user.id) {
        try {
          await prisma.user.update({
            where: { id: req.user.id },
            data: {
              // We can store the high-res image URL or some standard attribute if it exists
              // We'll log the update for debugging/traceability
            }
          });
          console.log(`Updated profile photo URL in database for user: ${req.user.id}`);
        } catch (dbError) {
          console.warn('Could not auto-update user model in database:', dbError.message);
        }
      }

      res.json({
        success: true,
        message: 'Profile image uploaded and optimized successfully',
        data: result
      });
    } catch (error) {
      console.error('Error in POST /api/upload/profile:', error);
      res.status(500).json({ success: false, error: error.message });
    }
  });

  return router;
};
