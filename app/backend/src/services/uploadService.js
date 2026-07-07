const sharp = require('sharp');
const { PutObjectCommand } = require('@aws-sdk/client-s3');
const { s3Client, isS3Configured, s3Bucket, getAssetUrl } = require('./awsConfig');
const multer = require('multer');
const path = require('path');
const fs = require('fs');

// Initialize Multer memory storage
const storage = multer.memoryStorage();
const upload = multer({
  storage: storage,
  limits: {
    fileSize: 15 * 1024 * 1024 // 15MB limit
  },
  fileFilter: (req, file, cb) => {
    const filetypes = /jpeg|jpg|png|webp|gif/;
    const mimetype = filetypes.test(file.mimetype);
    const extname = filetypes.test(path.extname(file.originalname).toLowerCase());

    if (mimetype && extname) {
      return cb(null, true);
    }
    cb(new Error('Only images (jpeg, jpg, png, webp, gif) are allowed!'));
  }
});

// Ensure local uploads folder exists
const uploadsDir = path.join(__dirname, '../../public/uploads');
if (!fs.existsSync(uploadsDir)) {
  fs.mkdirSync(uploadsDir, { recursive: true });
}

/**
 * Process image buffer using sharp:
 * 1. Resize to max 2048px width (high-resolution constraint)
 * 2. Optimize and convert to WebP
 * 3. Generate a WebP thumbnail (max 300px width)
 */
const processAndUploadImage = async (file, folder = 'general') => {
  const timestamp = Date.now();
  const randomSuffix = Math.round(Math.random() * 1e9);
  const baseFilename = `${folder}/${timestamp}-${randomSuffix}`;
  const mainFilename = `${baseFilename}.webp`;
  const thumbFilename = `${baseFilename}-thumb.webp`;

  try {
    // 1. Process Main Image: Enforce max 2048px width and compress
    const mainBuffer = await sharp(file.buffer)
      .resize({
        width: 2048,
        withoutEnlargement: true, // Do not upscale if smaller
        fit: 'inside'
      })
      .webp({ quality: 85 })
      .toBuffer();

    // 2. Process Thumbnail: Optimize and generate WebP (300px width max)
    const thumbBuffer = await sharp(file.buffer)
      .resize({
        width: 300,
        withoutEnlargement: true,
        fit: 'inside'
      })
      .webp({ quality: 80 })
      .toBuffer();

    let imageUrl = '';
    let thumbnailUrl = '';

    if (isS3Configured && s3Client) {
      // Upload Main Image to S3
      await s3Client.send(new PutObjectCommand({
        Bucket: s3Bucket,
        Key: mainFilename,
        Body: mainBuffer,
        ContentType: 'image/webp',
        ACL: 'public-read'
      }));

      // Upload Thumbnail to S3
      await s3Client.send(new PutObjectCommand({
        Bucket: s3Bucket,
        Key: thumbFilename,
        Body: thumbBuffer,
        ContentType: 'image/webp',
        ACL: 'public-read'
      }));

      imageUrl = getAssetUrl(mainFilename);
      thumbnailUrl = getAssetUrl(thumbFilename);
    } else {
      // Local fallback
      const localFolderDir = path.join(uploadsDir, folder);
      if (!fs.existsSync(localFolderDir)) {
        fs.mkdirSync(localFolderDir, { recursive: true });
      }

      const localMainPath = path.join(uploadsDir, mainFilename);
      const localThumbPath = path.join(uploadsDir, thumbFilename);

      fs.writeFileSync(localMainPath, mainBuffer);
      fs.writeFileSync(localThumbPath, thumbBuffer);

      imageUrl = `/uploads/${mainFilename}`;
      thumbnailUrl = `/uploads/${thumbFilename}`;
    }

    return {
      success: true,
      imageUrl,
      thumbnailUrl,
      filename: path.basename(mainFilename),
      thumbnailFilename: path.basename(thumbFilename),
      storageType: isS3Configured ? 's3' : 'local'
    };
  } catch (error) {
    console.error('Error in processAndUploadImage service:', error);
    throw new Error(`Failed to process or upload image: ${error.message}`);
  }
};

module.exports = {
  upload,
  processAndUploadImage,
  isS3Configured
};
