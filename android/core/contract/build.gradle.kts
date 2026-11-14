plugins { id("java-library"); id("org.jetbrains.kotlin.jvm") }
kotlin { jvmToolchain(17) }
java { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}
