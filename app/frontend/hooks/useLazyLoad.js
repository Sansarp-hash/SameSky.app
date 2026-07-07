import { useState, useEffect, useRef } from 'react';

/**
 * A highly robust and reusable Intersection Observer React hook for lazy-loading.
 * 
 * @param {Object} options Configuration options for the IntersectionObserver
 * @param {string} options.rootMargin Margin around the root (default: '100px' to load slightly before coming into view)
 * @param {number|number[]} options.threshold Visibility threshold (default: 0.01)
 * @param {Element|null} options.root Root element for viewport check (default: null / browser viewport)
 * @param {boolean} options.freezeOnceVisible Stop observing once the element is visible (default: true)
 * @returns {[React.RefObject, boolean]} A tuple containing the ref to attach to the target element and a boolean visibility state.
 */
export default function useLazyLoad({
  root = null,
  rootMargin = '100px',
  threshold = 0.01,
  freezeOnceVisible = true
} = {}) {
  const [isVisible, setIsVisible] = useState(false);
  const elementRef = useRef(null);

  useEffect(() => {
    const element = elementRef.current;
    if (!element) return;

    // Browser support fallback
    if (!window.IntersectionObserver) {
      setIsVisible(true);
      return;
    }

    const observer = new IntersectionObserver(
      ([entry]) => {
        const isElementVisible = entry.isIntersecting;
        
        if (isElementVisible) {
          setIsVisible(true);
          // If freezeOnceVisible is true, disconnect/stop observing
          if (freezeOnceVisible) {
            observer.disconnect();
          }
        } else if (!freezeOnceVisible) {
          setIsVisible(false);
        }
      },
      {
        root,
        rootMargin,
        threshold
      }
    );

    observer.observe(element);

    return () => {
      observer.disconnect();
    };
  }, [root, rootMargin, threshold, freezeOnceVisible]);

  return [elementRef, isVisible];
}
