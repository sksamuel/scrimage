import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
   kotlin("jvm")
}

dependencies {
   implementation(kotlin("stdlib-jdk8"))
   implementation(project(":scrimage-core"))
}

repositories {
   mavenCentral()
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
   jvmTarget = "1.8"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
   jvmTarget = "1.8"
}