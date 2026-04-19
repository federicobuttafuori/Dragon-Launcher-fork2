package org.elnix.dragonlauncher.enumsui

import androidx.compose.ui.graphics.vector.ImageVector
import org.elnix.dragonlauncher.common.R

enum class PointFeaturePanel(
    override val resId: Int?,
    override val iconEnabled: ImageVector? = null,
    override val iconDisabled: ImageVector? = null
) : ToggleButtonOption {
    LiveNest(R.string.live_nest),
    CycleActions(R.string.cycle_actions),
    HoldAndRun(R.string.hold_and_run)
}
