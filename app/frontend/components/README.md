# 🌌 FanArtGallery React Component

An extremely polished, highly interactive, and optimized **Fan Art & Media Gallery** component built in React using **Jetpack-style Tailwind styling**, **`framer-motion` animations**, and a performant **Intersection Observer lazy-loader**.

Designed to handle high-resolution imagery efficiently by leveraging the progressive **blur-up technique**—loading lightweight WebP thumbnails (`thumbnailUrl`) first, then loading the high-resolution original image (`imageUrl`) in the background and transitioning seamlessly.

---

## 🚀 Key Features

- **True CSS Masonry Grid**: Highly fluid and responsive layout which prevents heavy resize recalculations in JavaScript.
- **`framer-motion` Layout Transitions**: Smooth item rearrangement when filtering categories or entering search terms.
- **Intersection Observer Lazy-Loading**: Delays image initialization until the item is close to the viewport.
- **Progressive "Blur-Up" Loading**: Smoothly cross-fades from a blurry low-res thumbnail (`300px` WebP) to the original high-resolution WebP image (`2048px` max limit).
- **Interactive Hover overlays**: Shows item category tags, creator details, MBTI/Astrology signs, and stats directly.
- **Full-Featured Lightbox Modal**: Expanding transitions, support for arrow-key navigation (`Left`, `Right`, `Escape` to close), details panel, description block, and interactive actions (Loving/Liking, Sharing, and Downloading High-Res).
- **Modern Clean Design**: Hand-crafted inline SVGs (zero third-party icon packages needed) matching a cosmic slate/indigo design system.

---

## 📦 Installation & Dependencies

To use this component in your React application, ensure you have `framer-motion` and `tailwind` configured, then install the required package:

```bash
npm install framer-motion
```

*Note: The component is fully self-contained. It generates clean SVG icons inline, meaning you do not need `lucide-react` or `@heroicons/react`.*

---

## 🔌 API Integration Example

Here is a full integration example showing how to tie this frontend React gallery to our Node.js `/api/content` and `/api/upload` backend services.

```jsx
import React, { useState, useEffect } from 'react';
import FanArtGallery from './components/FanArtGallery';

export default function App() {
  const [artworks, setArtworks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);

  // 1. Fetch artwork feed from GET /api/content
  useEffect(() => {
    fetch('/api/content?limit=24&page=1', {
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}` // Authenticated request
      }
    })
      .then(res => res.json())
      .then(result => {
        if (result.success) {
          // Map backend Posts with media content to our gallery model
          const mapped = result.data.map(post => ({
            id: post.id,
            title: post.title || 'Untitled Creation',
            description: post.content,
            imageUrl: post.imageUrl || '/uploads/placeholder-main.webp',
            thumbnailUrl: post.thumbnailUrl || post.imageUrl || '/uploads/placeholder-thumb.webp',
            category: post.category || 'Digital Art',
            upvotes: post.upvotes || 0,
            createdAt: post.createdAt,
            author: {
              name: post.author?.name || 'Anonymous Creator',
              mbtiResult: post.author?.mbtiResult,
              sunSign: post.author?.sunSign
            },
            tags: post.tags || ['cosmos', 'community']
          }));
          setArtworks(mapped);
        }
      })
      .catch(err => console.error('Failed to load gallery content:', err))
      .finally(() => setLoading(false));
  }, []);

  // 2. Handle Like trigger
  const handleLike = async (artwork) => {
    try {
      await fetch(`/api/community/posts/${artwork.id}/upvote`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
    } catch (err) {
      console.error('Failed to register upvote:', err);
    }
  };

  // 3. Handle Art Upload using Multipart form (POST /api/upload/fanart)
  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('image', file);

    setUploading(true);
    try {
      const response = await fetch('/api/upload/fanart', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: formData
      });
      const result = await response.json();
      
      if (result.success) {
        alert('Artwork uploaded and optimized successfully! (Output format: WebP + Thumbnail)');
        // Reload or update list
        window.location.reload();
      } else {
        alert(`Upload failed: ${result.error}`);
      }
    } catch (err) {
      console.error('Upload Error:', err);
    } finally {
      setUploading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-slate-950 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-indigo-500"></div>
      </div>
    );
  }

  return (
    <div className="bg-slate-950 min-h-screen">
      {/* Upload Portal */}
      <div className="max-w-7xl mx-auto px-4 md:px-8 lg:px-12 pt-8 flex justify-end">
        <label className={`cursor-pointer inline-flex items-center gap-2 font-bold px-6 py-3 rounded-full bg-gradient-to-r from-indigo-500 to-purple-600 text-white shadow-lg shadow-indigo-500/20 hover:from-indigo-600 hover:to-purple-700 transition-all ${uploading ? 'opacity-50 pointer-events-none' : ''}`}>
          <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
          </svg>
          {uploading ? 'Uploading...' : 'Submit Creative Art'}
          <input type="file" accept="image/*" onChange={handleFileUpload} className="hidden" />
        </label>
      </div>

      {/* Render Gallery */}
      <FanArtGallery 
        items={artworks} 
        onLike={handleLike} 
      />
    </div>
  );
}
```

---

## 💎 Design and Optimization Details

### Progressive Blur-Up Load
When you build the backend with the optimization features (such as `sharp` compression), you generate two image assets:
1. High-resolution optimized image (`/uploads/fanart/...webp` constrained to **2048px width max**).
2. Small, low-fidelity, highly compressed thumbnail (`/uploads/fanart/...-thumb.webp` constrained to **300px width max**).

The `LazyImage` wrapper reads the `thumbnailUrl` instantly and loads it as a blurred placeholder image. As the heavy original image loads in the background of the browser, it transitions gracefully using `framer-motion`'s `animate={{ opacity: isLoaded ? 1 : 0 }}`.

### Performance & Memory
- Standard React memory leaks on fast scrolls are eliminated by using the robust custom `useLazyLoad` hook which cleanly mounts and unmounts the `IntersectionObserver` on lifecycle completion.
- Uses `columns` instead of complex `transform: translate3d` math, allowing standard hardware-accelerated rendering inside browser agents.

### ⚓ Reusable `useLazyLoad` Custom Hook
To prevent UI lag when rendering media grids, we extracted the observer logic into a custom `useLazyLoad` hook (`/app/frontend/hooks/useLazyLoad.js`):
- **Footprint**:
  ```javascript
  const [ref, isVisible] = useLazyLoad({
    root: null,
    rootMargin: '120px',
    threshold: 0.01,
    freezeOnceVisible: true
  });
  ```
- **Capabilities**:
  - Automatically handles cleanup (`observer.disconnect()`) upon component unmounting.
  - Implements the `freezeOnceVisible` feature which stops observing the DOM element as soon as it enters the viewport, reducing idle event listeners and maximizing frame rates.
  - Includes standard fallback mechanisms for environments/browsers lacking native `IntersectionObserver` support.

### 🌐 Dynamic SEO & Metadata Injection
The gallery integrates with the `<SEO>` tag injection engine (`SEO.jsx`). It manages search engine indexing and social link embeds (such as Open Graph for Facebook/Discord/Slack and Twitter Cards) dynamically at runtime:
- **Default Grid View**: Serves generalized search tags matching the selected creative filter category (e.g., *Digital Art Creations*, *Sketches*).
- **Expanded Lightbox View**: Instantly updates title, description, and high-resolution thumbnail tags to match the selected artwork (`imageUrl`). This ensures that when deep links or direct links to artworks are shared, the embedding crawlers capture the precise title, thumbnail, creator name, MBTI personality, and astrological sun sign.
- **Robust SSR & SPA Engine**: Automatically employs `react-helmet` or `react-helmet-async` for SEO indexing while gracefully falling back to a direct head DOM injection lifecycle if libraries are not configured.

### 🎨 Branded Watercolor CSS/SVG Fallbacks
To secure the visual layout against missing assets, CDN outages, or S3 permission issues:
- **`WatercolorPlaceholder` Component**: Integrated directly inside the image loader.
- **Visuals**: Employs an organic, animated multi-blob watercolor gradient backdrop coupled with a vector crescent moon constellation SVG overlay, giving it an elegant, bespoke celestial look instead of a generic broken-image placeholder.
- **Metadata Integration**: Dynamically showcases the original media title within the stylized fallback frame.

### ⚙️ Secure AWS & CloudFront Delivery (`services/awsConfig.js`)
- **Centralized Config**: Unifies AWS S3 client instantiation with secure environment variable bindings.
- **CDN Acceleration**: Auto-detects and formats custom `CLOUDFRONT_DOMAIN` links to wrap public asset tags in production, bypassing slow S3 transfers and enforcing low-latency asset delivery.

### ⚡ Database Singleton & Performance Logging (`lib/prismaClient.js`)
- **Singleton Pool**: Prevents Database connection pool starvation during high-concurrency event loops.
- **Middleware Telemetry**: Automatically intercepts query executions, measuring execution times. Any query exceeding `200ms` automatically logs a highly visible warning `[PRISMA SLOW QUERY ALERT]` with trace metadata to identify potential production bottlenecks.

### 🔒 Endpoint Rate Limiting (`middleware/rateLimiter.js`)
- **Brute-Force Guard**: Leverages `express-rate-limit` to restrict rapid-fire authentication endpoints (registration and logins are limited strictly to `5 attempts per 15 minutes`).
- **Submission Protection**: Raffle entry endpoints are bounded at `3 requests per 15 minutes` to protect the prize ledger against rapid scripting bots.
- **Distributed API Throttle**: General routes have a blanket limiter of `100 requests per 15 minutes` to protect server resources under heavy load.
