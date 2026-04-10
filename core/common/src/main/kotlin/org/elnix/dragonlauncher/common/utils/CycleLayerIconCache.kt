package org.elnix.dragonlauncher.common.utils

/**
 * Icon cache keys for Cycle Actions preview: one bitmap per layer.
 *
 * - Index **0** = base action ([SwipePointSerializable.action]).
 * - Index **1..N** = [org.elnix.dragonlauncher.common.serializables.CycleActionStage.action] for each stage.
 */
fun cycleLayerIconCacheKey(pointId: String, layerIndex: Int): String =
    "${pointId}__ca_layer_$layerIndex"
