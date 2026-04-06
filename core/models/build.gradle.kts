import com.android.build.api.dsl.LibraryExtension

plugins {
    alias(libs.plugins.android.library)
}

kotlin {
    jvmToolchain(17)
}

extensions.configure<LibraryExtension> {
    namespace = "org.elnix.dragonlauncher.models"
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.gson)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.ui.graphics)

    implementation(project(":core:logging"))
    implementation(project(":core:common"))
    implementation(project(":core:enumsui"))
    implementation(project(":core:settings"))
    implementation(project(":core:shizuku"))
}
