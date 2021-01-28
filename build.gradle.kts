plugins {
    kotlin("multiplatform") version "1.4.30-RC" apply false
    kotlin("plugin.serialization") version "1.4.30-RC" apply false
    id("org.jetbrains.dokka") version "1.4.10.2"
}

ext {
    // Dependencies
    set("coroutinesVersion", "1.4.1")
    set("kotlinpoetVersion", "1.6.0")
    set("stylisVersion", "4.0.2")
    set("murmurhashVersion", "1.0.0")
    set("logbackVersion", "1.2.1")
    set("ktorVersion", "1.4.0")
}

allprojects {
    //manage common setting and dependencies
    repositories {
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }
}

subprojects {
    group = "dev.fritz2"
    version = "0.9-SNAPSHOT"
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(rootDir.resolve("api"))
}
