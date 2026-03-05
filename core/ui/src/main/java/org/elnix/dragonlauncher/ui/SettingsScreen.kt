package org.elnix.dragonlauncher.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Grid3x3
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.base.theme.LocalExtraColors
import org.elnix.dragonlauncher.base.theme.addRemoveCirclesColor
import org.elnix.dragonlauncher.base.theme.copyColor
import org.elnix.dragonlauncher.base.theme.moveColor
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.serializables.CircleNest
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.Constants
import org.elnix.dragonlauncher.common.utils.Constants.Logging.NESTS_TAG
import org.elnix.dragonlauncher.common.utils.Constants.Logging.SWIPE_TAG
import org.elnix.dragonlauncher.common.utils.Constants.Settings.POINT_RADIUS_PX
import org.elnix.dragonlauncher.common.utils.Constants.Settings.SNAP_STEP_DEG
import org.elnix.dragonlauncher.common.utils.Constants.Settings.TOUCH_THRESHOLD_PX
import org.elnix.dragonlauncher.common.utils.UiCircle
import org.elnix.dragonlauncher.common.utils.circles.autoSeparate
import org.elnix.dragonlauncher.common.utils.circles.computePointPosition
import org.elnix.dragonlauncher.common.utils.circles.normalizeAngle
import org.elnix.dragonlauncher.common.utils.circles.randomFreeAngle
import org.elnix.dragonlauncher.common.utils.circles.rememberNestNavigation
import org.elnix.dragonlauncher.common.utils.semiTransparentIfDisabled
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.settings.stores.DebugSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeMapSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.components.AppPreviewTitle
import org.elnix.dragonlauncher.ui.components.burger.BurgerAction
import org.elnix.dragonlauncher.ui.components.burger.BurgerListAction
import org.elnix.dragonlauncher.ui.components.dragon.DragonColumnGroup
import org.elnix.dragonlauncher.ui.components.dragon.DragonIconButton
import org.elnix.dragonlauncher.ui.components.settings.SettingsSlider
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.dialogs.AddPointDialog
import org.elnix.dragonlauncher.ui.dialogs.EditPointDialog
import org.elnix.dragonlauncher.ui.dialogs.NestManagementDialog
import org.elnix.dragonlauncher.ui.dialogs.UserValidation
import org.elnix.dragonlauncher.ui.helpers.CircleIconButton
import org.elnix.dragonlauncher.ui.helpers.RepeatingPressButton
import org.elnix.dragonlauncher.ui.helpers.nests.actionsInCircle
import org.elnix.dragonlauncher.ui.helpers.nests.circlesSettingsOverlay
import org.elnix.dragonlauncher.ui.helpers.nests.glowOverlay
import org.elnix.dragonlauncher.ui.helpers.nests.swipeDefaultParams
import org.elnix.dragonlauncher.ui.remembers.LocalAppsViewModel
import org.elnix.dragonlauncher.ui.remembers.LocalDefaultPoint
import java.math.RoundingMode
import java.util.UUID
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.round
import kotlin.math.sin

@SuppressLint("LocalContextGetResourceValueCall")
@Suppress("AssignedValueIsNeverRead")
@Composable
fun SettingsScreen(
    onAdvSettings: () -> Unit,
    onNestEdit: (nest: Int) -> Unit,
    onBack: () -> Unit
) {
    val ctx = LocalContext.current
    val defaultPoint = LocalDefaultPoint.current
    val extraColors = LocalExtraColors.current

    val appsViewModel = LocalAppsViewModel.current

    val scope = rememberCoroutineScope()

    val iconsVersion by appsViewModel.iconsVersion.collectAsState()

    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary

    val snapPoints by UiSettingsStore.snapPoints.asState()
    val autoSeparatePoints by UiSettingsStore.autoSeparatePoints.asState()
    val appLabelOverlaySize by UiSettingsStore.appLabelOverlaySize.asState()
    val appIconOverlaySize by UiSettingsStore.appIconOverlaySize.asState()

    val settingsDebugInfos by DebugSettingsStore.settingsDebugInfo.asState()

    var center by remember { mutableStateOf(Offset.Zero) }
    var availableWidth by remember { mutableFloatStateOf(0f) }

    val points: SnapshotStateList<SwipePointSerializable> = remember { mutableStateListOf() }
    val nests: SnapshotStateList<CircleNest> = remember { mutableStateListOf() }

    var recomposeTrigger by remember { mutableIntStateOf(0) }

    val circles: SnapshotStateList<UiCircle> = remember { mutableStateListOf() }

    var selectedPoint by remember { mutableStateOf<SwipePointSerializable?>(null) }
    val selectedPointTempOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var isDragging by remember { mutableStateOf(false) }

    var closestHoveredPoint by remember { mutableStateOf<SwipePointSerializable?>(null) }
    var closestHoveredTempOffset by remember { mutableStateOf<Offset?>(null) }
    var ableToLaunchHoverAction by remember { mutableStateOf(false) }

    val hoveredPointRadialGradientProgress by animateFloatAsState(
        targetValue = if (ableToLaunchHoverAction) Constants.Settings.HOVER_GRADIENT_RADIUS else 1f
    )


    var lastSelectedCircle by remember { mutableIntStateOf(0) }
    val aPointIsSelected = selectedPoint != null

    var showBurgerMenu by remember { mutableStateOf(false) }

    var showEditDefaultPoint by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<SwipePointSerializable?>(null) }

    // Manual placement mode state (multi-select "Place one by one")
    var manualPlacementQueue by remember { mutableStateOf<List<SwipeActionSerializable>>(emptyList()) }
    val isInManualPlacementMode = manualPlacementQueue.isNotEmpty()

    var showNestManagementDialog by remember { mutableStateOf(false) }
    var showResetPointsAndNestsDialog by remember { mutableStateOf(false) }

    /** ──────────────────── NESTS SYSTEM ────────────────────
     * - Collects the nests from the datastore, then initialize the base nest to 0 (always the default)
     * while all the other have a random id
     */


    val nestNavigation = rememberNestNavigation(nests)
    val currentNest = nestNavigation.currentNest
    val nestId = currentNest.id


    /**
     * The number of circles; it's the size of the current nest, minus one, cause it ignores the
     * cancel zone
     */
    val circleNumber = currentNest.dragDistances.size - 1

    /**
     * Computes an even distance for the circles spacing, for clean integration
     */
    val circlesWidthIncrement = (1f / circleNumber).takeIf { it != 0f } ?: 1f

    /**
     * Used to ensure that there is always a 0-id nest, the default one, the most important
     */
    LaunchedEffect(nestId, nests.size) {
        if (nests.isNotEmpty() && nests.none { it.id == nestId }) {
            logD(NESTS_TAG, "Creating missing nest $nestId")
            nests.add(CircleNest(id = nestId))
        }
    }


    fun reloadIcons() {
        appsViewModel.preloadPointIcons(
            points = points.filter { it.nestId == nestId },
            override = true
        )

        /* Load asynchronously all the other points, to avoid lag */
        scope.launch(Dispatchers.IO) {
            appsViewModel.preloadPointIcons(
                points = points,
                override = true
            )
        }
    }

    /**
     * Reload all point icons on every change of the points, nestId, appIconOverlaySize, or default point
     * Set the size of the icons to the max size between the 2 overlays sizes preview to display them cleanly
     */
    LaunchedEffect(points, nestId, appIconOverlaySize, defaultPoint) {
        reloadIcons()
    }


    var bannerVisible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (bannerVisible) 1f else 0f,
        animationSpec = tween(150)
    )
    val offsetY by animateDpAsState(
        targetValue = if (bannerVisible) 0.dp else (-20).dp,
        animationSpec = tween(150)
    )


    var undoStack by remember { mutableStateOf<List<List<SwipePointSerializable>>>(emptyList()) }
    var nestsUndoStack by remember { mutableStateOf<List<List<CircleNest>>>(emptyList()) }

    var redoStack by remember { mutableStateOf<List<List<SwipePointSerializable>>>(emptyList()) }
    var nestsRedoStack by remember { mutableStateOf<List<List<CircleNest>>>(emptyList()) }

    fun snapshotPoints(): List<SwipePointSerializable> = points.map { it.copy() }
    fun snapshotNests(): List<CircleNest> = nests.map { it.copy() }


    fun save() {
        scope.launch {
            SwipeSettingsStore.savePoints(ctx, snapshotPoints())
            SwipeSettingsStore.saveNests(ctx, snapshotNests())
        }
    }


    fun applyChange(mutator: () -> Unit) {
        // Save current state into undo before mutation
        undoStack = undoStack + listOf(snapshotPoints())
        nestsUndoStack = nestsUndoStack + listOf(snapshotNests())
        // Any new user change invalidates redo history
        redoStack = emptyList()
        nestsRedoStack = emptyList()
        // Now apply the change
        mutator()
        recomposeTrigger++
        save()
    }

    fun undo() {
        if (undoStack.isEmpty() && nestsUndoStack.isEmpty()) return

        // Current state goes to redo
        redoStack = redoStack + listOf(snapshotPoints())
        nestsRedoStack = nestsRedoStack + listOf(snapshotNests())

        // Pop last from undo and set it as current
        val last = undoStack.last()
        undoStack = undoStack.dropLast(1)

        val lastNests = nestsUndoStack.last()
        nestsUndoStack = nestsUndoStack.dropLast(1)

        points.clear()
        points.addAll(last.map { it.copy() })

        nests.clear()
        nests.addAll(lastNests)

        selectedPoint = points.find { it.id == (selectedPoint?.id ?: "") }

        save()
    }

    fun redo() {
        if (redoStack.isEmpty() && nestsRedoStack.isEmpty()) return

        // Current state goes back to undo
        undoStack = undoStack + listOf(snapshotPoints())
        nestsUndoStack = nestsUndoStack + listOf(snapshotNests())

        val last = redoStack.last()
        redoStack = redoStack.dropLast(1)

        val lastNests = nestsRedoStack.last()
        nestsRedoStack = nestsRedoStack.dropLast(1)

        points.clear()
        points.addAll(last.map { it.copy() })

        nests.clear()
        nests.addAll(lastNests)

        selectedPoint = points.find { it.id == (selectedPoint?.id ?: "") }

        save()
    }

    /**
     * Adds a new nest to the current list of nests.
     *
     * This function generates a unique, human-readable ID for the new nest,
     * ensures it does not conflict with existing nest IDs, and initializes
     * its drag distances for all circles in the range [-1, circleNumber + 1].
     *
     * The new nest is then added to the `nests` list and the state is saved.
     *
     * @param circleNumber The number of circles for which to initialize drag distances.
     *                     Default is 3.
     * @return The unique ID of the newly created nest.
     */
    fun addNewNest(circleNumber: Int = 3): Int {
        // Generate a new, unique nest ID
        val existingIds = nests.map { it.id }.toSet()
        var newNestId = nests.size
        while (newNestId in existingIds) {
            newNestId++
        }

        val dragDistances = mutableStateMapOf<Int, Int>().apply {
            for (id in -1..<circleNumber) {
                this[id] = defaultDragDistance(id)
            }
        }

        // Add the new nest
        nests += CircleNest(
            id = newNestId,
            dragDistances = dragDistances
        )

        // Persist changes
        save()

        return newNestId
    }

    /**
     * Adds a new circle to the specified nest (or the currently selected nest by default).
     *
     * The new circle ID is computed as the next integer after the current maximum circle number
     * in the nest (ignoring the special -1 key). The drag distance for the new circle is
     * initialized using [defaultDragDistance]. The nest list is updated immutably, and the
     * change is recorded via `applyChange` for undo/redo support.
     *
     * @param nestToTouch Optional nest ID to target. If null, the currently selected nest is used.
     */
    fun addCircle(nestToTouch: Int? = null) {

        val nestIdRequested = nestToTouch ?: nestId

        val index = nests.indexOfFirst { it.id == nestIdRequested }
        if (index != -1) {
            val nest = nests[index]

            val newCircleNumber =
                nest.dragDistances
                    .keys
                    .filter { it >= 0 }
                    .maxOrNull()
                    ?.plus(1) ?: 0

            applyChange {
                val updatedNest = nest.copy(
                    dragDistances = nest.dragDistances +
                            (newCircleNumber to defaultDragDistance(newCircleNumber))
                )

                nests[index] = updatedNest
            }
        }
    }


    /**
     * Removes the last added circle from the specified nest (or the currently selected nest by default).
     *
     * The last circle is determined as the one with the highest circle number greater than 0.
     * The nest list is updated immutably, and the change is recorded via `applyChange` for
     * undo/redo support.
     *
     * Safely checks if there is mor than 1 circle to avoid deleting the last one
     *
     * @param nestToTouch Optional nest ID to target. If null, the currently selected nest is used.
     */
    fun removeLastCircle(nestToTouch: Int? = null) {

        val nestIdRequested = nestToTouch ?: nestId
        // Remove last circle
        val index = nests.indexOfFirst { it.id == nestIdRequested }
        if (index != -1) {
            val nest = nests[index]

            val maxCircle =
                nest.dragDistances.keys.filter { k -> k > 0 }
                    .maxOrNull()
                    ?: return

            val updatedDistances = nest.dragDistances - maxCircle
            applyChange {
                nests[index] = nest.copy(dragDistances = updatedDistances)
            }
        }
    }

    fun updatePointPosition(
        point: SwipePointSerializable,
        circles: List<UiCircle>,
        center: Offset,
        pos: Offset,
        snap: Boolean
    ) {
        var haveToApplyToStack = false

        // 1. Compute raw angle from center -> pos
        val dx = pos.x - center.x
        val dy = center.y - pos.y
        var angle = Math.toDegrees(atan2(dx.toDouble(), dy.toDouble()))
        if (angle < 0) angle += 360.0

        // 2. Apply snapping if enabled
        val finalAngle = if (snap) {
            round(angle / SNAP_STEP_DEG) * SNAP_STEP_DEG
        } else {
            angle
        }


        // 3. Find nearest circle based on radius
        val distFromCenter = hypot(dx, dy)
        val closestCircle = circles.minByOrNull { c -> abs(c.radius - distFromCenter) }
            ?: return


        // Only apply to the undo stack if the point coordinates have changed
        if (
            (point.angleDeg != finalAngle) ||
            (point.circleNumber != closestCircle.id)
        ) {
            haveToApplyToStack = true
        }

        if (haveToApplyToStack) applyChange {
            point.angleDeg = finalAngle
            point.circleNumber = closestCircle.id
        }
    }

    // Load points & nests
    LaunchedEffect(Unit, showResetPointsAndNestsDialog) {
        val savedPoints = SwipeSettingsStore.getPoints(ctx)
        points.clear()
        try {
            points.addAll(savedPoints)
        } catch (e: NullPointerException) {
            logE(SWIPE_TAG, "NullPointerException loading swipe points: $e")
            ctx.showToast("NullPointerException loading swipe points: $e")

            // Fallback load them the old way
            try {
                savedPoints.forEach {
                    @Suppress("USELESS_ELVIS")
                    points.add(
                        it.copy(
                            action = it.action
                                ?: SwipeActionSerializable.OpenDragonLauncherSettings()
                        )
                    )
                }
            } catch (e: Exception) {
                logE(SWIPE_TAG, "Fallback loading also failed, clearing all points: $e")
            }
        } catch (e: Exception) {
            logE(SWIPE_TAG, "Error loading swipe points: $e")
            ctx.showToast("Error loading swipe points: $e")
        }

        val savedNests = SwipeSettingsStore.getNests(ctx)
        nests.clear()
        try {
            nests.addAll(savedNests)
        } catch (e: Exception) {
            logE(SWIPE_TAG, "Error loading nests: $e")
            ctx.showToast("Error loading swipe points: $e")
        }
    }


    BackHandler {
        if (isInManualPlacementMode) manualPlacementQueue = emptyList()
        else if (selectedPoint != null) selectedPoint = null
        else if (nestId != 0) nestNavigation.goBack()
        else onBack()
    }

    /**
     * Computes and updates the radii for all circles in the current nest whenever
     * the nest, available width, or center changes.
     *
     * Each circle's radius is proportional to the available width of the container,
     * scaled by circlesWidthIncrement, and distributed evenly so that the largest
     * circle nearly fits the box.
     *
     * This updates the mutable list circles, which is used both for rendering
     * and for hit detection of points on the circles.
     *
     * - `currentNest`: The currently selected nest containing the drag distances for each circle.
     * - `availableWidth`: The width available for drawing the circles, used to scale the radii proportionally.
     * - `center`: The center of the container, used to compute offsets and positions for points.
     */
    LaunchedEffect(currentNest, availableWidth, center) {
        // Base radius scaled to 95% of half the available width
        val baseRadius = availableWidth / 2 * 0.95f

        // Clear previous circles before recomputing
        circles.clear()

        // Iterate over all circle numbers, excluding the special -1 key
        currentNest.dragDistances
            .filter { it.key != -1 }
            .forEach { (circleNumber, _) ->

                // Compute radius proportionally for this circle
                val radius = circlesWidthIncrement * (circleNumber + 1) * baseRadius

                // Add a new UiCircle with computed radius
                circles.add(
                    UiCircle(
                        id = circleNumber,
                        radius = radius,
                    )
                )
            }
    }

    LaunchedEffect(closestHoveredPoint) {
        ableToLaunchHoverAction = false
        closestHoveredPoint?.let {

            val finalOffset = computePointPosition(
                it,
                circles,
                center
            )

            closestHoveredTempOffset = finalOffset

            val startDuration = System.currentTimeMillis()
            while (System.currentTimeMillis() - startDuration < Constants.Settings.HOVER_POINT_DURATION) {
                delay(50L)
            }
            ableToLaunchHoverAction = true
        }
    }

    val subNestDefaultRadius by SwipeMapSettingsStore.subNestDefaultRadius.asState()


    val filteredPoints by remember(points, nestId) {
        derivedStateOf {
            points.filter { it.nestId == nestId }
        }
    }

    val currentFilteredPoints by rememberUpdatedState(filteredPoints)


    // Shows all points, excepted the currently dragged one, if any
    val displayedFilteredPoints by remember(points, isDragging, selectedPoint?.id) {
        derivedStateOf {
            if (!isDragging) points
            else points.filter { it.id != selectedPoint?.id }
        }
    }


    val baseDrawParams = swipeDefaultParams(
        points = points,
        nests = nests,
        backgroundColor = MaterialTheme.colorScheme.background,
        showCircle = true
    )


    val drawParams by remember(
        subNestDefaultRadius,
        iconsVersion,
        points,
        nests,
        displayedFilteredPoints,
        backgroundColor,
        extraColors
    ) {
        derivedStateOf {
            baseDrawParams.copy(
                points = displayedFilteredPoints,
                surfaceColorDraw = backgroundColor,
                extraColors = extraColors
            )
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing.exclude(WindowInsets.ime))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DragonIconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = stringResource(R.string.home)
                    )
                }

                Text(
                    text = stringResource(R.string.swipe_points_selection),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.MiddleEllipsis,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    DragonIconButton(onClick = { showBurgerMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.open_burger_menu)
                        )
                    }

                    DropdownMenu(
                        expanded = showBurgerMenu,
                        onDismissRequest = { showBurgerMenu = false },
                        containerColor = Color.Transparent,
                        shadowElevation = 0.dp,
                        tonalElevation = 0.dp
                    ) {
                        BurgerListAction(
                            actions = listOf(
                                BurgerAction(
                                    onClick = {
                                        showBurgerMenu = false
                                        showEditDefaultPoint = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EditNote,
                                        contentDescription = null
                                    )
                                    Text(stringResource(R.string.edit_default_point_settings))
                                },
                                BurgerAction(
                                    onClick = {
                                        showBurgerMenu = false
                                        appsViewModel.preloadPointIcons(
                                            points = points,
                                            override = true
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null
                                    )
                                    Text(stringResource(R.string.reload_point_icons))
                                },
                                BurgerAction(
                                    onClick = { onNestEdit(currentNest.id) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChangeCircle,
                                        contentDescription = null
                                    )
                                    Text(stringResource(R.string.edit_nest))
                                },
                                BurgerAction(
                                    onClick = {
                                        showBurgerMenu = false
                                        showResetPointsAndNestsDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Restore,
                                        tint = MaterialTheme.colorScheme.error,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = stringResource(R.string.reset_all_points),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        )
                    }

                    DragonIconButton(onClick = onAdvSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            }


            // Main content box: Adapts its size to the screen,
            // computes the circles radii and host the pointer input for the points selection
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .onSizeChanged { size ->
                        /**
                         * Updates the center and available width variables, that depends on the phone size and orientation.
                         * Computes the larger size between width and height to ensure all points belongs to the hittable zone
                         * The visual points and hitboxes are separated due to the need of a precise pointer input and
                         * should be synchronized using the clever [computePointPosition] function that relies on common
                         * center and circles to output the points position on screen
                         */

                        val w = size.width.toFloat()
                        val h = size.height.toFloat()
                        center = Offset(w / 2f, h / 2f)

                        // Use the minimum size between height and width, to prevent the box being untouchable on large displays
                        val usedSize = min(w, h)

                        availableWidth = usedSize - (POINT_RADIUS_PX * 2)  // Safe space for points + padding
                    }
            ) {

                /**
                 * Main Canva, draws the circles, and sub nests by recursivity
                 *
                 * if the user is dragging a point, I draw it in the offset of where the finger is.
                 * if the user has hovered a point for more than 500ms, a radial circle overlay spawns and indicates that
                 * it can release to merge the 2 points
                 */
                Canvas(Modifier.fillMaxSize()) {
                    circlesSettingsOverlay(
                        drawParams = drawParams,
                        center = center,
                        depth = 1,
                        circles = circles,
                        selectedPoint = selectedPoint,
                        nestId = nestId,
                        preventBgErasing = true
                    )

                    if (isDragging && selectedPoint != null) {
                        actionsInCircle(
                            drawParams = drawParams,
                            center = selectedPointTempOffset.value,
                            depth = 1,
                            point = selectedPoint!!,
                            selected = true,
                            preventBgErasing = true
                        )
                    }

                    if (isDragging && closestHoveredTempOffset != null && ableToLaunchHoverAction) {
                        glowOverlay(
                            center = closestHoveredTempOffset!!,
                            color = primaryColor,
                            radius = hoveredPointRadialGradientProgress
                        )
                    }
                }



                Box(
                    Modifier
                        .matchParentSize()
                        .then(
                            if (settingsDebugInfos) {
                                Modifier.background(Color.DarkGray.copy(0.3f))
                            } else Modifier
                        )
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    var closest: SwipePointSerializable? = null
                                    var best = Float.MAX_VALUE

                                    // Can only select points on the same nest
                                    currentFilteredPoints.forEach { p ->
                                        val pointOffset = computePointPosition(
                                            point = p,
                                            circles = circles,
                                            center = center
                                        )
                                        val dist = hypot(offset.x - pointOffset.x, offset.y - pointOffset.y)

                                        if (dist < best) {
                                            best = dist
                                            closest = p
                                        }
                                    }

                                    selectedPoint =
                                        if (best <= TOUCH_THRESHOLD_PX) closest else null

                                    selectedPoint?.let {
                                        lastSelectedCircle = it.circleNumber
                                        isDragging = true
                                        scope.launch {
                                            selectedPointTempOffset.snapTo(offset)
                                        }
                                    }

                                    bannerVisible = selectedPoint != null
                                },
                                onDrag = { change, _ ->
                                    change.consume()

                                    val position = change.position

                                    // Update the selected point offset in real time (the dragging thing)
                                    selectedPoint?.let {
                                        scope.launch {
                                            selectedPointTempOffset.snapTo(position)
                                        }
                                    }


                                    var closest: SwipePointSerializable? = null
                                    var best = Float.MAX_VALUE

                                    // Can only see points on the same nest
                                    currentFilteredPoints.filter { it.id != selectedPoint?.id }.forEach { p ->

                                        val pointOffset = computePointPosition(
                                            point = p,
                                            circles = circles,
                                            center = center
                                        )
                                        val dist = hypot(position.x - pointOffset.x, position.y - pointOffset.y)

                                        if (dist < best) {
                                            best = dist
                                            closest = p
                                        }
                                    }

                                    closestHoveredPoint =
                                        if (best <= TOUCH_THRESHOLD_PX) closest else null

                                },
                                onDragEnd = {
                                    selectedPoint?.let { p ->
                                        val position = selectedPointTempOffset.value

                                        if (ableToLaunchHoverAction && closestHoveredPoint != null) {

                                            // The hovered point
                                            val closest = closestHoveredPoint!!

                                            if (closest.action is SwipeActionSerializable.OpenCircleNest) {
                                                // Put the hovered point in the hovered nest

                                                val targetNestId =
                                                    (closest.action as SwipeActionSerializable.OpenCircleNest).nestId

                                                // Adjust the merged nest circle size if the point belongs to higher circles and the nest has less
                                                nests.find { it.id == targetNestId }?.let { targetNest ->

                                                    // I remove 1 because the dragDistances counts the cancel zone
                                                    val targetNestCircleNumbers = targetNest.dragDistances.size - 1

                                                    // Add 1 because the circle number starts at 0
                                                    val selectedPointCircleNumber = p.circleNumber + 1

                                                    if (selectedPointCircleNumber > targetNestCircleNumbers) {
                                                        repeat(selectedPointCircleNumber - targetNestCircleNumbers) {
                                                            logD(
                                                                NESTS_TAG,
                                                                "Adding a circle to nest n°$targetNestId "
                                                            )
                                                            addCircle(targetNestId)
                                                        }
                                                    }
                                                }

                                                applyChange {
                                                    p.nestId = targetNestId
                                                }

                                            } else {
                                                // Create new nest and put both points in it at 90° and 270° (left and right)
                                                // Tee new nest has only one circle and a Go parent nest in the top, for easier access

                                                applyChange {
                                                    val newNestId = addNewNest(1)

                                                    val newNestPoint = SwipePointSerializable(
                                                        circleNumber = closest.circleNumber,
                                                        angleDeg = closest.angleDeg,
                                                        nestId = closest.nestId,
                                                        action = SwipeActionSerializable.OpenCircleNest(newNestId),
                                                        id = UUID.randomUUID().toString()
                                                    )

                                                    // Creates a new go parent nest that'll be put on top of the nest, to easily exit this nest
                                                    val newGoParentNestPoint = SwipePointSerializable(
                                                        circleNumber = 0,
                                                        angleDeg = 0.0,
                                                        nestId = newNestId,
                                                        action = SwipeActionSerializable.GoParentNest,
                                                        id = UUID.randomUUID().toString()
                                                    )

                                                    points.add(newGoParentNestPoint)

                                                    appsViewModel.reloadPointIcon(newGoParentNestPoint)

                                                    points.add(newNestPoint)


                                                    // Move the 2 points to the new nest and change their position
                                                    p.nestId = newNestId
                                                    p.circleNumber = 0
                                                    p.angleDeg = 270.0


                                                    closest.nestId = newNestId
                                                    closest.circleNumber = 0
                                                    closest.angleDeg = 90.0
                                                }
                                            }

                                            scope.launch {
                                                // Stop dragging, don't animate point as it was merged
                                                isDragging = false
                                                selectedPoint = null
                                                closestHoveredPoint = null
                                            }

                                        } else {
                                            // No merging, just normal dragging

                                            updatePointPosition(
                                                p,
                                                circles,
                                                center,
                                                position,
                                                snapPoints
                                            )

                                            if (autoSeparatePoints) autoSeparate(
                                                points,
                                                nestId,
                                                circles.find { it.id == p.circleNumber },
                                                p
                                            )

                                            // Compute final snapped position
                                            val finalOffset = computePointPosition(
                                                p,
                                                circles,
                                                center
                                            )

                                            scope.launch {
                                                selectedPointTempOffset.animateTo(
                                                    finalOffset,
                                                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                                                )

                                                // Stop dragging
                                                isDragging = false
                                                selectedPoint = null
                                                closestHoveredPoint = null
                                            }
                                        }
                                    }
                                }
                            )
                        }
                        .pointerInput(isInManualPlacementMode) {
                            detectTapGestures(
                                onTap = { offset ->
                                    // Manual placement mode: place the current queued app where user tapped
                                    if (isInManualPlacementMode) {
                                        val action = manualPlacementQueue.first()
                                        val targetCircle =
                                            lastSelectedCircle.coerceAtMost(circleNumber - 1)

                                        // Compute angle from center
                                        val dx = offset.x - center.x
                                        val dy = center.y - offset.y
                                        var angle = Math.toDegrees(atan2(dx.toDouble(), dy.toDouble()))
                                        if (angle < 0) angle += 360.0
                                        val finalAngle = if (snapPoints) {
                                            round(angle / SNAP_STEP_DEG) * SNAP_STEP_DEG
                                        } else angle

                                        // Find nearest circle based on tap distance from center
                                        val distFromCenter = hypot(dx, dy)
                                        val closestCircle = circles.minByOrNull { c ->
                                            abs(c.radius - distFromCenter)
                                        }
                                        val circleId = closestCircle?.id ?: targetCircle

                                        val point = SwipePointSerializable(
                                            id = UUID.randomUUID().toString(),
                                            angleDeg = finalAngle,
                                            action = action,
                                            circleNumber = circleId,
                                            nestId = nestId
                                        )

                                        appsViewModel.reloadPointIcon(point)

                                        applyChange {
                                            points.add(point)
                                            if (autoSeparatePoints) autoSeparate(
                                                points = points,
                                                nestId = nestId,
                                                circle = circles.find { it.id == circleId },
                                                draggedPoint = point
                                            )
                                        }

                                        selectedPoint = point
                                        bannerVisible = true

                                        // Dequeue: move to the next app
                                        manualPlacementQueue = manualPlacementQueue.drop(1)
                                        return@detectTapGestures
                                    }

                                    // Normal tap mode
                                    var tapped: SwipePointSerializable? = null
                                    var best = Float.MAX_VALUE

                                    currentFilteredPoints.forEach { p ->
                                        val circle =
                                            circles.getOrNull(p.circleNumber) ?: return@forEach
                                        val px =
                                            center.x + circle.radius * sin(Math.toRadians(p.angleDeg)).toFloat()
                                        val py =
                                            center.y - circle.radius * cos(Math.toRadians(p.angleDeg)).toFloat()
                                        val dist = hypot(offset.x - px, offset.y - py)

                                        if (dist < best) {
                                            best = dist
                                            tapped = p
                                        }
                                    }

                                    selectedPoint =
                                        if (best <= TOUCH_THRESHOLD_PX)
                                            if (selectedPoint?.id == tapped?.id) {
                                                // Same point tapped -> if circle next, open it, else edit point
                                                if (selectedPoint?.action is SwipeActionSerializable.OpenCircleNest) {
                                                    nestNavigation.goToNest((selectedPoint?.action as SwipeActionSerializable.OpenCircleNest).nestId)
                                                    null
                                                } else {
                                                    showEditDialog = selectedPoint
                                                    tapped
                                                }

                                            } else tapped
                                        else null

                                    selectedPoint?.let { lastSelectedCircle = it.circleNumber }

                                    bannerVisible = selectedPoint != null
                                }
                            )
                        }
                )

            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    val nestToGo =
                        if (selectedPoint?.action is SwipeActionSerializable.OpenCircleNest) {
                            (selectedPoint!!.action as SwipeActionSerializable.OpenCircleNest).nestId
                        } else null

                    val canGoNest = nestToGo != null

                    CircleIconButton(
                        icon = Icons.Filled.AccountCircle,
                        contentDescription = stringResource(R.string.edit_nests),
                        tint = extraColors.goParentNest,
                        padding = 7.dp
                    ) {
                        showNestManagementDialog = true
                    }


                    CircleIconButton(
                        icon = Icons.Filled.Fullscreen,
                        contentDescription = stringResource(R.string.open_nest_circle),
                        tint = extraColors.goParentNest,
                        enabled = canGoNest,
                        padding = 7.dp
                    ) {
                        nestToGo?.let {
                            nestNavigation.goToNest(it)
                            selectedPoint = null
                        }
                    }


                    val canGoback = currentNest.id != 0

                    CircleIconButton(
                        icon = Icons.Filled.FullscreenExit,
                        contentDescription = stringResource(R.string.go_parent_nest),
                        tint = extraColors.goParentNest,
                        enabled = canGoback,
                        padding = 7.dp
                    ) {
                        nestNavigation.goBack()
                        selectedPoint = null
                    }
                }


                val undoButtonEnabled = undoStack.isNotEmpty()
                DragonIconButton(onClick = { undo() }, enabled = undoButtonEnabled) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        tint = MaterialTheme.colorScheme.primary.semiTransparentIfDisabled(undoButtonEnabled),
                        contentDescription = "Undo"
                    )
                }

                val redoButtonEnabled = redoStack.isNotEmpty()
                DragonIconButton(onClick = { redo() }, enabled = redoButtonEnabled) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Redo,
                        tint = MaterialTheme.colorScheme.primary.semiTransparentIfDisabled(redoButtonEnabled),
                        contentDescription = "Redo"
                    )
                }

//            RepeatingPressButton(
//                enabled = undoButtonEnabled,
//                onPress = ::undo
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.Undo,
//                    tint = MaterialTheme.colorScheme.primary.copy(if (undoButtonEnabled) 1f else 0.5f),
//                    contentDescription = "Undo"
//                )
//            }
//
//
//            RepeatingPressButton(
//                enabled = redoButtonEnabled,
//                onPress = ::redo
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.Redo,
//                    contentDescription = "Redo"
//                )
//            }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    icon = Icons.Default.Grid3x3,
                    contentDescription = stringResource(R.string.auto_separate),
                    tint = MaterialTheme.colorScheme.primary,
                    enabled = snapPoints,
                    padding = 10.dp
                ) {
                    scope.launch {
                        UiSettingsStore.snapPoints.set(ctx, !snapPoints)
                    }
                }

                CircleIconButton(
                    icon = Icons.Default.AutoMode,
                    contentDescription = stringResource(R.string.auto_separate),
                    tint = MaterialTheme.colorScheme.primary,
                    enabled = autoSeparatePoints,
                    padding = 10.dp
                ) {
                    scope.launch {
                        UiSettingsStore.autoSeparatePoints.set(ctx, !autoSeparatePoints)
                    }
                }


                RepeatingPressButton(
                    enabled = aPointIsSelected,
                    intervalMs = 35L,
                    onPress = {
                        selectedPoint?.let { point ->
                            applyChange {
                                point.angleDeg = normalizeAngle(point.angleDeg + 1)
                                if (snapPoints) point.angleDeg = point.angleDeg
                                    .toInt()
                                    .toDouble()
                                if (autoSeparatePoints) autoSeparate(
                                    points,
                                    nestId,
                                    circles.find { it.id == point.circleNumber },
                                    point
                                )
                            }
                        }
                    }
                ) {
                    CircleIconButton(
                        icon = Icons.Default.ChevronLeft,
                        contentDescription = stringResource(R.string.move_point_to_left),
                        tint = moveColor,
                        enabled = aPointIsSelected,
                        padding = 10.dp,
                        onClick = null
                    )
                }


                val angleText = if (selectedPoint != null) {
                    "${
                        selectedPoint?.angleDeg?.toBigDecimal()?.setScale(1, RoundingMode.UP)
                            ?.toDouble()
                    }°"
                } else {
                    ""
                }

                Text(
                    text = angleText,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Visible,
                    modifier = Modifier.width(50.dp)
                )

                RepeatingPressButton(
                    enabled = aPointIsSelected,
                    intervalMs = 35L,
                    onPress = {
                        selectedPoint?.let { point ->
                            applyChange {
                                point.angleDeg = normalizeAngle(point.angleDeg - 1)
                                if (snapPoints) point.angleDeg = point.angleDeg
                                    .toInt()
                                    .toDouble()
                                if (autoSeparatePoints) autoSeparate(
                                    points,
                                    nestId,
                                    circles.find { it.id == point.circleNumber },
                                    point
                                )
                            }
                        }
                    }
                ) {
                    CircleIconButton(
                        icon = Icons.Default.ChevronRight,
                        contentDescription = stringResource(R.string.move_point_to_right),
                        tint = moveColor,
                        enabled = aPointIsSelected,
                        padding = 10.dp,
                        onClick = null
                    )
                }
            }


            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {

                CircleIconButton(
                    icon = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_point),
                    tint = MaterialTheme.colorScheme.primary,
                    padding = 20.dp
                ) { showAddDialog = true }



                CircleIconButton(
                    icon = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_point),
                    tint = MaterialTheme.colorScheme.secondary,
                    enabled = aPointIsSelected,
                    padding = 20.dp
                ) {
                    showEditDialog = selectedPoint
                }


                CircleIconButton(
                    icon = Icons.Default.Remove,
                    contentDescription = stringResource(R.string.remove_point),
                    tint = MaterialTheme.colorScheme.error,
                    enabled = aPointIsSelected,
                    padding = 20.dp
                ) {
                    selectedPoint?.let { point ->
                        val index = points.indexOfFirst { it.id == point.id }
                        if (index >= 0) {
                            applyChange {
                                points.removeAt(index)
                            }
                        }
                        selectedPoint = null
                    }
                }


                CircleIconButton(
                    icon = Icons.Default.ContentCopy,
                    contentDescription = stringResource(R.string.copy_point),
                    enabled = aPointIsSelected,
                    tint = copyColor,
                    padding = 20.dp
                ) {
                    selectedPoint?.let { oldPoint ->
                        val newPoint = oldPoint.copy(
                            id = UUID.randomUUID().toString(),
                        )

                        appsViewModel.reloadPointIcon(newPoint)

                        applyChange {
                            points.add(newPoint)
                            autoSeparate(
                                points,
                                nestId,
                                circles.find { it.id == newPoint.circleNumber },
                                newPoint
                            )
                        }
                        selectedPoint = newPoint
                    }
                }



                Column(
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    CircleIconButton(
                        text = "+1",
                        contentDescription = stringResource(R.string.add_circle),
                        padding = 7.dp,
                        tint = addRemoveCirclesColor,
                        modifier = Modifier.size(40.dp)
                    ) { addCircle() }


                    val canRemoveCircle = circleNumber > 1


                    CircleIconButton(
                        text = "-1",
                        contentDescription = stringResource(R.string.remove_circle),
                        padding = 7.dp,
                        enabled = canRemoveCircle,
                        tint = addRemoveCirclesColor,
                        modifier = Modifier.size(40.dp)
                    ) { removeLastCircle() }
                }
            }
        }

        // Only show the toggler if user has a point that opens a nest, otherwise it may be confusing
        AnimatedVisibility(
            visible = points.any { it.action is SwipeActionSerializable.OpenCircleNest }
        ) {
            SettingsSlider(
                setting = SwipeMapSettingsStore.subNestDefaultRadius,
                title = "",
                valueRange = 0..50,
                modifier = Modifier
                    .height(50.dp)
                    .width(150.dp)
                    .offset(x = 20.dp, y = 50.dp)
            )
        }
    }

    if (showAddDialog) {
        AddPointDialog(
            onNewNest = ::addNewNest,
            onDismiss = {
                showAddDialog = false
            },
            onActionSelected = { action ->
                val circleNumber = lastSelectedCircle.coerceAtMost(circleNumber - 1)
                val newAngle =
                    randomFreeAngle(circles.find { it.id == circleNumber }, points) ?: run {
                        ctx.showToast("Error: no circle belonging to this point found")
                        return@AddPointDialog
                    }


                // Create a new swipe point, ids are still random, I think I'll keep it that way
                // unless I really have to manage them correctly
                val newPoint = SwipePointSerializable(
                    id = UUID.randomUUID().toString(),
                    angleDeg = newAngle,
                    action = action,
                    circleNumber = circleNumber,
                    nestId = nestId
                )

                appsViewModel.reloadPointIcon(newPoint)

                applyChange {
                    points.add(newPoint)
                    autoSeparate(
                        points,
                        nestId,
                        circles.find { it.id == newPoint.circleNumber },
                        newPoint
                    )
                }

                selectedPoint = newPoint
                showAddDialog = false
            },
            onMultipleActionsSelected = { actions, autoPlace ->
                val targetCircle = lastSelectedCircle.coerceAtMost(circleNumber - 1)
                val circle = circles.find { it.id == targetCircle }

                if (autoPlace) {
                    // Auto-place all apps evenly on the circle
                    applyChange {
                        for (action in actions) {
                            val newAngle = randomFreeAngle(circle, points) ?: continue

                            val point = SwipePointSerializable(
                                id = UUID.randomUUID().toString(),
                                angleDeg = newAngle,
                                action = action,
                                circleNumber = targetCircle,
                                nestId = nestId
                            )

                            appsViewModel.reloadPointIcon(point)

                            points.add(point)
                            autoSeparate(points, nestId, circle, point)
                        }
                    }
                    ctx.showToast(ctx.getString(R.string.apps_added_successfully, actions.size))
                } else {
                    // Manual placement: queue actions and let user tap to place each one
                    manualPlacementQueue = actions
                    ctx.showToast(ctx.getString(R.string.tap_circle_to_place))
                }
                showAddDialog = false
            }
        )
    }

    if (showEditDialog != null) {
        val editPoint = showEditDialog!!

        EditPointDialog(
            point = editPoint,
            onDismiss = {
                showEditDialog = null
                appsViewModel.reloadPointIcon(editPoint)
            },
        ) { newPoint ->
            appsViewModel.reloadPointIcon(newPoint)

            applyChange {
                val index = points.indexOfFirst { it.id == editPoint.id }
                if (index >= 0) {
                    points[index] = newPoint
                }
            }
            selectedPoint = newPoint
            showEditDialog = null
        }
    }


    if (showNestManagementDialog) {
        NestManagementDialog(
            onDismissRequest = { showNestManagementDialog = false }, onNewNest = ::addNewNest,
            nests = nests,
            onNameChange = null /*{ id, newName ->
                applyChange {
                    pendingNestUpdate = nests.map {
                        if (it.id == id) it.copy(name = newName)
                        else it
                    }
                }
            }*/,
            onDelete = { nestToDelete ->
                applyChange {
                    // Delete nest, leave points on it for now
                    val index = nests.indexOfFirst { it.id == nestToDelete }

                    if (index != -1) {
                        nests -= nests[index]
                    }
                }
            },
            onSelect = {
                nestNavigation.goToNest(it.id)
                showNestManagementDialog = false
            }
        )
    }

    if (selectedPoint != null) {
        val currentPoint = selectedPoint!!
        AppPreviewTitle(
            offsetY = offsetY,
            alpha = alpha,
            point = currentPoint,
            topPadding = 80.dp,
            labelSize = appLabelOverlaySize,
            iconSize = appIconOverlaySize,
            showLabel = true,
            showIcon = true
        )
    }

    // Manual placement mode banner
    if (isInManualPlacementMode) {
        val appName = when (val currentAction = manualPlacementQueue.first()) {
            is SwipeActionSerializable.LaunchApp -> {
                ctx.packageManager.runCatching {
                    getApplicationLabel(
                        getApplicationInfo(currentAction.packageName, 0)
                    ).toString()
                }.getOrDefault(currentAction.packageName)
            }

            else -> currentAction::class.simpleName ?: ""
        }
        val remaining = manualPlacementQueue.size

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.place_app_where, appName),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
                Text(
                    text = stringResource(R.string.multi_select_count, remaining),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Text(
                    text = stringResource(R.string.tap_circle_to_place),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
            }
        }
    }

    if (showEditDefaultPoint) {
        EditPointDialog(
            point = defaultPoint,
            isDefaultEditing = true,
            onDismiss = {
                showEditDefaultPoint = false
            },
        ) {
            scope.launch {
                SwipeSettingsStore.setDefaultPoint(ctx, it)
                reloadIcons()
            }
            showEditDefaultPoint = false
        }
    }

    if (showResetPointsAndNestsDialog) {
        UserValidation(
            title = stringResource(R.string.reset_all_points),
            message = stringResource(R.string.reset_all_points_desc),
            onDismiss = { showResetPointsAndNestsDialog = false }
        ) {
            scope.launch {
                SwipeSettingsStore.resetAll(ctx)
                selectedPoint = null
                showResetPointsAndNestsDialog = false
            }
        }
    }


    /**
     * Debug Infos section
     * Shows various information about the current settings state, may be unreadable when lots of points
     */
    if (settingsDebugInfos) {
        DragonColumnGroup {
            Text("nests id: $nestId")
            Text("current nests id: ${currentNest.id}")
            Text("nests number: ${nests.size}")
            Text("circle number: $circleNumber")
            Text("currentNest size: ${currentNest.dragDistances.size}")
            Text("circle width incr: $circlesWidthIncrement")
            Text("current dragDistances: ${currentNest.dragDistances}")
            Text("closest hovered point: $closestHoveredTempOffset")
            Text("current nest: $currentNest")

            selectedPoint?.let { Text(it.toString()) }
        }
    }
}

fun defaultDragDistance(id: Int): Int = when (id) {
    -1 -> 150 // Cancel Zone (below no action activation)
    0 -> 300  // First circle 300
    else -> 300 + 150 * id // others: add 150 each, don't be dumb and go to 10 circles
}
