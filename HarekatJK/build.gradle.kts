version = 1

cloudstream {
    language = "tr"
    description = "HarekatJK Canlı TV Yayınları"
    authors = listOf("mirac")
    
    status = 1 // 0: Down, 1: Ok, 2: Slow, 3: Beta only
    tvTypes = listOf("Live")
    
    iconUrl = "https://www.google.com/s2/favicons?domain=github.com&sz=128"
}

android {
    compileSdk = 33
    
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("com.github.recloudstream:cloudstream:master-SNAPSHOT")
}
