import com.lagradost.cloudstream3.gradle.CloudstreamExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.github.recloudstream:gradle:master-SNAPSHOT")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
    }
}

apply(plugin = "com.android.library")
apply(plugin = "kotlin-android")
apply(plugin = "com.github.recloudstream")

configure<CloudstreamExtension> {
    name = "HarekatJK"
    description = "HarekatJK Canlı Yayınları"
    authors = listOf("mirac")
    version = 1
    setOf(TvType.Live)
}

dependencies {
    val cloudstreamVersion = "master-SNAPSHOT"
    implementation("com.github.recloudstream:cloudstream:$cloudstreamVersion")
}
