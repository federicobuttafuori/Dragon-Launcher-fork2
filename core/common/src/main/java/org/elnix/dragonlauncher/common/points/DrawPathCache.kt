package org.elnix.dragonlauncher.common.points

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import org.elnix.dragonlauncher.common.serializables.IconShape

/**
 * An LRU (Least Recently Used) cache for [androidx.compose.ui.graphics.Path] objects derived from [IconShape], used to avoid
 * recomputing shape outlines on every draw frame in the recursive nest drawing system.
 *
 * ## Why this exists
 * Shape-to-path conversion (`shapeToPath`) involves outline creation, path allocation, and
 * translation — all of which are expensive when called recursively across many points per frame.
 * Since the vast majority of points share the same shape/size/center combination across frames,
 * caching the resulting [androidx.compose.ui.graphics.Path] objects yields a significant reduction in per-frame allocations.
 *
 * ## Eviction strategy
 * Backed by a [LinkedHashMap] in access-order mode. When the number of cached entries exceeds
 * [maxSize], the least recently accessed entry is automatically evicted. This means no manual
 * cache invalidation is needed — stale entries for points that are no longer visible will
 * naturally age out as new entries are added.
 *
 * ## Sizing guidance
 * The default [maxSize] of 64 is intentionally generous relative to the number of points
 * typically visible on screen (usually < 20). Raising it costs memory; lowering it increases
 * the chance of cache misses for complex nested nests.
 *
 * ## Thread safety
 * This cache is **not thread-safe**. It is intended to be created with `remember` at the
 * composable level and used exclusively on the main thread during `DrawScope` callbacks.
 *
 * @param maxSize Maximum number of [androidx.compose.ui.graphics.Path] entries to keep before evicting the least recently used.
 * It is initialized in [SwipeDrawParams] at the number of points you have to avoid boilerplate
 * Can be edited when points size changes
 */
class DrawPathCache(val initialMaxSize: Int = 64) {

    private var maxSize = initialMaxSize

    fun updateMaxCacheSize(newSize: Int) {
        maxSize = newSize
    }

    private val paths = object : LinkedHashMap<Triple<IconShape, Size, Offset>, Path>(
        maxSize, 0.75f, true // accessOrder = true → promotes on get, enabling LRU eviction
    ) {
        override fun removeEldestEntry(
            eldest: MutableMap.MutableEntry<Triple<IconShape, Size, Offset>, Path>
        ) = size > maxSize
    }

    /**
     * Returns the cached [Path] for the given [shape], [size], and [center] combination,
     * computing and storing it via [compute] if no entry exists yet.
     *
     * The cache key is the triple `(shape, size, center)`. Two calls with identical values
     * for all three parameters will return the same [Path] instance without recomputation.
     *
     * @param shape The [IconShape] that defines the outline geometry.
     * @param size The [Size] at which the shape is rendered.
     * @param center The [Offset] to which the path is translated on the canvas.
     * @param compute Lambda that produces the [Path] on a cache miss. Only called when no
     *   cached entry exists for the given key.
     * @return The cached or freshly computed [Path].
     */
    fun getOrCompute(
        shape: IconShape,
        size: Size,
        center: Offset,
        compute: () -> Path
    ): Path {
        return paths.getOrPut(Triple(shape, size, center), compute)
    }

    /**
     * The current number of entries held in the cache.
     * Useful for debugging or logging cache pressure.
     */
    val size: Int get() = paths.size
}