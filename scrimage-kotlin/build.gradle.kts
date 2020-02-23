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

