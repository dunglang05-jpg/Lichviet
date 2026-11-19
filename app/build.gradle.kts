plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "vn.lichviet.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "vn.lichviet.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 141
        versionName = "0.14.1"
    }

    // Khóa ký cố định (để cập nhật OTA cài đè được): đọc từ biến môi trường do CI giải mã từ GitHub Secrets.
    val ksFile = System.getenv("LV_KEYSTORE")?.let { file(it) }?.takeIf { it.exists() }
    signingConfigs {
        if (ksFile != null) create("lv") {
            storeFile = ksFile
            storePassword = System.getenv("LV_KEYSTORE_PASS")
            keyAlias = System.getenv("LV_KEY_ALIAS") ?: "lichviet"
            keyPassword = System.getenv("LV_KEY_PASS") ?: System.getenv("LV_KEYSTORE_PASS")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Có khóa riêng thì dùng, chưa có thì ký tạm bằng khóa debug.
            signingConfig = if (ksFile != null) signingConfigs.getByName("lv") else signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation(project(":core:lunar"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(project(":feature:tools"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:widget"))
    implementation(project(":feature:reminder"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.icons)
    implementation(libs.androidx.compose.icons.ext)
}
