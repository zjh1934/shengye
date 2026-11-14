plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.shengye.design.glass"
    compileSdk = 35
    defaultConfig { minSdk = 26 }
    buildFeatures { compose = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    api(project(":core:contract"))
    // 用 api：feature 模块只依赖 design:glass 就能用 Compose，不必各自再写一遍版本号
    val bom = platform("androidx.compose:compose-bom:2024.10.01")
    api(bom)
    api("androidx.compose.ui:ui")
    api("androidx.compose.ui:ui-graphics")
    api("androidx.compose.foundation:foundation")
    api("androidx.compose.material3:material3")
    // WindowCompat：状态栏/导航栏图标黑白跟随主题
    implementation("androidx.core:core-ktx:1.13.1")
}
