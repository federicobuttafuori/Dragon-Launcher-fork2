import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
}

kotlin {
    jvmToolchain(17)
}

extensions.configure<LibraryExtension> {
    namespace = "org.elnix.dragonlauncher.logging"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 26

        consumerProguardFiles("consumer-rules.pro")
    }

    flavorDimensions += "channel"

    productFlavors {
        create("stable") { dimension = "channel" }
        create("beta")   { dimension = "channel" }
        create("fdroid") { dimension = "channel" }
    }

    buildTypes {
        debug {}
        release {}

        create("unminifiedRelease") {
            initWith(getByName("release"))
        }

        create("debuggableRelease") {
            initWith(getByName("release"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.androidx.ui.graphics)
    implementation(libs.kotlinx.coroutines.core)

    runtimeOnly(libs.androidx.transition)

    api(libs.timber)
}