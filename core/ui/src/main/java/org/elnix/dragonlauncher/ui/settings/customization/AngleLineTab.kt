@file:Suppress("AssignedValueIsNeverRead")

package org.elnix.dragonlauncher.ui.settings.customization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.elnix.dragonlauncher.base.theme.LocalExtraColors
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.serializables.ColorSerializer
import org.elnix.dragonlauncher.common.serializables.CustomObjectBlockProperties
import org.elnix.dragonlauncher.common.serializables.CustomObjectSerializable
import org.elnix.dragonlauncher.common.utils.Constants.Logging.ANGLE_LINE_TAG
import org.elnix.dragonlauncher.common.utils.UiConstants
import org.elnix.dragonlauncher.settings.stores.AngleLineSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.components.ExpandableSection
import org.elnix.dragonlauncher.ui.components.settings.SettingsSwitchRow
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.dialogs.AngleLineObjectsOrderDialog
import org.elnix.dragonlauncher.ui.dialogs.rememberLineObjectsOrder
import org.elnix.dragonlauncher.ui.helpers.customobjects.EditCustomObjectBlock
import org.elnix.dragonlauncher.ui.helpers.customobjects.actionLine
import org.elnix.dragonlauncher.ui.helpers.settings.SettingsLazyHeader
import org.elnix.dragonlauncher.ui.modifiers.settingsGroup
import org.elnix.dragonlauncher.ui.remembers.rememberDecodedObject
import org.elnix.dragonlauncher.ui.remembers.rememberExpandableSection
import kotlin.math.atan2


@Composable
fun AngleLineTab(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val density = LocalDensity.current
    val extraColors = LocalExtraColors.current
    val scope = rememberCoroutineScope()

    val showLineObjectPreview by AngleLineSettingsStore.showLineObjectPreview.asState()
    val showAngleLineObjectPreview by AngleLineSettingsStore.showAngleLineObjectPreview.asState()
    val showStartObjectPreview by AngleLineSettingsStore.showStartObjectPreview.asState()
    val showEndObjectPreview by AngleLineSettingsStore.showEndObjectPreview.asState()


    val lineObjectExpandableSectionState = rememberExpandableSection(stringResource(R.string.line_object))
    val angleObjectExpandableSectionState = rememberExpandableSection(stringResource(R.string.angle_object))
    val startObjectExpandableSectionState = rememberExpandableSection(stringResource(R.string.start_object))
    val endObjectExpandableSectionState = rememberExpandableSection(stringResource(R.string.end_object))

    val order by rememberLineObjectsOrder()
    var showOrderDialog by remember { mutableStateOf(false) }

    val json = Json {
        serializersModule = SerializersModule {
            contextual(Color::class, ColorSerializer)
        }
    }

    val lineJson by AngleLineSettingsStore.lineJson.asState()
    val lineObject = rememberDecodedObject(
        jsonString = lineJson,
        default = UiConstants.defaultLineCustomObject,
        json = json
    ) {
        ctx.logE(ANGLE_LINE_TAG) { "Error decoding lineObject" }
    }

    val angleLineJson by AngleLineSettingsStore.angleLineJson.asState()
    val angleLineObject = rememberDecodedObject(
        jsonString = angleLineJson,
        default = UiConstants.defaultAngleCustomObject,
        json = json
    ) {
        ctx.logE(ANGLE_LINE_TAG) { "Error decoding angleLineObject" }
    }

    val startLineJson by AngleLineSettingsStore.startLineJson.asState()
    val startLineObject = rememberDecodedObject(
        jsonString = startLineJson,
        default = UiConstants.defaultStartCustomObject,
        json = json
    ) {
        ctx.logE(ANGLE_LINE_TAG) { "Error decoding startLineObject" }
    }

    val endLineJson by AngleLineSettingsStore.endLineJson.asState()
    val endLineObject = rememberDecodedObject(
        jsonString = endLineJson,
        default = UiConstants.defaultEndCustomObject,
        json = json
    ) {
        ctx.logE(ANGLE_LINE_TAG) { "Error decoding endLineObject" }
    }

    val rgbLine by UiSettingsStore.rgbLine.asState()


    var dummyEnd by remember { mutableStateOf(Offset.Infinite) }
    var hasAlreadyBeenPlaced by remember { mutableStateOf(false) }

    SettingsLazyHeader(
        title = stringResource(R.string.angle_line),
        onBack = onBack,
        helpText = stringResource(R.string.angle_line_help),
        onReset = {
            scope.launch {
                AngleLineSettingsStore.resetAll(ctx)
            }
        },
        Pair({ showOrderDialog = true }, Icons.Default.MoreVert),
        scrollableContent = true,
        content = {

            /**
             * Preview of the line
             */
            Box(
                modifier = Modifier
                    .settingsGroup()
                    .aspectRatio(1f)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        if (!hasAlreadyBeenPlaced) {
                            val rect = coordinates.boundsInRoot()
                            val rectSize = (rect.height * density.density).toInt() / 2

                            dummyEnd = Offset(
                                rect.left + (0..rectSize).random(),
                                rect.top + (0..rectSize).random()
                            )
                            // Prevent the thing to move after first placement
                            hasAlreadyBeenPlaced = true
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            // Allow the user to move the end for cleaner preview
                            dummyEnd = change.position
                        }
                    }
            ) {

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            compositingStrategy = CompositingStrategy.Offscreen
                        }
                ) {

                    val start = Offset(size.width / 2f, size.height / 2f)

                    val dx = dummyEnd.x - start.x
                    val dy = dummyEnd.y - start.y

                    // angle relative to UP = 0°
                    val angleRad = atan2(dx.toDouble(), -dy.toDouble())
                    val angleDeg = Math.toDegrees(angleRad)
                    val angle0to360 = if (angleDeg < 0) angleDeg + 360 else angleDeg


                    val lineColor =
                        if (rgbLine) Color.hsv(angle0to360.toFloat(), 1f, 1f)
                        else extraColors.angleLine

                    actionLine(
                        start = start,
                        end = dummyEnd,

                        order = order,

                        showLineObjectPreview = showLineObjectPreview,
                        showAngleLineObjectPreview = showAngleLineObjectPreview,
                        showStartObjectPreview = showStartObjectPreview,
                        showEndObjectPreview = showEndObjectPreview,

                        lineCustomObject = lineObject,
                        angleLineCustomObject = angleLineObject,
                        startCustomObject = startLineObject,
                        endCustomObject = endLineObject,
                        sweepAngle = angle0to360.toFloat(),

                        lineColor = lineColor
                    )
                }
            }

            /** Line object setting */
            ExpandableSection(lineObjectExpandableSectionState) {
                SettingsSwitchRow(
                    setting = AngleLineSettingsStore.showLineObjectPreview,
                    title = stringResource(R.string.show_app_line_preview),
                    description = stringResource(R.string.show_app_line_preview_description)
                )
                AnimatedVisibility(showLineObjectPreview) {
                    EditCustomObjectBlock(
                        editObject = lineObject,
                        default = UiConstants.defaultLineCustomObject,
                        properties = CustomObjectBlockProperties(
                            allowSizeCustomization = false,
                            allowShapeCustomization = false
                        )
                    ) {
                        val newLineJson = json.encodeToString(CustomObjectSerializable.serializer(), it)
                        scope.launch {
                            AngleLineSettingsStore.lineJson.set(ctx, newLineJson)
                        }
                    }
                }
            }

            /** Angle Line object setting */
            ExpandableSection(angleObjectExpandableSectionState) {
                SettingsSwitchRow(
                    setting = AngleLineSettingsStore.showAngleLineObjectPreview,
                    title = stringResource(
                        R.string.show_app_angle_preview,
                        if (!showAngleLineObjectPreview) stringResource(R.string.do_you_hate_it) else ""
                    ),
                    description = stringResource(R.string.show_app_angle_preview_description)
                )

                AnimatedVisibility(showAngleLineObjectPreview) {
                    EditCustomObjectBlock(
                        editObject = angleLineObject,
                        default = UiConstants.defaultAngleCustomObject,
                        properties = CustomObjectBlockProperties(
                            allowShapeCustomization = false
                        )
                    ) {
                        val newAngleJson = json.encodeToString(CustomObjectSerializable.serializer(), it)
                        scope.launch {
                            AngleLineSettingsStore.angleLineJson.set(ctx, newAngleJson)
                        }
                    }
                }
            }

            /** Start object setting */
            ExpandableSection(startObjectExpandableSectionState) {
                SettingsSwitchRow(
                    setting = AngleLineSettingsStore.showStartObjectPreview,
                    title = stringResource(R.string.show_start_object_preview),
                    description = stringResource(R.string.show_start_object_preview_desc)
                )

                AnimatedVisibility(showStartObjectPreview) {
                    EditCustomObjectBlock(
                        editObject = startLineObject,
                        default = UiConstants.defaultStartCustomObject
                    ) {
                        val newStarJson = json.encodeToString(CustomObjectSerializable.serializer(), it)
                        scope.launch {
                            AngleLineSettingsStore.startLineJson.set(ctx, newStarJson)
                        }
                    }
                }
            }

            /** End object setting */
            ExpandableSection(endObjectExpandableSectionState) {
                SettingsSwitchRow(
                    setting = AngleLineSettingsStore.showEndObjectPreview,
                    title = stringResource(R.string.show_end_object_preview),
                    description = stringResource(R.string.show_end_object_preview_desc)
                )

                AnimatedVisibility(showEndObjectPreview) {
                    EditCustomObjectBlock(
                        editObject = endLineObject,
                        default = UiConstants.defaultEndCustomObject
                    ) {
                        val newEndJson = json.encodeToString(CustomObjectSerializable.serializer(), it)
                        scope.launch {
                            AngleLineSettingsStore.endLineJson.set(ctx, newEndJson)
                        }
                    }
                }
            }
        }
    )

    if (showOrderDialog) {
        AngleLineObjectsOrderDialog { showOrderDialog = false }
    }
}
