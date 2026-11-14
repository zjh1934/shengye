pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "shengye"
include(":app")
// core · 纯逻辑
include(":core:contract", ":core:domain", ":core:audio", ":core:data")
// design · 唯一视觉来源
include(":design:glass")
// feature · 一个页面一个模块，互不依赖
include(":feature:library", ":feature:book", ":feature:tasks", ":feature:player",
        ":feature:mixer", ":feature:settings", ":feature:models")
// platform · 系统能力
include(":platform:service", ":platform:tts")
