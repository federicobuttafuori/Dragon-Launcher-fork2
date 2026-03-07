
import com.android.build.api.dsl.ApplicationExtension
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}


val dotenv = Properties().apply {
    val envFile = rootProject.file(".env")
    if (envFile.exists()) {
        envFile.inputStream().use { load(it) }
    }
}

fun env(name: String): String? =
    System.getenv(name) ?: dotenv.getProperty(name)


kotlin {
    jvmToolchain(17)
}

// Configure Android
extensions.configure<ApplicationExtension> {
    namespace = "org.elnix.dragonlauncher"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "org.elnix.dragonlauncher"
        minSdk = 26
        targetSdk = 36
        versionName = "${property("VERSION_NAME") as String} (${property("CODE_NAME") as String})"
        versionCode = (property("VERSION_CODE") as String).toInt()
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }


    flavorDimensions += "channel"
    productFlavors {
        create("stable") {
            dimension = "channel"
            versionNameSuffix = ""
        }
        create("beta") {
            dimension = "channel"
            versionNameSuffix = "-beta"
        }
        create("fdroid") {
            dimension = "channel"
            versionNameSuffix = ""
        }
    }

    signingConfigs {
        create("release") {
            val keystore = env("KEYSTORE_FILE")
            val storePass = env("KEYSTORE_PASSWORD")
            val alias = env("KEY_ALIAS")
            val keyPass = env("KEY_PASSWORD")

            if (
                !keystore.isNullOrBlank() &&
                !storePass.isNullOrBlank() &&
                !alias.isNullOrBlank() &&
                !keyPass.isNullOrBlank()
            ) {
                storeFile = file(keystore)
                storePassword = storePass
                keyAlias = alias
                keyPassword = keyPass
            } else {
                println("WARNING: Release signingConfig not fully configured.")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            val isFdroidBuild = System.getenv("FDROID_BUILD") == "true"

            signingConfig = if (!isFdroidBuild) {

                val hasSigning =
                    env("KEYSTORE_FILE") != null &&
                            env("KEYSTORE_PASSWORD") != null &&
                            env("KEY_ALIAS") != null &&
                            env("KEY_PASSWORD") != null

                 if (hasSigning) {
                    signingConfigs.getByName("release")
                } else null

            } else {
                println("FDroid build - not using release signing config")
                null
            }

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }


        create("unminifiedRelease") {
            initWith(getByName("release"))
            isMinifyEnabled = false
            isShrinkResources = false
        }
        create("debuggableRelease") {
            initWith(getByName("release"))
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = true
        }

        debug {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs (for IzzyOnDroid/F-Droid)
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles (for Google Play)
        includeInBundle = false
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.reorderable)
    implementation(libs.android.image.cropper)
    implementation(libs.material)
    implementation(libs.material3)

    implementation(project(":core:ui"))
    implementation(project(":core:base"))
    implementation(project(":core:enumsui"))
    implementation(project(":core:common"))
    implementation(project(":core:settings"))
    implementation(project(":core:models"))
}



// Copy files in the fastlane/metadata dir to the assets folder, where they are compiled and added to the app
tasks.register<Copy>("copyChangelogsToAssets") {
    from("../fastlane/metadata/android/en-US/changelogs")
    into(file("src/main/assets/changelogs"))
    include("*.txt")
}

// Use preBuild tasks instead of merge* (they exist in AGP)
tasks.named("preBuild") {
    dependsOn("copyChangelogsToAssets")
}
