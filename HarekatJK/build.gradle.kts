// Apply the required plugins
apply(plugin = "com.android.library")
apply(plugin = "kotlin-android")
apply(plugin = "com.lagradost.cloudstream3.gradle")

version = 1

cloudstream {
    language = "tr"
    description = "HarekatJK Canlı TV Yayınları"
    authors = listOf("mirac")
    status = 1
    tvTypes = listOf("Live")
    iconUrl = "https://www.google.com/s2/favicons?domain=github.com&sz=128"
}

android {
    compileSdkVersion(33)
    
    defaultConfig {
        minSdk = 21
        targetSdkVersion(33)
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}
