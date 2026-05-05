plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.github.janna99"
version = "1.0"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("io.github.janna99.jchart")
    mainClass.set("io.github.janna99.jchart.Launcher")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveClassifier.set("all")
    // Merges service files (required for some libraries like ControlsFX)
    mergeServiceFiles()

    manifest {
        attributes["Main-Class"] = "io.github.janna99.jchart.Launcher"
    }
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "app"
    }
}


// ─── Packaging Task ───────────────────────────────────────────────────────────

val appName = "JChart"
val mainClassName = "io.github.janna99.jchart.Launcher"
val javaModules = "javafx.controls,javafx.fxml,javafx.graphics,java.base,java.desktop,jdk.unsupported"

val inputDir = layout.buildDirectory.dir("package-input")
val runtimeDir = layout.buildDirectory.dir("package-runtime")
val outputDir = layout.buildDirectory.dir("package-output")

// Step 1: Copy only the fat JAR into a clean input folder
val prepareInput by tasks.registering(Copy::class) {
    dependsOn(tasks.named("shadowJar"))
    from(tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").map { it.archiveFile })
    into(inputDir)
}

// Step 2: Run jlink to create a runtime image
val createRuntime by tasks.registering(Exec::class) {
    dependsOn(prepareInput)
    val runtimePath = runtimeDir.get().asFile

    doFirst {
        runtimePath.deleteRecursively() // jlink fails if output folder already exists
    }

    commandLine(
        "${System.getProperty("java.home")}/bin/jlink",
        "--add-modules", javaModules,
        "--output", runtimePath.absolutePath
    )
}

// Step 3: Run jpackage to create the app image
val packageApp by tasks.registering(Exec::class) {
    dependsOn(createRuntime)
    val outputPath = outputDir.get().asFile

    doFirst {
        outputPath.deleteRecursively() // jpackage fails if output folder already exists
    }

    val shadowJar = tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar").get()
    val jarName = shadowJar.archiveFileName.get()

    val iconFile = when {
        System.getProperty("os.name").lowercase().contains("mac") -> "src/main/resources/icons/JChart_Icon.icns"
        else -> "src/main/resources/icon/JChart_Icon.ico"
    }

    commandLine(
        "${System.getProperty("java.home")}/bin/jpackage",
        "--type", "app-image",
        "--input", inputDir.get().asFile.absolutePath,
        "--dest", outputPath.absolutePath,
        "--name", appName,
        "--main-jar", jarName,
        "--main-class", mainClassName,
        "--runtime-image", runtimeDir.get().asFile.absolutePath,
        "--icon", iconFile
    )

    doLast {
        println("✅ App packaged successfully at: ${outputPath.absolutePath}/$appName")
    }
}

