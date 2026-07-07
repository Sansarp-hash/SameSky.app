# --- Stage 1: Build & Dependencies ---
FROM node:22-slim AS builder

# Install openssl for Prisma CLI
RUN apt-get update && apt-get install -y openssl && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src/app

# Copy package definitions and prisma schema first for better caching
COPY package*.json ./
COPY prisma ./prisma/

# Install all dependencies (needed for Prisma generation)
RUN npm ci

# Generate Prisma Client
RUN npx prisma generate --schema=./prisma/schema.prisma

# --- Stage 2: Production Runner ---
FROM node:22-slim AS runner

# Install essential runtime system dependencies
RUN apt-get update && apt-get install -y openssl ca-certificates && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src/app
ENV NODE_ENV=production

# Pre-create directory for local uploads and set ownership to node user
RUN mkdir -p /usr/src/app/public/uploads && chown -R node:node /usr/src/app

# Copy built node_modules (including generated Prisma Client) from builder
COPY --from=builder --chown=node:node /usr/src/app/node_modules ./node_modules

# Copy package files and source code
COPY --chown=node:node package*.json ./
COPY --chown=node:node prisma ./prisma/
COPY --chown=node:node src ./src

# Expose the application port
EXPOSE 3000

# Switch to the non-privileged node user for security
USER node

# Run the application
CMD [ "npm", "start" ]
