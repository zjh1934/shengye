plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.shengye.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.shengye.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "2.0.0-alpha1"
    }

    buildFeatures { compose = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildTypes {
        release {
            // 当前用 debug 签名出可安装包，便于从 Release 页下载验收；
            // 正式分发前换成独立 keystore（见 02_PROGRESS 待办）。
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":core:contract"))
    implementation(project(":core:domain"))
    implementation(project(":core:audio"))
    implementation(project(":core:data"))
    implementation(project(":design:glass"))
    implementation(project(":feature:library"))
    implementation(project(":feature:book"))
    implementation(project(":feature:tasks"))
    implementation(project(":feature:player"))
    implementation(project(":feature:mixer"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:models"))
    implementation(project(":platform:service"))
    implementation(project(":platform:native"))

    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.13.1")
}
