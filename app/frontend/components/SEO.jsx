import React, { useEffect } from 'react';

/**
 * Reusable SEO Component for dynamic head tag injection.
 * Supports:
 * - Page Title & Subtitle
 * - Meta descriptions and keywords
 * - Open Graph (Facebook/LinkedIn/Discord) for rich social embeds
 * - Twitter Cards for optimized tweet sharing
 * - Canonical URLs to prevent duplicate indexing issues
 * - Dynamic image attachment (prefers high-res WebP)
 * 
 * Includes an automated DOM fallback that dynamically modifies head tags, 
 * ensuring it works seamlessly with or without react-helmet configurations.
 */
export default function SEO({
  title = "SameSky Cosmos - Celestial Community",
  description = "Connect with peers through astrology, MBTI compatibility, and explore high-resolution fan art in our celestial community.",
  keywords = ["astrology", "mbti", "cosmos", "celestial", "fan art", "compatibility", "zodiac"],
  author = "SameSky Community",
  image = "/uploads/default-og.webp",
  canonicalUrl = typeof window !== 'undefined' ? window.location.href : '',
  type = "website",
  mbti = "",
  sunSign = ""
}) {
  const fullTitle = `${title} | SameSky`;

  useEffect(() => {
    if (typeof document === 'undefined') return;

    // 1. Update document title
    document.title = fullTitle;

    // Helper function to update or create meta tags
    const updateOrCreateMeta = (nameOrProperty, value, isProperty = false) => {
      if (!value) return;
      const attr = isProperty ? 'property' : 'name';
      let element = document.querySelector(`meta[${attr}="${nameOrProperty}"]`);
      
      if (!element) {
        element = document.createElement('meta');
        element.setAttribute(attr, nameOrProperty);
        document.head.appendChild(element);
      }
      element.setAttribute('content', value);
    };

    // Helper function to update or create link tags
    const updateOrCreateLink = (rel, href) => {
      if (!href) return;
      let element = document.querySelector(`link[rel="${rel}"]`);
      if (!element) {
        element = document.createElement('link');
        element.setAttribute('rel', rel);
        document.head.appendChild(element);
      }
      element.setAttribute('href', href);
    };

    // 2. Standard Search Engine Tags
    updateOrCreateMeta('description', description);
    updateOrCreateMeta('keywords', keywords.join(', '));
    updateOrCreateMeta('author', author);
    updateOrCreateMeta('robots', 'index, follow');

    // 3. Open Graph / Facebook Embeds
    updateOrCreateMeta('og:title', fullTitle, true);
    updateOrCreateMeta('og:description', description, true);
    updateOrCreateMeta('og:image', image, true);
    updateOrCreateMeta('og:url', canonicalUrl, true);
    updateOrCreateMeta('og:type', type, true);
    updateOrCreateMeta('og:site_name', 'SameSky', true);

    // 4. Twitter Cards
    updateOrCreateMeta('twitter:card', 'summary_large_image');
    updateOrCreateMeta('twitter:title', fullTitle);
    updateOrCreateMeta('twitter:description', description);
    updateOrCreateMeta('twitter:image', image);
    updateOrCreateMeta('twitter:creator', author);

    // 5. Canonical URL Link Tag
    updateOrCreateLink('canonical', canonicalUrl);

    // 6. Custom Cosmic Rich Metadata
    if (mbti) {
      updateOrCreateMeta('cosmos:mbti', mbti);
    }
    if (sunSign) {
      updateOrCreateMeta('cosmos:sunsign', sunSign);
    }

    // Return cleanup (optional, but good for single page navigations)
    return () => {
      // Keep basic tags but we could reset them if needed on unmount
    };
  }, [fullTitle, description, keywords, author, image, canonicalUrl, type, mbti, sunSign]);

  // If environment has react-helmet-async or react-helmet installed,
  // we also render the tags inside a <Helmet> wrapper to support server-side rendering (SSR)
  // when pre-rendering or indexing is performed.
  let HelmetComponent = null;
  try {
    const { Helmet } = require('react-helmet');
    HelmetComponent = Helmet;
  } catch (e) {
    try {
      const { Helmet } = require('react-helmet-async');
      HelmetComponent = Helmet;
    } catch (err) {
      // react-helmet is not available as npm package, we rely on our high-fidelity DOM injection fallback
    }
  }

  if (HelmetComponent) {
    const Helmet = HelmetComponent;
    return (
      <Helmet>
        <title>{fullTitle}</title>
        <meta name="description" content={description} />
        <meta name="keywords" content={keywords.join(', ')} />
        <meta name="author" content={author} />
        
        {/* Open Graph */}
        <meta property="og:title" content={fullTitle} />
        <meta property="og:description" content={description} />
        <meta property="og:image" content={image} />
        <meta property="og:url" content={canonicalUrl} />
        <meta property="og:type" content={type} />
        <meta property="og:site_name" content="SameSky" />

        {/* Twitter */}
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content={fullTitle} />
        <meta name="twitter:description" content={description} />
        <meta name="twitter:image" content={image} />
        <meta name="twitter:creator" content={author} />

        {/* Links */}
        <link rel="canonical" href={canonicalUrl} />

        {/* Custom Rich Metas */}
        {mbti && <meta name="cosmos:mbti" content={mbti} />}
        {sunSign && <meta name="cosmos:sunsign" content={sunSign} />}
      </Helmet>
    );
  }

  // Fallback to null (head is updated via our highly performant useEffect fallback)
  return null;
}
