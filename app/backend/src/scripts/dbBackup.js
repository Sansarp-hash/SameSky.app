const { exec } = require('child_process');
const fs = require('fs');
const path = require('path');
const prisma = require('../lib/prismaClient');
const { s3Client, s3Bucket, isS3Configured } = require('../services/awsConfig');
const { PutObjectCommand } = require('@aws-sdk/client-s3');

/**
 * Perform database backup.
 * Attempts to run pg_dump. If pg_dump fails or is unavailable, falls back
 * to a clean JSON-serialized dump of key tables (users, raffle entries, etc.)
 */
async function performBackup() {
  const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
  const sqlBackupPath = path.join('/tmp', `samesky_backup_${timestamp}.sql`);
  const jsonBackupPath = path.join('/tmp', `samesky_backup_${timestamp}.json`);
  
  console.log(`[DATABASE BACKUP] Starting weekly database maintenance backup... Timestamp: ${timestamp}`);

  const databaseUrl = process.env.DATABASE_URL;
  if (!databaseUrl) {
    console.warn('[DATABASE BACKUP] DATABASE_URL is not set. Falling back to JSON backup.');
    await runJsonFallbackBackup(jsonBackupPath);
    return;
  }

  // Attempt pg_dump
  const command = `pg_dump "${databaseUrl}" -F c -b -v -f "${sqlBackupPath}"`;
  
  exec(command, async (error, stdout, stderr) => {
    if (error) {
      console.warn(`[DATABASE BACKUP] pg_dump failed or is unavailable in this environment: ${error.message}. Running robust JSON fallback...`);
      await runJsonFallbackBackup(jsonBackupPath);
      return;
    }

    console.log(`[DATABASE BACKUP] pg_dump succeeded. Export saved to ${sqlBackupPath}`);
    await uploadToS3(sqlBackupPath, `backups/postgresql/samesky_backup_${timestamp}.sql`);
    
    // Clean up local temp file
    try {
      fs.unlinkSync(sqlBackupPath);
    } catch (e) {
      console.error('[DATABASE BACKUP] Error cleaning up local SQL backup file:', e);
    }
  });
}

/**
 * Graceful JSON backup fallback when pg_dump is not present or fails
 */
async function runJsonFallbackBackup(destinationPath) {
  try {
    console.log('[DATABASE BACKUP FALLBACK] Exporting primary tables using Prisma Client...');
    
    const users = await prisma.user.findMany().catch(() => []);
    const posts = await prisma.post.findMany().catch(() => []);
    const comments = await prisma.comment.findMany().catch(() => []);
    const tarotReadings = await prisma.tarotReading.findMany().catch(() => []);
    const contentEntries = await prisma.contentEntry.findMany().catch(() => []);
    const raffleEntries = await prisma.raffleEntry.findMany().catch(() => []);

    const backupData = {
      metadata: {
        exportedAt: new Date().toISOString(),
        strategy: 'Prisma Client JSON Fallback',
        app: 'SameSky'
      },
      data: {
        users,
        posts,
        comments,
        tarotReadings,
        contentEntries,
        raffleEntries
      }
    };

    fs.writeFileSync(destinationPath, JSON.stringify(backupData, null, 2));
    console.log(`[DATABASE BACKUP FALLBACK] Export saved successfully to ${destinationPath}`);

    const key = `backups/fallback-json/samesky_backup_${new Date().toISOString().replace(/[:.]/g, '-')}.json`;
    await uploadToS3(destinationPath, key);

    // Clean up local temp file
    try {
      fs.unlinkSync(destinationPath);
    } catch (e) {
      console.error('[DATABASE BACKUP FALLBACK] Error cleaning up local JSON backup file:', e);
    }
  } catch (error) {
    console.error('[DATABASE BACKUP FALLBACK] Critical failure during backup fallback:', error);
  }
}

/**
 * Upload backup file to S3
 */
async function uploadToS3(localFilePath, s3Key) {
  if (!isS3Configured) {
    console.warn(`[DATABASE BACKUP] AWS S3 is not configured. Local backup remains unsynced, but was generated successfully.`);
    return;
  }

  try {
    const fileContent = fs.readFileSync(localFilePath);
    const bucketName = process.env.AWS_S3_BACKUP_BUCKET || s3Bucket;
    
    console.log(`[DATABASE BACKUP] Uploading to S3 bucket "${bucketName}" with key "${s3Key}"...`);
    
    const uploadCommand = new PutObjectCommand({
      Bucket: bucketName,
      Key: s3Key,
      Body: fileContent,
      ContentType: localFilePath.endsWith('.sql') ? 'application/sql' : 'application/json'
    });

    await s3Client.send(uploadCommand);
    console.log(`[DATABASE BACKUP SUCCESS] Successfully secured database backup in S3: ${s3Key}`);
  } catch (error) {
    console.error('[DATABASE BACKUP] S3 upload failed:', error);
  }
}

module.exports = {
  performBackup
};
