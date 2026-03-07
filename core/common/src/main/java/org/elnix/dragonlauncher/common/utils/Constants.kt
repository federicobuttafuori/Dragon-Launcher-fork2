package org.elnix.dragonlauncher.common.utils

import org.elnix.dragonlauncher.common.serializables.SwipeActionSerializable

const val obtainiumPackageName = "dev.imranr.obtainium.fdroid"


object Constants {
    object PackageNameLists {
        val systemLaunchers = listOf(
            // Xiaomi/RedMagic/HyperOS/MIUI
            "com.miui.home",
            "com.miui.home.launcher",
            "com.zui.launcher",
            "com.redmagic.launcher",

            // Samsung OneUI
            "com.sec.android.app.launcher",
            "com.samsung.android.app.launcher",

            // ZTE/Nubia
            "com.zte.mifavor.launcher",
            "com.android.nubialauncher",

            // OnePlus OxygenOS/ColorOS
            "com.oneplus.launcher",
            "com.oplus.launcher",

            // OPPO/Realme
            "com.oppo.launcher",
            "com.coloros.safecenter.launcher",

            // Huawei EMUI/HarmonyOS
            "com.huawei.android.launcher",
            "com.huawei.android.home",

            // Google Pixel/Stock Android
            "com.google.android.apps.nexuslauncher",
            "com.android.launcher3",

            // Sony
            "com.sonymobile.home",

            // LG
            "com.lge.launcher2",
            "com.lge.launcher3",

            // HTC
            "com.htc.launcher",

            // Motorola
            "com.motorola.blur.launcher",

            // Vivo FuntouchOS
            "com.iuni.launcher",

            // Nothing OS
            "com.nothing.launcher",

            // Fairphone
            "ch.fairphone.launcher"
        )


        /**
         * Known social media package names for auto-detection
         */
        val knownSocialMediaApps = setOf(
            // Meta
            "com.instagram.android",
            "com.facebook.katana",
            "com.facebook.orca", // Messenger
            "com.whatsapp",
            "com.facebook.lite",

            // ByteDance
            "com.zhiliaoapp.musically", // TikTok
            "com.ss.android.ugc.trill", // TikTok (alternate)

            // Snap
            "com.snapchat.android",

            // Twitter/X
            "com.twitter.android",
            "com.twitter.android.lite",

            // Reddit
            "com.reddit.frontpage",

            // Pinterest
            "com.pinterest",

            // LinkedIn
            "com.linkedin.android",

            // Telegram
            "org.telegram.messenger",
            "org.telegram.messenger.web",

            // Discord
            "com.discord",
            "com.aliucord",

            // BeReal
            "com.bereal.ft",

            // Threads
            "com.instagram.barcelona",

            // YouTube (can be considered social)
            "com.google.android.youtube",

            // Twitch
            "tv.twitch.android.app",

            // Tumblr
            "com.tumblr",

            // WeChat
            "com.tencent.mm"
        )
    }

    object Navigation {
        /** List of routes that the routes killer ignores when the user leave the app for too long, usually files pickers */
        val ignoredReturnRoutes = listOf(
            ROUTES.WELCOME,
            SETTINGS.BACKUP,
            SETTINGS.WALLPAPER,
            SETTINGS.FLOATING_APPS
        )

        val transparentScreens = listOf(
            ROUTES.MAIN,
            ROUTES.DRAWER,
            SETTINGS.WALLPAPER,
            SETTINGS.FLOATING_APPS
        )
    }

    object Paths {

        /* Themes loader utils */
        const val themesDir = "themes"
        val imageExts = listOf("png", "jpg", "jpeg", "webp")
    }

    object Actions {
        val defaultChoosableActions = listOf(
            SwipeActionSerializable.OpenCircleNest(0),
            SwipeActionSerializable.GoParentNest,
            SwipeActionSerializable.LaunchApp("", false,0),
            SwipeActionSerializable.LaunchShortcut("", ""),
            SwipeActionSerializable.OpenUrl(""),
            SwipeActionSerializable.OpenFile(""),
            SwipeActionSerializable.NotificationShade,
            SwipeActionSerializable.ControlPanel,
            SwipeActionSerializable.OpenAppDrawer(),
            SwipeActionSerializable.Lock,
            SwipeActionSerializable.ReloadApps,
            SwipeActionSerializable.OpenRecentApps,
            SwipeActionSerializable.OpenDragonLauncherSettings()
        )
    }

    object Logging {
        /*  ─────────────  Tags constants  ─────────────  */
        const val TAG = "DragonLauncherDebug"
        const val APPS_TAG = "AppsVm"
        const val ICONS_TAG = "IconsDebug"
        const val BACKUP_TAG = "SettingsBackupManager"
        const val SWIPE_TAG = "SwipeDebug"
        const val WIDGET_TAG = "WidgetsDebug"
        const val FLOATING_APPS_TAG = "FloatingAppsDebug"
        const val ACCESSIBILITY_TAG = "SystemControl"
        const val IMAGE_TAG = "ImageDebug"
        const val SETTINGS_TAG = "SettingsDebug"
        const val ICON_PACK_TAG = "IconsPacks"
        const val SHAPES_TAG = "ShapesDebug"
        const val COLORS_TAG = "ColorsDebug"
        const val DRAWER_TAG = "DrawerDebug"
        const val APP_LAUNCH_TAG = "AppLaunchDebug"
        const val PRIVATE_SPACE_TAG = "PrivateSpaceDebug"
        const val APPS_JSON_TAG = "AppsJsonDebug"
        const val PM_COMPAT_TAG = "PmCompatDebug"
        const val BROADCAST_TAG = "BroadcastDebug"
        const val POINTS_TAG = "PointsDebug"
        const val STATUS_BAR_TAG = "StatusBarDebug"
        const val WORKSPACES_TAG = "WorkspacesDebug"
        const val NESTS_TAG = "NestsDebug"
        const val ANGLE_LINE_TAG = "AngleLineDebug"
        const val LAUNCHER_WIDGET_HOLDER_TAG = "LauncherWidgetHolder"
    }

    /*  ─────────────  Links  ─────────────  */
    object Links {

        const val discordInviteLink = "https://discord.gg/6UyuP8EBWS"
    }

    object Settings {
        /*  ─────────────  Settings Screen Constants  ─────────────  */
        const val POINT_RADIUS_PX = 40f
        const val TOUCH_THRESHOLD_PX = 100f
        const val HOVER_POINT_DURATION = 500L
        const val HOVER_GRADIENT_RADIUS = 75f

        const val SNAP_STEP_DEG = 15.0
        const val HOME_REENTER_WINDOW_MS = 80L

        const val STATUS_BAR_TEMPLATE = "[{\"type\":\"Time\",\"action\":\"null\",\"formatter\":\"HH:mm:ss\"},{\"type\":\"Date\",\"action\":\"null\",\"formatter\":\" | MMM dd\"},{\"type\":\"Spacer\",\"width\":-1},{\"type\":\"Notifications\",\"maxIcons\":8},{\"type\":\"Spacer\",\"width\":6},{\"type\":\"Connectivity\"},{\"type\":\"Spacer\",\"width\":9},{\"type\":\"Bandwidth\"},{\"type\":\"Spacer\",\"width\":11},{\"type\":\"Battery\",\"showIcon\":false,\"showPercentage\":true}]"
    }

    object Drawer {
        const val DRAWER_DRAG_DOWN_THRESHOLD = 50
        const val DRAWER_MAX_DRAG_DOWN = 70
    }
}
