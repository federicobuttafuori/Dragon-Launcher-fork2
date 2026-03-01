package org.elnix.dragonlauncher.common.serializables

import com.google.gson.annotations.SerializedName


/**
 * New CircleNest system, where every bloc of circles is contained inside one of those*
 * This way, we can navigate across those nests, to achieve more actions, using the jump actions
 */
data class CircleNest(
    /**
     *  By default the id 0 is the first nest that is available,
     *  I'll try to make the old system importable, to avoid breaking changes like empty actions circle
     */
    @SerializedName("id") val id: Int = 0,
    /**
     * Holds the cancel zone (index -1), and the circle numbers for each drag distances
     * for all the circles in the nest (index positive integer)
     * the key is the circle number, made for allowing not ascending order drag distances
     * For the last one, the drag distance has no limit, it's not even counted
     */
    @SerializedName("dragDistances") val dragDistances: Map<Int, Int> = mapOf(
        -1 to 150,
        0 to 300,
        1 to 450,
        2 to 600
    ),
    /**
     * A custom name for the nest you can set for easier identification
     */
    @SerializedName("name") val name: String? = null,

    /**
     * Haptic feedback, as default for the points in  the circle, separated from the point system
     */
    @SerializedName("haptic") val haptic: Map<Int, Int> = emptyMap(),

    /**
     * Haptic feedback, as default for the points in  the circle, separated from the point system
     */
    @SerializedName("minAngleActivation") val minAngleActivation: Map<Int, Int> = emptyMap(),

) {
    override fun toString(): String {
        return "Nest N°$id | contains ${dragDistances.size} circles: \n${dragDistances.map { "\n${it.key} to ${it.value}" }}"
    }
}
