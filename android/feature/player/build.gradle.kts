plugins { id("com.android.library"); id("org.jetbrains.kotlin.android") }
android { namespace = "com.shengye.feature.player"; compileSdk = 35
    defaultConfig { minSdk = 26 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}
dependencies {
    implementation(project(":core:contract"))
    implementation(project(":design:glass"))
}
