import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar
import java.util.Properties

plugins { `java-library` }
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
val sdkDir = Properties().apply {
    rootProject.file("local.properties").inputStream().use { load(it) }
}.getProperty("sdk.dir")
dependencies {
    compileOnly(files("$sdkDir/platforms/android-36/android.jar"))
    compileOnly(project(":nativeApi"))
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.2.10")
}

val dexDir = layout.buildDirectory.dir("dex")
val dexBridge = tasks.register<JavaExec>("dexBridge") {
    dependsOn(tasks.named("jar"), project(":nativeApi").tasks.named("jar"))
    val bridgeJar = tasks.named<Jar>("jar").flatMap { it.archiveFile }
    inputs.file(bridgeJar)
    inputs.files(configurations.compileClasspath)
    outputs.dir(dexDir)
    classpath(files("$sdkDir/build-tools/36.0.0/lib/d8.jar"))
    mainClass.set("com.android.tools.r8.D8")
    doFirst {
        dexDir.get().asFile.mkdirs()
        args = listOf("--release", "--min-api", "35", "--lib", "$sdkDir/platforms/android-36/android.jar",
                "--output", dexDir.get().asFile.absolutePath) +
                configurations.compileClasspath.get().files.filterNot {
                    it.absolutePath == "$sdkDir/platforms/android-36/android.jar"
                }.flatMap { listOf("--lib", it.absolutePath) } +
                bridgeJar.get().asFile.absolutePath
    }
}
tasks.register<Sync>("bridgeAssets") {
    dependsOn(dexBridge)
    from(dexDir.map { it.file("classes.dex") }) { rename { "colormotion-native.dex" } }
    into(layout.buildDirectory.dir("assets"))
}
