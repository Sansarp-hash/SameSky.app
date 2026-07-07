const { S3Client } = require('@aws-sdk/client-s3');

// 1. Load AWS environment variables with clean fallbacks
const awsRegion = process.env.AWS_REGION || 'us-east-1';
const s3Bucket = process.env.AWS_S3_BUCKET;
const accessKeyId = process.env.AWS_ACCESS_KEY_ID;
const secretAccessKey = process.env.AWS_SECRET_ACCESS_KEY;
const cloudFrontDomain = process.env.CLOUDFRONT_DOMAIN || process.env.CLOUDFRONT_URL; // e.g. "https://d12345abcdef.cloudfront.net" or "d12345abcdef.cloudfront.net"

// 2. Validate configuration completeness
const isS3Configured = !!(accessKeyId && secretAccessKey && s3Bucket);

let s3Client = null;

if (isS3Configured) {
  try {
    s3Client = new S3Client({
      region: awsRegion,
      credentials: {
        accessKeyId,
        secretAccessKey,
      },
    });
    console.log('AWS S3 Client initialized in awsConfig service.');
  } catch (error) {
    console.error('Failed to initialize AWS S3 Client in awsConfig:', error);
  }
} else {
  console.warn('AWS S3 Credentials missing. S3-backed features will use local storage fallback.');
}

/**
 * Format a CloudFront domain securely to guarantee correct URL prefixing.
 */
const getFormattedCloudFrontDomain = () => {
  if (!cloudFrontDomain) return null;
  let domain = cloudFrontDomain.trim();
  if (!domain.startsWith('http://') && !domain.startsWith('https://')) {
    domain = `https://${domain}`;
  }
  return domain.endsWith('/') ? domain.slice(0, -1) : domain;
};

const formattedCloudFront = getFormattedCloudFrontDomain();

/**
 * Generates the public-facing URL for an asset key.
 * Prefers CloudFront URL if configured, otherwise falls back to standard S3 domain.
 *
 * @param {string} key The object key inside the S3 bucket
 * @returns {string} The public URL for the asset
 */
const getAssetUrl = (key) => {
  if (!key) return '';
  // Clean key (prevent leading slash duplication)
  const cleanKey = key.startsWith('/') ? key.slice(1) : key;

  if (formattedCloudFront) {
    return `${formattedCloudFront}/${cleanKey}`;
  }

  if (s3Bucket) {
    return `https://${s3Bucket}.s3.${awsRegion}.amazonaws.com/${cleanKey}`;
  }

  // Fallback to local file path if S3 is not configured
  return `/uploads/${cleanKey}`;
};

module.exports = {
  s3Client,
  isS3Configured,
  s3Bucket,
  awsRegion,
  getAssetUrl,
};
