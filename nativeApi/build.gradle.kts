import java.util.Properties

plugins { `java-library` }
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

val sdkDir = Properties().apply {
    rootProject.file("local.properties").inputStream().use { load(it) }
}.getProperty("sdk.dir")
dependencies { compileOnly(files("$sdkDir/platforms/android-36/android.jar")) }
