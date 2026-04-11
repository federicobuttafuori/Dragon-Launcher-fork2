package org.elnix.dragonlauncher.ui.helpers.nests.points

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import org.elnix.dragonlauncher.common.serializables.IconShape

/**
 * An LRU (Least Recently Used) cache for [Path] objects derived from [IconShape], used to avoid
 * recomputing shape outlines on every draw frame in the recursive nest drawing system.
 *
 * ## Why this exists
 * Shape-to-path conversion (`shapeToPath`) involves outline creation and path allocation —
 * both expensive when called recursively across many points per frame. Since most points share
 * the same `(shape, size)` combination across frames, caching the base [Path] and translating
 * it at draw time yields a significant reduction in per-frame allocations.
 *
 * ## Translation at draw time
 * The cached [Path] is always centered at the origin `(0, 0)`. The caller is responsible for
 * translating it to the correct [androidx.compose.ui.geometry.Offset] at draw time via
 * `withTransform` or `translate`. This is intentionally cheap — it is a
 * canvas matrix operation with no path re-allocation — which is why [androidx.compose.ui.geometry.Offset] was deliberately
 * excluded from the cache key.
 *
 * ## Eviction strategy
 * Backed by a [LinkedHashMap] in access-order mode. When the number of cached entries exceeds
 * [maxSize], the least recently accessed entry is automatically evicted. No manual cache
 * invalidation is needed — stale entries for points no longer on screen age out naturally.
 *
 * ## Sizing guidance
 * [maxSize] is set in [SwipeDrawParams] to the current number of points, so the cache is
 * sized exactly to the working set with no wasted memory. Call [updateMaxCacheSize] whenever
 * the point count changes to keep the limit accurate.
 *
 * ## Thread safety
 * This cache is **not thread-safe**. It must be created with `remember` at the composable
 * level and accessed exclusively on the main thread inside `DrawScope` callbacks.
 *
 * @param initialMaxSize Maximum number of [Path] entries before LRU eviction kicks in.
 *   Updated dynamically via [updateMaxCacheSize] as the point count changes.
 */
class DrawPathCache(initialMaxSize: Int) {

    private var maxSize = initialMaxSize

    /**
     * Updates the maximum number of cached entries.
     * Call this whenever the number of points changes to keep the cache sized to the working set.
     *
     * @param newSize The new maximum entry count.
     */
    fun updateMaxCacheSize(newSize: Int) {
        maxSize = newSize
    }

    private val paths = object : LinkedHashMap<Pair<IconShape, Size>, Path>(
        maxSize, 0.75f, true // accessOrder = true → promotes on get, enabling LRU eviction
    ) {
        override fun removeEldestEntry(
            eldest: MutableMap.MutableEntry<Pair<IconShape, Size>, Path>
        ) = size > maxSize
    }

    /**
     * Returns the cached [Path] for the given [shape] and [size], computing and storing it
     * via [compute] on a cache miss.
     *
     * The returned path is always origin-centered. The caller must apply the correct
     * draw-time translation — see the class-level doc for rationale.
     *
     * @param shape The [IconShape] defining the outline geometry.
     * @param size The [Size] at which the shape is rendered.
     * @param compute Produces the [Path] on a cache miss. Only invoked when no entry exists
     *   for `(shape, size)`.
     * @return The cached or freshly computed [Path].
     */
    fun getOrCompute(
        shape: IconShape,
        size: Size,
        compute: () -> Path
    ): Path = paths.getOrPut(Pair(shape, size), compute)


    /**
     * The current number of entries held in the cache.
     * Useful for debugging or logging cache pressure.
     */
    val size: Int get() = paths.size
}