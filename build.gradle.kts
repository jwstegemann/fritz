plugins {
    kotlin("multiplatform") version "1.7.10" apply false
    kotlin("plugin.serialization") version "1.7.10" apply false
    id("org.jetbrains.dokka") version "1.6.21"
    id("maven-publish")
    signing
}

//// needed to work on Apple Silicon. Should be fixed by 1.6.20 (https://youtrack.jetbrains.com/issue/KT-49109#focus=Comments-27-5259190.0-0)
//rootProject.plugins.withType<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin> {
//    rootProject.the<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension>().nodeVersion = "16.0.0"
//}

// consider moving to idiomatic solution of gradle for dependency sharing once it is ready:
// https://docs.gradle.org/current/userguide/platforms.html
ext {
    // Dependencies
    set("kotlinVersion", "1.7.10")
    set("coroutinesVersion", "1.6.4")
    set("kotlinpoetVersion", "1.12.0")
    set("compileTestingVersion", "1.4.9")
    set("stylisVersion", "4.0.2")
    set("murmurhashVersion", "2.0.0")
    set("logbackVersion", "1.2.11")
    set("ktorVersion", "1.6.6")
    set("serializationVersion", "1.3.3")
    set("kspVersion", "1.7.10-1.0.6")
    set("autoServiceVersion", "1.0.1")
    set("junitJupiterParamsVersion", "5.8.1")
    set("assertJVersion", "3.19.0")
}

allprojects {
    //manage common setting and dependencies
    repositories {
        mavenCentral()
    }
}

subprojects {
    group = "dev.fritz2"
    version = "0.14.4"
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(rootDir.resolve("api"))
}
