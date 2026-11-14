plugins { id("java-library"); id("org.jetbrains.kotlin.jvm") }
kotlin { jvmToolchain(17) }
java { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }

dependencies {
    // api：domain 对外暴露 contract 类型（UiIntent / AppState / ProduceTask）
    api(project(":core:contract"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    testImplementation("junit:junit:4.13.2")
}

tasks.test { useJUnit(); testLogging { events("passed", "failed", "skipped") } }
