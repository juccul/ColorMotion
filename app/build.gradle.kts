plugins {
    id("com.android.application")
}

android {
    namespace = "dev.colormotion"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.colormotion"
        minSdk = 35
        targetSdk = 36
        versionCode = 100
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
        // Android 16 skips install-time dexopt for debuggable packages by default.
        // Use this variant to avoid invoking Vector's recursive compiler wrapper
        // during installation; it does not repair Vector's compiler service.
        create("installWorkaround") {
            initWith(getByName("release"))
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    sourceSets.getByName("main").assets.directories.add(project(":nativeBridge").layout.buildDirectory.dir("assets").get().asFile.absolutePath)
}

tasks.named("preBuild") { dependsOn(":nativeBridge:bridgeAssets") }

dependencies {
    compileOnly("de.robv.android.xposed:api:82")
}
