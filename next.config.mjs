import path from "node:path"

/** @type {import('next').NextConfig} */
const nextConfig = {
  typescript: { ignoreBuildErrors: true },
  images: { unoptimized: true },
  turbopack: {
    root: path.dirname(new URL(import.meta.url).pathname),
  },
}

export default nextConfig
