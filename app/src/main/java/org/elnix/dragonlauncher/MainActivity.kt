package org.elnix.dragonlauncher

import android.annotation.SuppressLint
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.elnix.dragonlauncher.common.R
import org.elnix.dragonlauncher.common.logging.DragonLogManager
import org.elnix.dragonlauncher.common.logging.logD
import org.elnix.dragonlauncher.common.logging.logE
import org.elnix.dragonlauncher.common.logging.logI
import org.elnix.dragonlauncher.common.logging.logW
import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable
import org.elnix.dragonlauncher.common.serializables.SwipePointSerializable
import org.elnix.dragonlauncher.common.utils.Constants.Logging.WIDGET_TAG
import org.elnix.dragonlauncher.common.utils.Constants.Navigation.ignoredReturnRoutes
import org.elnix.dragonlauncher.common.utils.Constants.Settings.HOME_REENTER_WINDOW_MS
import org.elnix.dragonlauncher.common.utils.PrivateSpaceUtils
import org.elnix.dragonlauncher.common.utils.ROUTES
import org.elnix.dragonlauncher.common.utils.SamsungWorkspaceIntegration
import org.elnix.dragonlauncher.common.utils.WidgetHostProvider
import org.elnix.dragonlauncher.common.utils.showToast
import org.elnix.dragonlauncher.models.AppLifecycleViewModel
import org.elnix.dragonlauncher.models.BackupViewModel
import org.elnix.dragonlauncher.models.FloatingAppsViewModel
import org.elnix.dragonlauncher.settings.SettingsBackupManager
import org.elnix.dragonlauncher.settings.stores.BehaviorSettingsStore
import org.elnix.dragonlauncher.settings.stores.PrivateSettingsStore
import org.elnix.dragonlauncher.settings.stores.SwipeSettingsStore
import org.elnix.dragonlauncher.settings.stores.UiSettingsStore
import org.elnix.dragonlauncher.ui.MainAppUi
import org.elnix.dragonlauncher.ui.components.settings.asState
import org.elnix.dragonlauncher.ui.components.settings.asStateNull
import org.elnix.dragonlauncher.ui.remembers.LocalAppLifecycleViewModel
import org.elnix.dragonlauncher.ui.remembers.LocalAppsViewModel
import org.elnix.dragonlauncher.ui.remembers.LocalBackupViewModel
import org.elnix.dragonlauncher.ui.remembers.LocalFloatingAppsViewModel
import org.elnix.dragonlauncher.ui.theme.DragonLauncherTheme
import org.elnix.dragonlauncher.ui.widgets.LauncherWidgetHolder
import java.util.UUID

class MainActivity : FragmentActivity(), WidgetHostProvider {

    private val appLifecycleViewModel: AppLifecycleViewModel by viewModels()
    private val backupViewModel: BackupViewModel by viewModels()
    private val floatingAppsViewModel: FloatingAppsViewModel by viewModels()

    private var navControllerHolder = mutableStateOf<NavHostController?>(null)

    companion object {
        private var GLOBAL_APPWIDGET_HOST: AppWidgetHost? = null
        private const val REQUEST_WIDGET_CONFIG = 1001
    }


    val appWidgetHost: AppWidgetHost by lazy {
        GLOBAL_APPWIDGET_HOST ?: AppWidgetHost(this, R.id.appwidget_host_id).also {
            GLOBAL_APPWIDGET_HOST = it
        }
    }

    private val widgetHolder by lazy { LauncherWidgetHolder.getInstance(this) }

    override fun createAppWidgetView(widgetId: Int): AppWidgetHostView? {
        val info = getAppWidgetInfo(widgetId) ?: return null
        return widgetHolder.createView(widgetId, info)
    }

    override fun getAppWidgetInfo(widgetId: Int): AppWidgetProviderInfo? {
        return widgetHolder.getAppWidgetInfo(widgetId)
    }

    private val appWidgetManager by lazy {
        AppWidgetManager.getInstance(this)
    }


    private var pendingBindWidgetId: Int? = null
    private var pendingAddNestId: Int? = null
    private var pendingBindProvider: ComponentName? = null
    private var pendingConfigWidgetId: Int = -1


    private val widgetPickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            logD(WIDGET_TAG, "widgetPickerLauncher resultCode=${result.resultCode}")
            val widgetId = result.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                ?: return@registerForActivityResult

            addWidgetsWithId(widgetId)

        }


    private fun addWidgetsWithId(widgetId: Int) {
        logD(WIDGET_TAG, "Picked widgetId=$widgetId")
        val info = widgetHolder.getAppWidgetInfo(widgetId)
        if (info == null) {
            logW(WIDGET_TAG, "No AppWidgetInfo for widgetId=$widgetId, deleting...")
            widgetHolder.deleteAppWidgetId(widgetId)
            return
        }

        // Try to bind silently
        if (appWidgetManager.bindAppWidgetIdIfAllowed(widgetId, info.provider)) {
            logD(WIDGET_TAG, "bindAppWidgetIdIfAllowed=true, proceeding")
            proceedAfterBind(widgetId, info)
        } else {
            logD(WIDGET_TAG, "bindAppWidgetIdIfAllowed=false, launching bind consent")
            pendingBindWidgetId = widgetId
            pendingBindProvider = info.provider

            val bindIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, info.provider)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS, Bundle())
            }
            widgetBindLauncher.launch(bindIntent)
        }
    }

    fun bindWidgetFromCustomPicker(
        widgetId: Int,
        provider: ComponentName
    ) {
        logD(WIDGET_TAG, "DRAGON_FLOW: Starting bind process from picker for ID $widgetId")
        lifecycleScope.launch {
//            val forceBinding = DebugSettingsStore.forceAppWidgetsBinding.get(this@MainActivity)
//            logD(WIDGET_TAG, "DRAGON_FLOW: Force binding setting=$forceBinding")

            // First try to bind the widget to the ID
//            val bound =
//                if (forceBinding) {
//                    logD(WIDGET_TAG, "DRAGON_FLOW: Skipping direct bind due to forceBinding=true")
//                    false
//                } else {
//                    widgetHolder.bindWidget(widgetId, provider)
//                }

            // Testing feature, since GH widget work only with this feature enabled it may require it so I'll keep it as default

//            if (bound) {
//                // Retrieve full info only AFTER successful bind
//                val info = widgetHolder.getAppWidgetInfo(widgetId)
//
//                if (info != null) {
//                    logD(WIDGET_TAG, "DRAGON_FLOW: Widget $widgetId bound successfully. Proceeding...")
//                    proceedAfterBind(widgetId, info)
//                } else {
//                   logW(WIDGET_TAG, "DRAGON_FLOW: Bound OK but info is NULL for ID $widgetId, deleting")
//                    widgetHolder.deleteAppWidgetId(widgetId)
//                }
//            } else {
                // Need user consent to bind
//                logD(WIDGET_TAG, "DRAGON_FLOW: Binding REJECTED (Wait for consent). Launching ACTION_APPWIDGET_BIND for $widgetId")
                pendingBindWidgetId = widgetId
                pendingBindProvider = provider
                val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider)
                }
                widgetBindLauncher.launch(intent)
//            }
        }
    }


    private val widgetBindLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val widgetId = pendingBindWidgetId
            val provider = pendingBindProvider

            logD(WIDGET_TAG, "DRAGON_FLOW: ActionBind finished with resultCode=${result.resultCode} for ID $widgetId")

            if (widgetId == null || provider == null) {
               logW(WIDGET_TAG, "DRAGON_FLOW: Pending data lost during activity transition!")
                return@registerForActivityResult
            }

            pendingBindWidgetId = null
            pendingBindProvider = null

            lifecycleScope.launch {
                logD(WIDGET_TAG, "DRAGON_FLOW: Waiting for OS to sync bind state...")
                // Wait a short time for system to finish binding
                var bound = false
                repeat(5) { attempt ->
                    val info = try {
                        widgetHolder.getAppWidgetInfo(widgetId)
                    } catch (e: SecurityException) {
                       logW(
                            WIDGET_TAG,
                            "DRAGON_FLOW: SecurityException on attempt $attempt for ID $widgetId: ${e.message}"
                        )
                        null
                    }

                    if (info != null) {
                        logD(WIDGET_TAG, "DRAGON_FLOW: Sync successful on attempt $attempt! Info found.")
                        bound = true
                        return@repeat
                    }

                    delay(300)
                }

                if (bound) {
                    logD(WIDGET_TAG, "DRAGON_FLOW: Widget bound after consent. Proceeding...")
                    widgetHolder.getAppWidgetInfo(widgetId)?.let { info ->
                        proceedAfterBind(widgetId, info)
                    } ?: run {
                       logW(WIDGET_TAG, "DRAGON_FLOW: Critical - Info missing for bound ID $widgetId")
                        widgetHolder.deleteAppWidgetId(widgetId)
                    }
                } else {
                   logW(WIDGET_TAG, "DRAGON_FLOW: Bind FAILED after consent. ID $widgetId was not blessed by system.")
                    showToast("Binding failed. Check Xiaomi 'Add Shortcut' permission.")
                    widgetHolder.deleteAppWidgetId(widgetId)
                }
            }
        }


    /**
     * I struggled so much to achieve to something that works in most cases I don't want to change that
     */
    private fun proceedAfterBind(widgetId: Int, info: AppWidgetProviderInfo) {
        logD(WIDGET_TAG, "DRAGON_FLOW: proceedAfterBind for ID $widgetId, provider=${info.provider}")

        if (info.configure != null) {
            logD(WIDGET_TAG, "DRAGON_FLOW: Provider requires configuration. Launching via Host Proxy...")
            pendingConfigWidgetId = widgetId
            try {
                // Use the official AppWidgetHost proxy to launch configuration.
                // This is REQUIRED for widgets with non-exported configuration activities (like GitHub).
                appWidgetHost.startAppWidgetConfigureActivityForResult(
                    this,
                    widgetId,
                    0,
                    REQUEST_WIDGET_CONFIG,
                    null
                )
            } catch (e: Exception) {
                logE(WIDGET_TAG, "DRAGON_FLOW: Proxy launch failed: ${e.message}")
                showToast("Failed to launch configuration")
                // Add it anyway if config fails to launch
                floatingAppsViewModel.addFloatingApp(
                    SwipeActionSerializable.OpenWidget(widgetId, info.provider),
                    info,
                    pendingAddNestId ?: 0
                )
            }
        } else {
            logD(WIDGET_TAG, "DRAGON_FLOW: No configuration needed, adding widget")
            floatingAppsViewModel.addFloatingApp(
                SwipeActionSerializable.OpenWidget(widgetId, info.provider),
                info,
                pendingAddNestId ?: 0
            )
        }

        pendingAddNestId =  null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_WIDGET_CONFIG) {
            val widgetId = data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingConfigWidgetId) ?: pendingConfigWidgetId
            logD(WIDGET_TAG, "DRAGON_FLOW: Proxy config finished for ID $widgetId, result=$resultCode")

            if (resultCode == RESULT_OK && widgetId != -1) {
                val info = widgetHolder.getAppWidgetInfo(widgetId)
                if (info != null) {
                    floatingAppsViewModel.addFloatingApp(
                        SwipeActionSerializable.OpenWidget(widgetId, info.provider),
                        info,
                        0
                    )
                }
            } else if (widgetId != -1) {
                // User canceled or config failed, clean up the ID
                widgetHolder.deleteAppWidgetId(widgetId)
            }
            pendingConfigWidgetId = -1
        }
    }

    fun launchWidgetPicker() {
        val widgetId = widgetHolder.allocateAppWidgetId()
        val pickIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        widgetPickerLauncher.launch(pickIntent)
    }

    /**
     * Deletes a widget ID and removes it from the host.
     */
    fun deleteWidget(widgetId: Int) {
        widgetHolder.deleteAppWidgetId(widgetId)
    }

    private val packageReceiver = PackageReceiver()
    private val filter = IntentFilter().apply {
        addAction(Intent.ACTION_PACKAGE_ADDED)
        addAction(Intent.ACTION_PACKAGE_REMOVED)
        addAction(Intent.ACTION_PACKAGE_REPLACED)
        addAction(Intent.ACTION_PACKAGES_SUSPENDED)
        addAction(Intent.ACTION_PACKAGES_UNSUSPENDED)
        addAction(Intent.ACTION_PACKAGE_CHANGED)
        addDataScheme("package")
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        val startTime = System.currentTimeMillis()
        // Use hardware acceleration ASAP
        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )

        super.onCreate(savedInstanceState)
        logI("StartupPerf", "MainActivity.onCreate started")

        // Initialize logging & other background tasks asynchronously
        org.elnix.dragonlauncher.common.utils.AsyncInitializer.init(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(packageReceiver, filter, RECEIVER_EXPORTED)
        }

        appWidgetHost.startListening()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Force launch of full viewmodel after first frame for performance
        // This avoids layout & loading overlap
        lifecycleScope.launch(Dispatchers.Main) {
            yield() // Wait for first frame
            logI("StartupPerf", "First frame rendered in ${System.currentTimeMillis() - startTime}ms. Starting AppsViewModel.loadAll().")
            (applicationContext as MyApplication).appsViewModel.loadAll()
            logI("StartupPerf", "AppsViewModel.loadAll() finished at ${System.currentTimeMillis() - startTime}ms total.")
        }

        setContent {
            val ctx = LocalContext.current

            val scope = rememberCoroutineScope()
            val lifecycleOwner = LocalLifecycleOwner.current

            val appsViewModel = remember(ctx) {
                (ctx.applicationContext as MyApplication).appsViewModel
            }

            // Used internally by the app view model
            appsViewModel.cacheDensity(LocalDensity.current)

            // Launch full viewmodel after first frame for performance
            // LaunchedEffect(Unit) {
            //     yield()
            //     appsViewModel.loadAll()
            // }

            // May be used in the future for some quit action / operation
            // DoubleBackToExit()

            // Used to visually block private space content on window quit, and if user locks his phone,
            // the apps are also visually blocked, since they can't be launched
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (
                        event == Lifecycle.Event.ON_RESUME &&
                        PrivateSpaceUtils.isPrivateSpaceSupported()
                    ) {
                        val locked = PrivateSpaceUtils.isPrivateSpaceLocked(ctx) ?: false

                        // If private space is locked on return, set it unavailable on the viewmodel state
                        if (locked) {
                            appsViewModel.setPrivateSpaceLocked()
                        } else { // Set it available
//                            appsViewModel.setPrivateSpaceAvailable()

                            scope.launch(Dispatchers.IO) {
                                appsViewModel.unlockAndReloadPrivateSpace()
                            }
                        }
                    }
                }

                // Add the observer to the lifecycle
                lifecycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }


            val keepScreenOn by BehaviorSettingsStore.keepScreenOn.asState()
            val fullscreen by UiSettingsStore.fullScreen.asState()
            val hasInitialized by PrivateSettingsStore.hasInitialized.asStateNull()
            val samsungPreferSecureFolder by PrivateSettingsStore.samsungPreferSecureFolder.asState()


            LaunchedEffect(Unit) {
                appLifecycleViewModel.privateSpaceUnlockRequestEvents.collect {

                    val openPrivateSpace = {
                        ctx.logI("SamsungIntegration", "Using standard Android Private Space")
                        ctx.startActivity(
                            Intent(ctx, PrivateSpaceUnlockActivity::class.java)
                        )
                    }

                    ctx.logI(
                        "SamsungIntegration",
                        "Loading Samsung preference: $samsungPreferSecureFolder"
                    )
                    val useSecureFolder = SamsungWorkspaceIntegration.resolveUseSecureFolder(
                        context = ctx,
                        preferenceEnabled = samsungPreferSecureFolder
                    )

                    ctx.logI(
                        "SamsungIntegration",
                        "Using system: ${if (useSecureFolder) "Secure Folder" else "Private Space"}"
                    )

                    if (useSecureFolder) {
                        SamsungWorkspaceIntegration.openSecureFolder(
                            context = ctx,
                            onFallback = openPrivateSpace
                        )
                    } else {
                        openPrivateSpace()
                    }
                }
            }


            val window = this@MainActivity.window
            val controller = WindowInsetsControllerCompat(window, window.decorView)

            LaunchedEffect(keepScreenOn) {
                if (keepScreenOn) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            }

            LaunchedEffect(Unit, fullscreen) {
                if (fullscreen) {
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                } else {
                    controller.show(WindowInsetsCompat.Type.systemBars())
                }
            }


            LaunchedEffect(hasInitialized) {
                if (hasInitialized == false) {

                    /* ───────────── Create the 3 default points (has to be changed ───────────── */
                    SwipeSettingsStore.savePoints(
                        ctx,
                        listOf(
                            SwipePointSerializable(
                                circleNumber = 0,
                                angleDeg = 0.toDouble(),
                                action = SwipeActionSerializable.OpenAppDrawer(),
                                id = UUID.randomUUID().toString()
                            ),
                            SwipePointSerializable(
                                circleNumber = 1,
                                angleDeg = 200.toDouble(),
                                action = SwipeActionSerializable.NotificationShade,
                                id = UUID.randomUUID().toString()
                            ),
                            SwipePointSerializable(
                                circleNumber = 1,
                                angleDeg = 160.toDouble(),
                                action = SwipeActionSerializable.ControlPanel,
                                id = UUID.randomUUID().toString()
                            )
                        )
                    )

                    /* ───────────── Finally, initialize ───────────── */
                    PrivateSettingsStore.hasInitialized.set(ctx, true)
                }
            }

            DragonLauncherTheme {

                val navController = rememberNavController()
                navControllerHolder.value = navController


                CompositionLocalProvider(
                    LocalAppsViewModel provides appsViewModel,
                    LocalAppLifecycleViewModel provides appLifecycleViewModel,
                    LocalBackupViewModel provides backupViewModel,
                    LocalFloatingAppsViewModel provides floatingAppsViewModel
                ) {
                    MainAppUi(
                        widgetHostProvider = this,
                        navController = navController,
                        onBindCustomWidget = { widgetId, provider, nestId ->
                            pendingAddNestId = nestId
                            (ctx as MainActivity).bindWidgetFromCustomPicker(widgetId, provider)
                        },
                        onLaunchSystemWidgetPicker = { nestId ->
                            pendingAddNestId = nestId
                            (ctx as MainActivity).launchWidgetPicker()
                        },
                        onResetWidgetSize = { id, widgetId ->
                            val info = appWidgetManager.getAppWidgetInfo(widgetId)
                            floatingAppsViewModel.resetFloatingAppSize(id, info)
                        },
                        onRemoveFloatingApp = { floatingAppObject ->
                            floatingAppsViewModel.removeFloatingApp(floatingAppObject.id) {
                                (ctx as MainActivity).deleteWidget(it)
                            }
                        }
                    )
                }
            }
        }
    }


    // For the home action, to prevent it to work TOO MUCH (I tested and it was launching
    // the action everytime I clicked on the home button/gesture lol
    var pauseTime: Long = 0L
    var isNewHomeIntent: Boolean = false


    override fun onPause() {
        super.onPause()

        /* ────────────────  Home detection actions ──────────────── */
        pauseTime = SystemClock.uptimeMillis()


        /* ──────────────── Returns back to home if outside for too long ─────────────────── */
        appLifecycleViewModel.onPause()
        lifecycleScope.launch {
            SettingsBackupManager.triggerBackup(this@MainActivity)
        }
    }

    override fun onResume() {
        super.onResume()


        /* ────────────────  Home detection actions ──────────────── */
        val now = SystemClock.uptimeMillis()
        val delta = now - pauseTime

        if (
            isNewHomeIntent &&
            delta in 1..HOME_REENTER_WINDOW_MS
        ) {
            // HOME pressed while launcher already visible
            isNewHomeIntent = false
            appLifecycleViewModel.launchHomeAction()
        }

        isNewHomeIntent = false


        /* ──────────────── Returns back to home if outside for too long ─────────────────── */
        lifecycleScope.launch {
            SettingsBackupManager.triggerBackup(this@MainActivity)
        }

        val currentRoute = navControllerHolder.value
            ?.currentBackStackEntry
            ?.destination
            ?.route

        // If user was outside > 10s, and not in the ignored list
        if (appLifecycleViewModel.resume(10_000) && currentRoute !in ignoredReturnRoutes) {
            navControllerHolder.value?.navigate(ROUTES.MAIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)


        /* Detects if the new Intent is the launcher one, and set the pending value to true */
        if (
            intent.action == Intent.ACTION_MAIN &&
            intent.hasCategory(Intent.CATEGORY_HOME)
        ) {
            isNewHomeIntent = true
            logD("HomeAction", "HOME intent received (pending)")
        }
    }

    override fun onStart() {
        super.onStart()
        appWidgetHost.startListening()
    }


    override fun onStop() {
        super.onStop()
        appWidgetHost.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(packageReceiver)
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        lifecycleScope.launch {
            SettingsBackupManager.triggerBackup(this@MainActivity)
        }

        // Widgets
        GLOBAL_APPWIDGET_HOST = null
    }
}
