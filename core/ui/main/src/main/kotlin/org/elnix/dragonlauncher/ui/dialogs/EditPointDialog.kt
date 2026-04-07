@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.dialogs

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.elnix.dragonlauncher.base.theme.LocalExtraColors
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.serializables.defaultSwipePointsValues
import org.elnix.dragonlauncher.common.utils.colors.ColorUtils.definedOrNull
import org.elnix.dragonlauncher.enumsui.SelectedUnselectedViewMode
import org.elnix.dragonlauncher.ui.UiConstants.DragonShape
import org.elnix.dragonlauncher.ui.actions.actionColor
import org.elnix.dragonlauncher.ui.actions.actionLabel
import org.elnix.dragonlauncher.ui.colors.AppObjectsColors
import org.elnix.dragonlauncher.ui.colors.ColorPickerRow
import org.elnix.dragonlauncher.ui.components.PointPreviewCanvas
import org.elnix.dragonlauncher.ui.components.TextDivider
import org.elnix.dragonlauncher.ui.components.ValidateCancelButtons
import org.elnix.dragonlauncher.ui.components.dragon.DragonColumnGroup
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.components.generic.MultiSelectConnectedButtonRow
import org.elnix.dragonlauncher.ui.components.generic.ShowLabels
import org.elnix.dragonlauncher.ui.helpers.ShapeRow
import org.elnix.dragonlauncher.ui.helpers.SliderWithLabel
import org.elnix.dragonlauncher.ui.remembers.LocalAppsViewModel
import org.elnix.dragonlauncher.ui.remembers.LocalDefaultPoint


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


    var editPoint by remember { mutableStateOf(point) }
    var showEditIconDialog by remember { mutableStateOf(false) }
    var showEditActionDialog by remember { mutableStateOf(false) }
    var showShapePickerDialog by remember { mutableStateOf(false) }
    var showSelectedShapePickerDialog by remember { mutableStateOf(false) }
    var showHapticFeedbackEditor by remember { mutableStateOf(false) }


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

                item { TextDivider(stringResource(R.string.individual_options)) }
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
}
