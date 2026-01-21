pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
    }
}

rootProject.name = "HarekatJK-Repo"

// Dinamik olarak alt projeleri ekle
FileTree projects = fileTree(rootDir)
projects.include("**/build.gradle.kts")
projects.exclude("build.gradle.kts") // Root'u hariç tut
projects.exclude("**/build/**")

projects.forEach { file ->
    val projectName = file.parentFile.name
    include(projectName)
    project(":$projectName").projectDir = file.parentFile
}
