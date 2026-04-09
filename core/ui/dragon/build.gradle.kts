import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(17)
}

extensions.configure<LibraryExtension> {
    namespace = "org.elnix.dragonlauncher.ui.dragon"
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

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.process)
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
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.reorderable)
    implementation(libs.android.image.cropper)
    implementation(libs.material)
    implementation(libs.material3)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.shapeindicators)
    implementation(libs.shizuku.api)



    implementation(project(":core:ui:base"))
    implementation(project(":core:ui:theme"))
    implementation(project(":core:ui:composition"))

    implementation(project(":core:base"))
    implementation(project(":core:models"))
    implementation(project(":core:common"))
    implementation(project(":core:enumsui"))
    implementation(project(":core:logging"))
    implementation(project(":core:services"))
    implementation(project(":core:settings"))
}
