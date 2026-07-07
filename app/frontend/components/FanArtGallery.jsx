import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import SEO from './SEO';
import useLazyLoad from '../hooks/useLazyLoad';

/**
 * CSS-based Watercolor illustration placeholder for image fallback
 */
const WatercolorPlaceholder = ({ title = "Celestial Creation" }) => {
  return (
    <div className="w-full min-h-[260px] bg-gradient-to-br from-slate-950 via-indigo-950/80 to-slate-900 flex flex-col items-center justify-center p-6 relative overflow-hidden select-none">
      {/* Animated, organic watercolor glowing blobs */}
      <div className="absolute top-1/4 left-1/4 w-36 h-36 rounded-full bg-indigo-500/20 blur-3xl mix-blend-screen animate-pulse" style={{ animationDuration: '6s' }} />
      <div className="absolute bottom-1/4 right-1/4 w-40 h-40 rounded-full bg-purple-600/25 blur-3xl mix-blend-screen animate-pulse" style={{ animationDuration: '8s' }} />
      <div className="absolute top-1/2 left-1/3 w-32 h-32 rounded-full bg-pink-500/15 blur-3xl mix-blend-screen animate-pulse" style={{ animationDuration: '10s' }} />

      {/* Constellation SVG art */}
      <motion.div
        initial={{ scale: 0.8, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ duration: 1.2 }}
        className="z-10 mb-4 text-indigo-400"
      >
        <svg className="w-14 h-14 filter drop-shadow-[0_0_12px_rgba(129,140,248,0.4)]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
          {/* Crescent Moon */}
          <path d="M12 3a6 6 0 0 0 9 9 9 9 0 1 1-9-9Z" />
          {/* Constellations */}
          <circle cx="12" cy="7" r="1.2" fill="currentColor" className="animate-ping" />
          <circle cx="8" cy="12" r="1" fill="currentColor" />
          <circle cx="16" cy="14" r="1" fill="currentColor" />
          <line x1="12" y1="7" x2="8" y2="12" stroke="currentColor" strokeWidth="0.5" strokeDasharray="2" />
          <line x1="8" y1="12" x2="16" y2="14" stroke="currentColor" strokeWidth="0.5" strokeDasharray="2" />
        </svg>
      </motion.div>

      {/* Fallback metadata text */}
      <div className="z-10 text-center px-4">
        <span className="text-[10px] uppercase font-bold tracking-[0.25em] text-indigo-400/80 block mb-1">
          SAMESKY COSMOS
        </span>
        <h4 className="text-xs font-semibold text-slate-300 truncate max-w-[200px]">
          {title}
        </h4>
        <p className="text-[10px] text-slate-500 mt-2 max-w-[180px] leading-relaxed italic">
          High-res connection paused.
        </p>
      </div>

      {/* Decorative inner frame */}
      <div className="absolute inset-4 border border-slate-800/40 rounded-xl pointer-events-none" />
      <div className="absolute inset-5 border border-dashed border-slate-800/25 rounded-xl pointer-events-none" />
    </div>
  );
};

/**
 * Custom LazyImage component that implements:
 * 1. Intersection Observer for lazy-loading (via useLazyLoad hook).
 * 2. Progressive blur-up loading (low-res thumbnail placeholder loaded first, 
 *    high-res loaded in the background and cross-faded).
 * 3. Graceful fallback rendering if an asset fails to load.
 */
const LazyImage = ({ src, thumbnailSrc, alt, className = '' }) => {
  const [isLoaded, setIsLoaded] = useState(false);
  const [hasError, setHasError] = useState(false);
  const [containerRef, isInView] = useLazyLoad({
    rootMargin: '120px', // Start preloading 120px before the image scrolls into view
    threshold: 0.01,
    freezeOnceVisible: true
  });

  // Preload high-res image once container is in view
  useEffect(() => {
    if (!isInView || !src) return;

    const img = new Image();
    img.src = src;
    img.onload = () => {
      setIsLoaded(true);
    };
    img.onerror = () => {
      setHasError(true);
    };
  }, [isInView, src]);

  if (hasError) {
    return <WatercolorPlaceholder title={alt} />;
  }

  return (
    <div
      ref={containerRef}
      className={`relative overflow-hidden bg-slate-900 ${className}`}
      style={{ aspectRatio: 'auto' }}
    >
      {/* Low-res Blurred Thumbnail */}
      {thumbnailSrc && (
        <img
          src={thumbnailSrc}
          alt={alt}
          onError={() => setHasError(true)}
          className={`w-full h-full object-cover transition-opacity duration-500 ease-out ${
            isLoaded ? 'opacity-0' : 'opacity-100'
          }`}
          style={{
            filter: 'blur(12px)',
            transform: 'scale(1.05)',
            position: isLoaded ? 'absolute' : 'relative',
            top: 0,
            left: 0,
          }}
        />
      )}

      {/* High-res Image */}
      {isInView && (
        <motion.img
          src={src}
          alt={alt}
          onError={() => setHasError(true)}
          initial={{ opacity: 0 }}
          animate={{ opacity: isLoaded ? 1 : 0 }}
          transition={{ duration: 0.5, ease: 'easeInOut' }}
          className="w-full h-auto object-cover block"
        />
      )}

      {/* Loading overlay spinner when not yet in view */}
      {!isInView && (
        <div className="absolute inset-0 flex items-center justify-center">
          <svg
            className="animate-spin h-6 w-6 text-indigo-500"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
        </div>
      )}
    </div>
  );
};

/**
 * Reusable FanArtGallery Component
 * Supports search, category filters, beautiful masonry column layout,
 * interactive hovering with metadata overlays, and an elegant details lightbox.
 */
export default function FanArtGallery({ 
  items = [], 
  onLike = null, 
  onShare = null,
  categories = ["All", "Digital Art", "Sketches", "Portraits", "Comics", "Concept Art"]
}) {
  const [selectedCategory, setSelectedCategory] = useState("All");
  const [searchQuery, setSearchQuery] = useState("");
  const [activeItem, setActiveItem] = useState(null);
  const [likedItems, setLikedItems] = useState({});

  // Filtered and searched items
  const filteredItems = items.filter((item) => {
    const matchesCategory =
      selectedCategory === "All" ||
      item.category?.toLowerCase() === selectedCategory.toLowerCase();
    
    const matchesSearch =
      item.title?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      item.author?.name?.toLowerCase().includes(searchQuery.toLowerCase()) ||
      item.description?.toLowerCase().includes(searchQuery.toLowerCase());

    return matchesCategory && matchesSearch;
  });

  const handleLike = (e, item) => {
    e.stopPropagation();
    const itemId = item.id;
    setLikedItems(prev => ({
      ...prev,
      [itemId]: !prev[itemId]
    }));
    if (onLike) onLike(item);
  };

  const handleShare = (e, item) => {
    e.stopPropagation();
    if (onShare) {
      onShare(item);
    } else if (navigator.share) {
      navigator.share({
        title: item.title,
        text: `Check out this amazing fan art by ${item.author?.name}!`,
        url: window.location.href,
      }).catch(console.error);
    } else {
      navigator.clipboard.writeText(item.imageUrl);
      alert("Image link copied to clipboard!");
    }
  };

  // Keyboard navigation for lightbox
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (!activeItem) return;
      if (e.key === 'Escape') {
        setActiveItem(null);
      } else if (e.key === 'ArrowRight' || e.key === 'ArrowLeft') {
        const currentIndex = filteredItems.findIndex(i => i.id === activeItem.id);
        if (currentIndex === -1) return;
        
        let nextIndex = e.key === 'ArrowRight' ? currentIndex + 1 : currentIndex - 1;
        if (nextIndex >= filteredItems.length) nextIndex = 0;
        if (nextIndex < 0) nextIndex = filteredItems.length - 1;
        
        setActiveItem(filteredItems[nextIndex]);
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [activeItem, filteredItems]);

  return (
    <div className="w-full min-h-screen bg-slate-950 text-slate-100 py-10 px-4 md:px-8 lg:px-12">
      {/* Dynamic SEO Tag Injection */}
      {activeItem ? (
        <SEO
          title={activeItem.title}
          description={activeItem.description || `Explore this gorgeous piece of celestial fan art by ${activeItem.author?.name || 'Anonymous'}.`}
          image={activeItem.imageUrl}
          author={activeItem.author?.name || 'Anonymous Creator'}
          mbti={activeItem.author?.mbtiResult || ''}
          sunSign={activeItem.author?.sunSign || ''}
          type="article"
        />
      ) : (
        <SEO
          title={selectedCategory === "All" ? "Fan Art & Cosmos Gallery" : `${selectedCategory} Creations`}
          description={searchQuery 
            ? `Searching the cosmic gallery for: "${searchQuery}". Explore illustrations, sketches, and celestial art.` 
            : `Browse ${selectedCategory.toLowerCase()} creations, digital illustrations, sketches, and celestial art uploaded by the SameSky community.`
          }
          keywords={selectedCategory === "All" ? undefined : ["astrology", "mbti", selectedCategory.toLowerCase(), "cosmos"]}
        />
      )}

      {/* Header section with rich dark theme aesthetics */}
      <div className="max-w-7xl mx-auto mb-10 text-center">
        <motion.h1 
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
          className="text-4xl md:text-5xl font-extrabold tracking-tight bg-gradient-to-r from-indigo-400 via-purple-400 to-pink-500 bg-clip-text text-transparent mb-4"
        >
          Fan Art & Cosmos Gallery
        </motion.h1>
        <motion.p 
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.2, duration: 0.6 }}
          className="text-slate-400 max-w-2xl mx-auto text-base md:text-lg"
        >
          Explore beautiful, optimized high-resolution creations uploaded by our community.
          Filter by categories, search for artists, and interact with the cosmos.
        </motion.p>
      </div>

      {/* Control panel (Filter Tabs & Search Bar) */}
      <div className="max-w-7xl mx-auto mb-8 flex flex-col md:flex-row gap-4 items-center justify-between border-b border-slate-800 pb-6">
        
        {/* Horizontal Category Slider */}
        <div className="flex items-center gap-2 overflow-x-auto w-full md:w-auto no-scrollbar scroll-smooth py-2">
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-all duration-300 relative ${
                selectedCategory === cat 
                  ? 'text-slate-100' 
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900/60'
              }`}
            >
              {selectedCategory === cat && (
                <motion.div
                  layoutId="activeCategory"
                  className="absolute inset-0 bg-indigo-600 rounded-full -z-10"
                  transition={{ type: "spring", stiffness: 300, damping: 30 }}
                />
              )}
              {cat}
            </button>
          ))}
        </div>

        {/* Live Search Input */}
        <div className="relative w-full md:w-80">
          <span className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-slate-500">
            {/* Search Icon SVG */}
            <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </span>
          <input
            type="text"
            placeholder="Search art, artists, tags..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-slate-900/80 border border-slate-800 rounded-full py-2.5 pl-10 pr-4 text-sm text-slate-100 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-transparent transition-all duration-300"
          />
          {searchQuery && (
            <button
              onClick={() => setSearchQuery("")}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-slate-500 hover:text-slate-300"
            >
              <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          )}
        </div>
      </div>

      {/* Main Gallery Masonry Section */}
      <div className="max-w-7xl mx-auto">
        <AnimatePresence mode="popLayout">
          {filteredItems.length === 0 ? (
            <motion.div 
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              className="text-center py-24 bg-slate-900/40 border border-slate-900 rounded-2xl"
            >
              <svg className="mx-auto h-12 w-12 text-slate-600 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <h3 className="text-lg font-medium text-slate-300">No artwork found</h3>
              <p className="text-slate-500 text-sm mt-1">Try adjusting your filters or search keywords.</p>
            </motion.div>
          ) : (
            <motion.div 
              layout
              className="columns-1 sm:columns-2 md:columns-3 lg:columns-4 gap-5 space-y-5"
            >
              {filteredItems.map((item, index) => {
                const isLiked = !!likedItems[item.id];
                return (
                  <motion.div
                    key={item.id}
                    layoutId={`gallery-card-${item.id}`}
                    initial={{ opacity: 0, y: 30 }}
                    animate={{ opacity: 1, y: 0 }}
                    exit={{ opacity: 0, scale: 0.9 }}
                    transition={{ 
                      duration: 0.4, 
                      delay: Math.min(index * 0.05, 0.4),
                      layout: { type: "spring", stiffness: 300, damping: 30 } 
                    }}
                    onClick={() => setActiveItem(item)}
                    className="break-inside-avoid relative overflow-hidden rounded-2xl group border border-slate-900/80 bg-slate-900/40 cursor-pointer hover:shadow-2xl hover:shadow-indigo-500/10 hover:border-indigo-500/20 transition-all duration-300"
                  >
                    
                    {/* Lazy and Optimized Image with progressive Blur-up load */}
                    <LazyImage
                      src={item.imageUrl}
                      thumbnailSrc={item.thumbnailUrl}
                      alt={item.title}
                      className="w-full h-auto"
                    />

                    {/* Gradient Overlay & Metadata showing on hover */}
                    <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/40 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex flex-col justify-end p-5">
                      
                      {/* Tags / Badges */}
                      <div className="flex flex-wrap gap-1.5 mb-2">
                        {item.category && (
                          <span className="text-[10px] uppercase font-bold tracking-wider bg-indigo-600/80 text-white px-2 py-0.5 rounded-full backdrop-blur-sm">
                            {item.category}
                          </span>
                        )}
                        {item.author?.mbtiResult && (
                          <span className="text-[10px] uppercase font-bold tracking-wider bg-emerald-600/80 text-white px-2 py-0.5 rounded-full backdrop-blur-sm">
                            {item.author.mbtiResult}
                          </span>
                        )}
                      </div>

                      {/* Title & Artist */}
                      <h3 className="text-lg font-bold text-slate-100 truncate mb-0.5">
                        {item.title}
                      </h3>
                      <p className="text-sm text-slate-300 font-medium truncate mb-3">
                        by <span className="text-indigo-400 group-hover:underline">{item.author?.name || "Anonymous"}</span>
                      </p>

                      {/* Interaction Actions */}
                      <div className="flex items-center justify-between pt-3 border-t border-slate-800/60">
                        <div className="flex gap-3">
                          {/* Like Button */}
                          <button
                            onClick={(e) => handleLike(e, item)}
                            className={`flex items-center gap-1.5 text-xs font-semibold px-2.5 py-1.5 rounded-full backdrop-blur-sm transition-all duration-200 ${
                              isLiked 
                                ? 'bg-rose-500/20 text-rose-400 hover:bg-rose-500/30' 
                                : 'bg-slate-800/60 text-slate-300 hover:text-white hover:bg-slate-700/60'
                            }`}
                          >
                            <svg 
                              className={`h-4.5 w-4.5 transition-transform duration-200 active:scale-125 ${isLiked ? 'fill-current' : 'none'}`}
                              stroke="currentColor" 
                              fill={isLiked ? "currentColor" : "none"} 
                              viewBox="0 0 24 24"
                            >
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                            </svg>
                            <span>{item.upvotes + (isLiked ? 1 : 0)}</span>
                          </button>

                          {/* Share Button */}
                          <button
                            onClick={(e) => handleShare(e, item)}
                            className="p-1.5 rounded-full bg-slate-800/60 hover:bg-slate-700/60 text-slate-300 hover:text-white backdrop-blur-sm transition-all duration-200"
                            title="Copy link"
                          >
                            <svg className="h-4.5 w-4.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8.684 10.742l4.828-2.414m0 0a3 3 0 10-4.828-2.414m4.828 2.414a3 3 0 005.196 3.016m-5.196-3.016a3 3 0 00-3.016 5.196m4.828-2.414l-4.828 2.414m0 0a3 3 0 104.828 2.414" />
                            </svg>
                          </button>
                        </div>

                        {/* Expand Icon */}
                        <span className="text-slate-400 group-hover:text-indigo-400 transition-colors duration-200">
                          <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 8V4m0 0h4M4 4l5 5m11-1V4m0 0h-4m4 0l-5 5M4 16v4m0 0h4m-4 0l5-5m11 5l-5-5m5 5v-4m0 4h-4" />
                          </svg>
                        </span>
                      </div>

                    </div>
                  </motion.div>
                );
              })}
            </motion.div>
          )}
        </AnimatePresence>
      </div>

      {/* Lightbox Modal (Full Resolution Details View) */}
      <AnimatePresence>
        {activeItem && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 md:p-6 lg:p-10">
            {/* Backdrop */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setActiveItem(null)}
              className="absolute inset-0 bg-black/95 backdrop-blur-md"
            />

            {/* Modal Body */}
            <motion.div
              layoutId={`gallery-card-${activeItem.id}`}
              transition={{ type: "spring", damping: 25, stiffness: 180 }}
              className="relative w-full max-w-6xl max-h-[90vh] bg-slate-900 border border-slate-800 rounded-3xl overflow-hidden shadow-2xl flex flex-col md:flex-row z-10"
            >
              
              {/* Close Button */}
              <button
                onClick={() => setActiveItem(null)}
                className="absolute top-4 right-4 z-20 p-2.5 rounded-full bg-slate-950/80 hover:bg-slate-950 text-slate-300 hover:text-white border border-slate-800/80 transition-all duration-200"
              >
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>

              {/* Left Side: High-res Image Display */}
              <div className="flex-1 bg-slate-950 flex items-center justify-center min-h-[40vh] md:min-h-0 overflow-hidden relative">
                <img
                  src={activeItem.imageUrl}
                  alt={activeItem.title}
                  className="max-w-full max-h-[85vh] object-contain block"
                  loading="eager"
                />
              </div>

              {/* Right Side: Artwork Info & Meta Panels */}
              <div className="w-full md:w-[380px] p-6 md:p-8 flex flex-col justify-between border-t md:border-t-0 md:border-l border-slate-800 bg-slate-900/90 overflow-y-auto max-h-[45vh] md:max-h-none">
                <div>
                  
                  {/* Category & Date */}
                  <div className="flex items-center justify-between mb-4">
                    <span className="text-xs uppercase font-bold tracking-widest bg-indigo-600 text-indigo-50 px-3 py-1 rounded-full">
                      {activeItem.category || "Fan Art"}
                    </span>
                    <span className="text-xs text-slate-500 font-medium">
                      {activeItem.createdAt ? new Date(activeItem.createdAt).toLocaleDateString(undefined, {
                        year: 'numeric', month: 'long', day: 'numeric'
                      }) : "Recent"}
                    </span>
                  </div>

                  {/* Title & Author */}
                  <h2 className="text-2xl font-black text-slate-100 tracking-tight mb-2">
                    {activeItem.title}
                  </h2>

                  {/* Creator Card */}
                  <div className="flex items-center gap-3 bg-slate-950/50 p-3 rounded-2xl border border-slate-800 mb-6">
                    <div className="h-10 w-10 rounded-full bg-gradient-to-tr from-indigo-500 to-purple-500 flex items-center justify-center text-sm font-bold text-white uppercase shadow-md">
                      {(activeItem.author?.name || "A").substring(0, 2)}
                    </div>
                    <div>
                      <h4 className="text-sm font-bold text-slate-200">
                        {activeItem.author?.name || "Anonymous"}
                      </h4>
                      
                      {/* Creator Cosmic Details */}
                      <div className="flex gap-2 mt-0.5">
                        {activeItem.author?.mbtiResult && (
                          <span className="text-[9px] uppercase font-bold tracking-wider text-emerald-400 bg-emerald-950/40 border border-emerald-900/40 px-1.5 py-0.2 rounded">
                            {activeItem.author.mbtiResult}
                          </span>
                        )}
                        {activeItem.author?.sunSign && (
                          <span className="text-[9px] uppercase font-bold tracking-wider text-amber-400 bg-amber-950/40 border border-amber-900/40 px-1.5 py-0.2 rounded">
                            ✨ {activeItem.author.sunSign}
                          </span>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Description */}
                  <div className="mb-6">
                    <h5 className="text-xs uppercase font-extrabold tracking-widest text-slate-500 mb-2">
                      Description
                    </h5>
                    <p className="text-slate-300 text-sm leading-relaxed whitespace-pre-line bg-slate-950/20 p-3 rounded-xl border border-slate-800/40">
                      {activeItem.description || "The creator has left this artwork without a description, letting the art speak of the cosmos on its own."}
                    </p>
                  </div>

                  {/* Cosmic Tags */}
                  {activeItem.tags && activeItem.tags.length > 0 && (
                    <div className="mb-6">
                      <h5 className="text-xs uppercase font-extrabold tracking-widest text-slate-500 mb-2">
                        Tags
                      </h5>
                      <div className="flex flex-wrap gap-1.5">
                        {activeItem.tags.map(tag => (
                          <span key={tag} className="text-xs bg-slate-950 text-slate-400 border border-slate-800 px-2.5 py-1 rounded-lg">
                            #{tag}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}

                </div>

                {/* Lightbox Footer Actions */}
                <div className="pt-6 border-t border-slate-800 flex items-center justify-between gap-4 mt-6">
                  
                  {/* Upvote/Like Interaction */}
                  <button
                    onClick={(e) => handleLike(e, activeItem)}
                    className={`flex-1 flex items-center justify-center gap-2 font-bold py-3 px-4 rounded-2xl border transition-all duration-300 ${
                      likedItems[activeItem.id]
                        ? 'bg-rose-500 text-white border-rose-600 shadow-lg shadow-rose-500/20 hover:bg-rose-600'
                        : 'bg-slate-950 text-slate-300 border-slate-800 hover:text-white hover:bg-slate-900 hover:border-slate-700'
                    }`}
                  >
                    <svg 
                      className={`h-5 w-5 transition-transform duration-200 active:scale-125 ${likedItems[activeItem.id] ? 'fill-current' : 'none'}`}
                      stroke="currentColor" 
                      fill={likedItems[activeItem.id] ? "currentColor" : "none"} 
                      viewBox="0 0 24 24"
                    >
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                    </svg>
                    <span>{likedItems[activeItem.id] ? 'Loved!' : 'Love Art'}</span>
                  </button>

                  {/* Share Icon */}
                  <button
                    onClick={(e) => handleShare(e, activeItem)}
                    className="p-3.5 rounded-2xl bg-slate-950 hover:bg-slate-900 border border-slate-800 hover:border-slate-700 text-slate-300 hover:text-white transition-all duration-300"
                    title="Share Artwork"
                  >
                    <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8.684 10.742l4.828-2.414m0 0a3 3 0 10-4.828-2.414m4.828 2.414a3 3 0 005.196 3.016m-5.196-3.016a3 3 0 00-3.016 5.196m4.828-2.414l-4.828 2.414m0 0a3 3 0 104.828 2.414" />
                    </svg>
                  </button>

                  {/* Direct WebP Download Button */}
                  <a
                    href={activeItem.imageUrl}
                    download={`${activeItem.title || 'fan-art'}.webp`}
                    target="_blank"
                    rel="noreferrer"
                    className="p-3.5 rounded-2xl bg-slate-950 hover:bg-slate-900 border border-slate-800 hover:border-slate-700 text-slate-300 hover:text-white transition-all duration-300"
                    title="Download High-Res WebP"
                  >
                    <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                    </svg>
                  </a>

                </div>
              </div>

            </motion.div>
          </div>
        )}
      </AnimatePresence>

    </div>
  );
}
