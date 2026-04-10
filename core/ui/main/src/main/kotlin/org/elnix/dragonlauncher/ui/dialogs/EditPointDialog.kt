@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.base.theme.LocalExtraColors
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.CycleActionStage
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.circles.computePointPosition
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.base.ColorUtils.definedOrNull
import org.elnix.dragonlauncher.enumsui.SelectedUnselectedViewMode
import org.elnix.dragonlauncher.ui.base.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.actions.actionColor
import org.elnix.dragonlauncher.ui.actions.actionLabel
import org.elnix.dragonlauncher.theme.AppObjectsColors
import org.elnix.dragonlauncher.ui.dragon.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.components.PointPreviewCanvas
import org.elnix.dragonlauncher.ui.dragon.text.TextDivider
import org.elnix.dragonlauncher.ui.dragon.components.ValidateCancelButtons
import org.elnix.dragonlauncher.ui.dragon.components.DragonColumnGroup
import org.elnix.dragonlauncher.ui.dragon.components.DragonIconButton
import org.elnix.dragonlauncher.ui.dragon.generic.MultiSelectConnectedButtonRow
import org.elnix.dragonlauncher.ui.dragon.generic.ShowLabels
import org.elnix.dragonlauncher.ui.helpers.ShapeRow
import org.elnix.dragonlauncher.ui.dragon.components.SliderWithLabel
import org.elnix.dragonlauncher.ui.composition.LocalAppsViewModel
import org.elnix.dragonlauncher.ui.composition.LocalDefaultPoint
import org.elnix.dragonlauncher.ui.composition.LocalNests
import org.elnix.dragonlauncher.ui.dragon.dialogs.CustomAlertDialog

/** Which optional feature block is expanded in the edit dialog — at most one at a time. */
private enum class PointFeaturePanel {
    None,
    LiveNest,
    CycleActions,
    HoldAndRun
}

@Composable
fun EditPointDialog(
    point: SwipePointSerializable,
    isDefaultEditing: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (SwipePointSerializable) -> Unit
) {
    val extraColors = LocalExtraColors.current
    val defaultPoint = LocalDefaultPoint.current

    val appsViewModel = LocalAppsViewModel.current
    val nests = LocalNests.current


    var editPoint by remember { mutableStateOf(point) }
    var showEditIconDialog by remember { mutableStateOf(false) }
    var showEditActionDialog by remember { mutableStateOf(false) }
    var showShapePickerDialog by remember { mutableStateOf(false) }
    var showSelectedShapePickerDialog by remember { mutableStateOf(false) }
    var showHapticFeedbackEditor by remember { mutableStateOf(false) }

    /*  ─────────────  Live Nest / Cycle Actions: single expanded panel  ─────────────  */
    var expandedFeaturePanel by remember(point.id) {
        mutableStateOf(
                when {
                point.liveNestTargetNestId != null -> PointFeaturePanel.LiveNest
                point.cycleActions != null -> PointFeaturePanel.CycleActions
                point.holdAndRunDelayMs != null -> PointFeaturePanel.HoldAndRun
                else -> PointFeaturePanel.None
            }
        )
    }
    var showLiveNestNestPicker by remember { mutableStateOf(false) }
    var showHoldAndRunActionDialog by remember { mutableStateOf(false) }

    // Cycle Actions: index of the stage whose action / haptic is being edited (null = none).
    var editingCycleStageActionIndex by remember { mutableStateOf<Int?>(null) }
    var editingCycleStageHapticIndex by remember { mutableStateOf<Int?>(null) }


    val currentActionColor = actionColor(editPoint.action, extraColors)

    val label = editPoint.customName ?: actionLabel(editPoint.action)
    val actionColor =
        actionColor(editPoint.action, extraColors, editPoint.customActionColor?.let { Color(it) })


    var selectedView by remember { mutableStateOf(SelectedUnselectedViewMode.Unselected) }


    val defaultBorderStroke =
        defaultPoint.borderStroke
            ?.takeIf { !isDefaultEditing }
            ?: defaultSwipePointsValues.borderStroke!!

    val defaultBorderColor =
        defaultPoint.borderColor
            ?.takeIf { !isDefaultEditing }
            ?.let(::Color)
            ?: extraColors.circle

    val defaultBackgroundColor =
        defaultPoint.backgroundColor
            ?.takeIf { !isDefaultEditing }
            ?.let(::Color)
            ?: Color.Unspecified

    val defaultBorderStrokeSelected =
        defaultPoint.borderStroke
            ?.takeIf { !isDefaultEditing }
            ?: defaultSwipePointsValues.borderStrokeSelected!!

    val defaultBorderColorSelected =
        defaultPoint.borderColorSelected
            ?.takeIf { !isDefaultEditing }
            ?.let(::Color)
            ?: extraColors.circle

    val defaultBackgroundColorSelected =
        defaultPoint.backgroundColorSelected
            ?.takeIf { !isDefaultEditing }
            ?.let(::Color)
            ?: Color.Unspecified

    val defaultSize =
        defaultPoint.size
            ?.takeIf { !isDefaultEditing }
            ?: defaultSwipePointsValues.size!!


    val defaultInnerPadding =
        defaultPoint.innerPadding
            ?.takeIf { !isDefaultEditing }
            ?: defaultSwipePointsValues.innerPadding!!


    LaunchedEffect(
        editPoint.action,
        editPoint.customIcon,
        editPoint.customActionColor,
        editPoint.size
    ) {
        appsViewModel.reloadPointIcon(editPoint)
    }


    CustomAlertDialog(
        modifier = Modifier
            .padding(16.dp),
        onDismissRequest = onDismiss,
        imePadding = false,
        scroll = false,
        alignment = Alignment.Center,
        confirmButton = {
            ValidateCancelButtons(
                onCancel = onDismiss
            ) {
                onConfirm(editPoint)
            }
        },
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(Modifier.weight(1f))

                    Text(
                        text = stringResource(R.string.edit_point),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Spacer(Modifier.weight(1f))
                    DragonIconButton(
                        onClick = {
                            editPoint = SwipePointSerializable(
                                circleNumber = editPoint.circleNumber,
                                angleDeg = editPoint.angleDeg,
                                nestId = editPoint.nestId,
                                action = editPoint.action,
                                id = editPoint.id
                            )
                        },
                        imageVector = Icons.Default.Restore,
                        contentDescription = stringResource(R.string.reset)
                    )
                }

                DragonColumnGroup {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = stringResource(R.string.unselected_action),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall
                        )

                        Text(
                            text = stringResource(R.string.selected_action),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    PointPreviewCanvas(
                        editPoint = editPoint,
                        defaultPoint = defaultPoint,
                        backgroundSurfaceColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth(1f)
                    )
                }
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (!isDefaultEditing) {

                    /*  ─────────────  Feature row: Live Nest / Cycle Actions / Hold & Run  ─────────────  */
                    item {
                        val liveNestActive = editPoint.liveNestTargetNestId != null
                        val cycleConfigured = editPoint.cycleActions != null
                        val harConfigured = editPoint.holdAndRunDelayMs != null
                        val liveNestExpanded = expandedFeaturePanel == PointFeaturePanel.LiveNest
                        val cycleExpanded = expandedFeaturePanel == PointFeaturePanel.CycleActions
                        val harExpanded = expandedFeaturePanel == PointFeaturePanel.HoldAndRun
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // ── Live Nest ──
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(DragonShape)
                                    .background(
                                        when {
                                            liveNestExpanded -> MaterialTheme.colorScheme.primaryContainer
                                            liveNestActive -> MaterialTheme.colorScheme.secondaryContainer
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .clickable {
                                        expandedFeaturePanel =
                                            if (expandedFeaturePanel == PointFeaturePanel.LiveNest) {
                                                PointFeaturePanel.None
                                            } else {
                                                PointFeaturePanel.LiveNest
                                            }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.live_nest),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = when {
                                        liveNestExpanded -> MaterialTheme.colorScheme.onPrimaryContainer
                                        liveNestActive -> MaterialTheme.colorScheme.onSecondaryContainer
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }

                            // ── Cycle Actions ──
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(DragonShape)
                                    .background(
                                        when {
                                            cycleExpanded -> MaterialTheme.colorScheme.primaryContainer
                                            cycleConfigured -> MaterialTheme.colorScheme.secondaryContainer
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .clickable {
                                        expandedFeaturePanel =
                                            if (expandedFeaturePanel == PointFeaturePanel.CycleActions) {
                                                PointFeaturePanel.None
                                            } else {
                                                PointFeaturePanel.CycleActions
                                            }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.cycle_actions),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = when {
                                        cycleExpanded -> MaterialTheme.colorScheme.onPrimaryContainer
                                        cycleConfigured -> MaterialTheme.colorScheme.onSecondaryContainer
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }

                            // ── Hold & Run ──
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(DragonShape)
                                    .background(
                                        when {
                                            harExpanded -> MaterialTheme.colorScheme.primaryContainer
                                            harConfigured -> MaterialTheme.colorScheme.secondaryContainer
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                    .clickable {
                                        expandedFeaturePanel =
                                            if (expandedFeaturePanel == PointFeaturePanel.HoldAndRun) {
                                                PointFeaturePanel.None
                                            } else {
                                                PointFeaturePanel.HoldAndRun
                                            }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.hold_and_run),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = when {
                                        harExpanded -> MaterialTheme.colorScheme.onPrimaryContainer
                                        harConfigured -> MaterialTheme.colorScheme.onSecondaryContainer
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }

                    item {
                        TextDivider(stringResource(R.string.general_style))
                    }

                    /*  ─────────────  Live Nest configuration panel  ─────────────  */
                    item {
                        AnimatedVisibility(visible = expandedFeaturePanel == PointFeaturePanel.LiveNest) {
                            val liveNestEnabled = editPoint.liveNestTargetNestId != null
                            val targetNest = nests.find { it.id == editPoint.liveNestTargetNestId }
                            val nestLabel = targetNest?.name ?: targetNest?.let { "Nest ${it.id}" }
                            val currentDelay = editPoint.liveNestPreviewDelayMs ?: 500
                            val currentScale = editPoint.liveNestScale ?: 0.65f

                            DragonColumnGroup {
                                if (!liveNestEnabled) {
                                    /*  ─── "Pick Nest" enable row — mirrors Cycle Actions "Add Stage" ───  */
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(DragonShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { showLiveNestNestPicker = true }
                                            .padding(horizontal = 12.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.live_nest_pick_nest),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else {
                                    /*  ─── Nest picker row ───  */
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(DragonShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { showLiveNestNestPicker = true }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.live_nest_target_nest),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = nestLabel ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    /*  ─── Hold delay slider ───  */
                                    SliderWithLabel(
                                        label = stringResource(R.string.live_nest_hold_delay),
                                        value = currentDelay,
                                        valueRange = 100..2000,
                                        color = MaterialTheme.colorScheme.primary,
                                        onReset = {
                                            editPoint = editPoint.copy(liveNestPreviewDelayMs = null)
                                        }
                                    ) { editPoint = editPoint.copy(liveNestPreviewDelayMs = it) }

                                    /*  ─── Scale slider ───  */
                                    SliderWithLabel(
                                        label = stringResource(R.string.live_nest_scale),
                                        value = currentScale,
                                        valueRange = 0.3f..1.0f,
                                        color = MaterialTheme.colorScheme.primary,
                                        onReset = {
                                            editPoint = editPoint.copy(liveNestScale = null)
                                        }
                                    ) { editPoint = editPoint.copy(liveNestScale = it) }

                                    /*  ─── Grace distance slider ───  */
                                    SliderWithLabel(
                                        label = stringResource(R.string.live_nest_grace_distance),
                                        value = editPoint.liveNestGraceDistancePx ?: 50,
                                        valueRange = 0..300,
                                        color = MaterialTheme.colorScheme.primary,
                                        onReset = {
                                            editPoint = editPoint.copy(liveNestGraceDistancePx = null)
                                        }
                                    ) { editPoint = editPoint.copy(liveNestGraceDistancePx = it) }

                                    /*  ─── Optional: dim main nest while Live Nest is open ───  */
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.live_nest_dim_main_nest),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = stringResource(R.string.live_nest_dim_main_nest_summary),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Switch(
                                            checked = editPoint.liveNestMainNestOpacityPercent != null,
                                            onCheckedChange = { on ->
                                                editPoint = if (on) {
                                                    editPoint.copy(
                                                        liveNestMainNestOpacityPercent =
                                                            editPoint.liveNestMainNestOpacityPercent ?: 50
                                                    )
                                                } else {
                                                    editPoint.copy(liveNestMainNestOpacityPercent = null)
                                                }
                                            },
                                            colors = AppObjectsColors.switchColors()
                                        )
                                    }

                                    editPoint.liveNestMainNestOpacityPercent?.let { opacityPct ->
                                        SliderWithLabel(
                                            label = stringResource(R.string.live_nest_main_nest_opacity),
                                            value = opacityPct,
                                            valueRange = 0..100,
                                            color = MaterialTheme.colorScheme.primary,
                                            onReset = {
                                                editPoint = editPoint.copy(liveNestMainNestOpacityPercent = null)
                                            }
                                        ) { editPoint = editPoint.copy(liveNestMainNestOpacityPercent = it) }
                                    }

                                    /*  ─── Disable button ───  */
                                    OutlinedButton(
                                        onClick = {
                                            editPoint = editPoint.copy(
                                                liveNestTargetNestId = null,
                                                liveNestPreviewDelayMs = null,
                                                liveNestScale = null,
                                                liveNestGraceDistancePx = null,
                                                liveNestMainNestOpacityPercent = null
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Text(
                                            text = stringResource(R.string.disable),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                            }
                        }
                    }

                    /*  ─────────────  Cycle Actions configuration panel  ─────────────  */
                    item {
                        AnimatedVisibility(visible = expandedFeaturePanel == PointFeaturePanel.CycleActions) {
                            val cycleStages = editPoint.cycleActions ?: emptyList()

                            DragonColumnGroup {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    /*  ─── One card per stage ───  */
                                    cycleStages.forEachIndexed { index, stage ->
                                        val stageLabel = actionLabel(stage.action)
                                        val stageActionColor = actionColor(stage.action, extraColors)

                                        val stageCardColor =
                                            if (index % 2 == 0) {
                                                MaterialTheme.colorScheme.surfaceContainerHighest
                                            } else {
                                                MaterialTheme.colorScheme.surfaceContainerHigh
                                            }

                                        OutlinedCard(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = DragonShape,
                                            border = BorderStroke(
                                                1.dp,
                                                MaterialTheme.colorScheme.outlineVariant
                                            ),
                                            colors = CardDefaults.outlinedCardColors(
                                                containerColor = stageCardColor
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                                verticalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = stringResource(
                                                            R.string.cycle_actions_stage,
                                                            index + 1
                                                        ),
                                                        style = MaterialTheme.typography.titleSmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    DragonIconButton(
                                                        onClick = {
                                                            val updated = cycleStages.toMutableList()
                                                                .also { it.removeAt(index) }
                                                            editPoint = if (updated.isEmpty()) {
                                                                editPoint.copy(
                                                                    cycleActions = null,
                                                                    cycleActionsLoopEnabled = false,
                                                                    cycleActionsLoopDelayMs = null
                                                                )
                                                            } else {
                                                                editPoint.copy(cycleActions = updated)
                                                            }
                                                        },
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = stringResource(R.string.disable)
                                                    )
                                                }

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(DragonShape)
                                                        .clickable { editingCycleStageActionIndex = index }
                                                        .padding(vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = stageLabel,
                                                        color = stageActionColor,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = stringResource(R.string.edit_action),
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }

                                                SliderWithLabel(
                                                    label = stringResource(R.string.cycle_actions_delay),
                                                    value = stage.triggerTimeMs,
                                                    valueRange = 100..5000,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    onReset = null
                                                ) { newDelay ->
                                                    val updated = cycleStages.toMutableList().also {
                                                        it[index] = it[index].copy(triggerTimeMs = newDelay)
                                                    }
                                                    editPoint = editPoint.copy(cycleActions = updated)
                                                }

                                                HapticFeedBackEditorButtonWithPlayTest(
                                                    customHapticFeedbackSerializable = stage.hapticFeedback,
                                                    titleExt = " (Stage ${index + 1})",
                                                    onClick = { editingCycleStageHapticIndex = index }
                                                )
                                            }
                                        }
                                    }

                                    /*  ─── Add Stage (below the cards) ───  */
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(DragonShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable {
                                                val lastThreshold =
                                                    cycleStages.lastOrNull()?.triggerTimeMs ?: 0
                                                val newStage = CycleActionStage(
                                                    triggerTimeMs = lastThreshold + 500,
                                                    action = editPoint.action
                                                )
                                                editPoint = editPoint.copy(
                                                    cycleActions = cycleStages + newStage
                                                )
                                            }
                                            .padding(horizontal = 12.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.cycle_actions_add_stage),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    /*  ─── Loop (optional tail before cycle restarts) ───  */
                                    if (cycleStages.isNotEmpty()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = stringResource(R.string.cycle_actions_loop),
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = stringResource(R.string.cycle_actions_loop_summary),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Switch(
                                                checked = editPoint.cycleActionsLoopEnabled,
                                                onCheckedChange = { on ->
                                                    editPoint = editPoint.copy(
                                                        cycleActionsLoopEnabled = on,
                                                        cycleActionsLoopDelayMs = when {
                                                            on -> editPoint.cycleActionsLoopDelayMs ?: 500
                                                            else -> editPoint.cycleActionsLoopDelayMs
                                                        }
                                                    )
                                                }
                                            )
                                        }
                                        if (editPoint.cycleActionsLoopEnabled) {
                                            val loopDelay = editPoint.cycleActionsLoopDelayMs ?: 500
                                            SliderWithLabel(
                                                label = stringResource(R.string.cycle_actions_loop_delay),
                                                value = loopDelay,
                                                valueRange = 50..5000,
                                                color = MaterialTheme.colorScheme.primary,
                                                onReset = {
                                                    editPoint = editPoint.copy(cycleActionsLoopDelayMs = 500)
                                                }
                                            ) { ms ->
                                                editPoint = editPoint.copy(cycleActionsLoopDelayMs = ms)
                                            }
                                        }
                                    }

                                    /*  ─── Disable Cycle Actions ───  */
                                    if (editPoint.cycleActions != null) {
                                        OutlinedButton(
                                            onClick = {
                                                editPoint = editPoint.copy(
                                                    cycleActions = null,
                                                    cycleActionsLoopEnabled = false,
                                                    cycleActionsLoopDelayMs = null
                                                )
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text(
                                                text = stringResource(R.string.disable),
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    /*  ─────────────  Hold & Run configuration panel  ─────────────  */
                    item {
                        AnimatedVisibility(visible = expandedFeaturePanel == PointFeaturePanel.HoldAndRun) {
                            val harEnabled = editPoint.holdAndRunDelayMs != null
                            val currentHarDelay = editPoint.holdAndRunDelayMs ?: 1000

                            DragonColumnGroup {
                                if (!harEnabled) {
                                    /*  ─── "Enable Hold & Run" row — mirrors Cycle Actions "Add Stage" ───  */
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(DragonShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable {
                                                editPoint = editPoint.copy(holdAndRunDelayMs = 1000)
                                            }
                                            .padding(horizontal = 12.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.hold_and_run_enable),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else {
                                    /*  ─── Delay slider ───  */
                                    SliderWithLabel(
                                        label = stringResource(R.string.hold_and_run_delay),
                                        value = currentHarDelay,
                                        valueRange = 100..5000,
                                        color = MaterialTheme.colorScheme.primary,
                                        onReset = {
                                            editPoint = editPoint.copy(
                                                holdAndRunDelayMs = null,
                                                holdAndRunAction = null
                                            )
                                        }
                                    ) { newDelay ->
                                        editPoint = editPoint.copy(holdAndRunDelayMs = newDelay)
                                    }

                                    /*  ─── Optional different action than the point’s main action ───  */
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp)
                                        ) {
                                            Text(
                                                text = stringResource(R.string.hold_and_run_custom_action),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = stringResource(R.string.hold_and_run_custom_action_summary),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Switch(
                                            checked = editPoint.holdAndRunAction != null,
                                            onCheckedChange = { on ->
                                                editPoint = if (on) {
                                                    editPoint.copy(
                                                        holdAndRunAction = editPoint.holdAndRunAction
                                                            ?: editPoint.action
                                                    )
                                                } else {
                                                    editPoint.copy(holdAndRunAction = null)
                                                }
                                            },
                                            colors = AppObjectsColors.switchColors()
                                        )
                                    }

                                    editPoint.holdAndRunAction?.let { harAction ->
                                        val harLabel = actionLabel(harAction)
                                        val harColor = actionColor(
                                            harAction,
                                            extraColors,
                                            editPoint.customActionColor?.let { Color(it) }
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(DragonShape)
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .clickable { showHoldAndRunActionDialog = true }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Text(
                                                text = harLabel,
                                                color = harColor,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(Modifier.weight(1f))
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = stringResource(R.string.edit_action),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    /*  ─── Disable button ───  */
                                    OutlinedButton(
                                        onClick = {
                                            editPoint = editPoint.copy(
                                                holdAndRunDelayMs = null,
                                                holdAndRunAction = null
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Text(
                                            text = stringResource(R.string.disable),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        DragonColumnGroup {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(DragonShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable {
                                            showEditActionDialog = true
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = label,
                                        color = actionColor,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = stringResource(R.string.edit_action),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }


                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(DragonShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable {
                                            showEditIconDialog = true
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.edit_icon),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(Modifier.weight(1f))

                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = stringResource(R.string.edit_action),
                                        tint = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }

                            TextField(
                                value = editPoint.customName ?: "",
                                onValueChange = {
                                    editPoint = editPoint.copy(customName = it)
                                },
                                label = { Text(stringResource(R.string.custom_name)) },
                                trailingIcon = {
                                    if (editPoint.customName != null) {
                                        DragonIconButton(
                                            onClick = {
                                                editPoint = editPoint.copy(customName = null)
                                            },
                                            imageVector = Icons.Default.Restore,
                                            contentDescription = stringResource(R.string.reset)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(DragonShape),
                                colors = AppObjectsColors.outlinedTextFieldColors(
                                    removeBorder = true,
                                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )

                            ColorPickerRow(
                                label = stringResource(R.string.custom_action_color),
                                currentColor = editPoint.customActionColor?.let { Color(it) }
                                    ?: currentActionColor,
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant
                            ) { selectedColor ->
                                editPoint = editPoint.copy(customActionColor = selectedColor?.toArgb())
                            }
                        }
                    }
                }

                item {
                    DragonColumnGroup {
                        SliderWithLabel(
                            label = stringResource(R.string.inner_padding),
                            value = editPoint.innerPadding ?: defaultInnerPadding,
                            valueRange = 0..100,
                            color = MaterialTheme.colorScheme.primary,
                            onReset = { editPoint = editPoint.copy(innerPadding = null) }
                        ) { editPoint = editPoint.copy(innerPadding = it) }

                        SliderWithLabel(
                            label = stringResource(R.string.size),
                            value = editPoint.size ?: defaultSize,
                            valueRange = 1..200,
                            color = MaterialTheme.colorScheme.primary,
                            onReset = { editPoint = editPoint.copy(size = null) }
                        ) { editPoint = editPoint.copy(size = it) }
                    }
                }


                /* Selected / Unselected Options Toggler */

                item { TextDivider(stringResource(R.string.individual_style)) }
                item {
                    MultiSelectConnectedButtonRow(
                        entries = SelectedUnselectedViewMode.entries,
                        isChecked = { selectedView == it },
                        showLabels = ShowLabels.Always
                    ) { selectedView = it }
                }
                item {

                    AnimatedContent(selectedView == SelectedUnselectedViewMode.Unselected) { selectedMode ->
                        DragonColumnGroup {
                            if (selectedMode) {
                                SliderWithLabel(
                                    label = stringResource(R.string.border_stroke),
                                    value = editPoint.borderStroke
                                        ?: defaultBorderStroke,
                                    valueRange = 0f..50f,
                                    color = MaterialTheme.colorScheme.primary,
                                    onReset = {
                                        editPoint = editPoint.copy(borderStroke = null)
                                    }
                                ) {
                                    editPoint = editPoint.copy(borderStroke = it)
                                }

                                ColorPickerRow(
                                    label = stringResource(R.string.border_color),
                                    currentColor = editPoint.borderColor?.let { Color(it) }
                                        ?: defaultBorderColor
                                ) { selectedColor ->
                                    editPoint = editPoint.copy(borderColor = selectedColor?.toArgb())
                                }

                                ColorPickerRow(
                                    label = stringResource(R.string.background_color),
                                    currentColor = editPoint.backgroundColor?.let { Color(it) }
                                        ?: defaultBackgroundColor
                                ) { selectedColor ->
                                    editPoint = editPoint.copy(
                                        backgroundColor = selectedColor.definedOrNull()
                                            ?.toArgb()
                                    )
                                }

                                ShapeRow(
                                    selected = editPoint.borderShape ?: defaultSwipePointsValues.borderShape!!,
                                    title = stringResource(R.string.edit_border_shape),
                                    onReset = {
                                        editPoint = editPoint.copy(borderShape = null)
                                    }
                                ) { showShapePickerDialog = true }

                            } else {
                                SliderWithLabel(
                                    label = stringResource(R.string.border_stroke_selected),
                                    value = editPoint.borderStrokeSelected
                                        ?: defaultBorderStrokeSelected,
                                    valueRange = 0f..50f,
                                    color = MaterialTheme.colorScheme.primary,
                                    onReset = {
                                        editPoint =
                                            editPoint.copy(borderStrokeSelected = null)
                                    }
                                ) {
                                    editPoint = editPoint.copy(borderStrokeSelected = it)
                                }


                                ColorPickerRow(
                                    label = stringResource(R.string.border_color_selected),
                                    currentColor = editPoint.borderColorSelected?.let { Color(it) }
                                        ?: defaultBorderColorSelected
                                ) { selectedColor ->
                                    editPoint =
                                        editPoint.copy(borderColorSelected = selectedColor?.toArgb())
                                }


                                ColorPickerRow(
                                    label = stringResource(R.string.background_selected),
                                    currentColor = editPoint.backgroundColorSelected?.let { Color(it) }
                                        ?: defaultBackgroundColorSelected
                                ) { selectedColor ->
                                    editPoint = editPoint.copy(
                                        backgroundColorSelected = selectedColor.definedOrNull()
                                            ?.toArgb()
                                    )
                                }

                                ShapeRow(
                                    selected = editPoint.borderShapeSelected ?: defaultSwipePointsValues.borderShapeSelected!!,
                                    title = stringResource(R.string.edit_border_shape),
                                    onReset = {
                                        editPoint = editPoint.copy(borderShapeSelected = null)
                                    }
                                ) { showSelectedShapePickerDialog = true }
                            }
                        }
                    }
                }


                // Can not edit the haptic feedback in default mode, has to go to nest settings to edit it circle by circle
                if (!isDefaultEditing) {
                    item {
                        DragonColumnGroup {
                            HapticFeedBackEditorButtonWithPlayTest(
                                customHapticFeedbackSerializable = editPoint.hapticFeedback,
                                onClick = { showHapticFeedbackEditor = true },
                            )
                        }
                    }
                } else {
                    item {
                        Text(stringResource(R.string.you_can_edit_haptic_feedback_on_nest_settings))
                    }
                }
            }
        }
    )


    // ── Dialogs ─────────────────────────────────────────

    if (showEditIconDialog) {
        IconEditorDialog(
            point = editPoint,
            onDismiss = { showEditIconDialog = false }
        ) { newIcon ->

            val previewPoint = point.copy(customIcon = newIcon)

            appsViewModel.reloadPointIcon(previewPoint)

            showEditIconDialog = false
            editPoint = editPoint.copy(customIcon = newIcon)
        }
    }
    if (showEditActionDialog) {
        AddPointDialog(
            onDismiss = { showEditActionDialog = false },
            onActionSelected = { selectedAction ->
                editPoint = editPoint.copy(action = selectedAction)
                showEditActionDialog = false
            }
        )
    }

    if (showHoldAndRunActionDialog) {
        AddPointDialog(
            onDismiss = { showHoldAndRunActionDialog = false },
            onActionSelected = { selectedAction ->
                editPoint = editPoint.copy(holdAndRunAction = selectedAction)
                showHoldAndRunActionDialog = false
            }
        )
    }

    if (showShapePickerDialog) {
        ShapePickerDialog(
            selected = editPoint.borderShape ?: defaultSwipePointsValues.borderShape!!,
            onDismiss = { showShapePickerDialog = false }
        ) {
            editPoint = editPoint.copy(borderShape = it)
            showShapePickerDialog = false
        }
    }

    if (showSelectedShapePickerDialog) {
        ShapePickerDialog(
            selected = editPoint.borderShapeSelected ?: defaultSwipePointsValues.borderShapeSelected!!,
            onDismiss = { showSelectedShapePickerDialog = false }
        ) {
            editPoint = editPoint.copy(borderShapeSelected = it)
            showSelectedShapePickerDialog = false
        }
    }


    if (showHapticFeedbackEditor) {
        HapticFeedbackEditor(
            initial = editPoint.hapticFeedback,
            onDismiss = { showHapticFeedbackEditor = false }
        ) { newHaptic ->
            editPoint = editPoint.copy(hapticFeedback = newHaptic)
            showHapticFeedbackEditor = false
        }
    }

    /*  ─────────────  Cycle Actions ─ action editor  ─────────────  */
    if (editingCycleStageActionIndex != null) {
        val idx = editingCycleStageActionIndex!!
        AddPointDialog(
            onDismiss = { editingCycleStageActionIndex = null },
            onActionSelected = { selectedAction ->
                val current = editPoint.cycleActions ?: emptyList()
                if (idx < current.size) {
                    val updated = current.toMutableList().also { it[idx] = it[idx].copy(action = selectedAction) }
                    editPoint = editPoint.copy(cycleActions = updated)
                }
                editingCycleStageActionIndex = null
            }
        )
    }

    /*  ─────────────  Cycle Actions ─ haptic editor  ─────────────  */
    if (editingCycleStageHapticIndex != null) {
        val idx = editingCycleStageHapticIndex!!
        val currentStages = editPoint.cycleActions ?: emptyList()
        HapticFeedbackEditor(
            initial = currentStages.getOrNull(idx)?.hapticFeedback,
            onDismiss = { editingCycleStageHapticIndex = null }
        ) { newHaptic ->
            if (idx < currentStages.size) {
                val updated = currentStages.toMutableList().also { it[idx] = it[idx].copy(hapticFeedback = newHaptic) }
                editPoint = editPoint.copy(cycleActions = updated)
            }
            editingCycleStageHapticIndex = null
        }
    }

    /*  ─────────────  Live Nest ─ nest picker  ─────────────  */
    if (showLiveNestNestPicker) {
        NestManagementDialog(
            onDismissRequest = { showLiveNestNestPicker = false },
            title = stringResource(R.string.pick_a_nest),
            onNewNest = null,
            onNameChange = null,
            onDelete = null,
            onSelect = { selectedNest ->
                editPoint = editPoint.copy(
                    liveNestTargetNestId = selectedNest.id,
                    liveNestPreviewDelayMs = editPoint.liveNestPreviewDelayMs ?: 500,
                    liveNestScale = editPoint.liveNestScale ?: 0.65f,
                    liveNestMainNestOpacityPercent = editPoint.liveNestMainNestOpacityPercent ?: 50
                )
                showLiveNestNestPicker = false
            }
        )
    }
}
