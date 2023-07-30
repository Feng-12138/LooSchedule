//import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
//    id("com.android.application") version "8.0.1"
    application
}

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":backend"))
    implementation(project(":AndroidApp"))
}

//tasks.named<KotlinCompile>("compileKotlin") {
//    kotlinOptions.jvmTarget = "1.6"
//}
//
//tasks.named<KotlinCompile>("compileTestKotlin") {
//    kotlinOptions.jvmTarget = "1.6"
//}

kotlin {
    sourceSets {
        main {
            kotlin.srcDirs("src/main/kotlin")
        }
    }
}

application {
    mainClass.set("MainKt")
}
