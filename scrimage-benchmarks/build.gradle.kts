plugins {
   `java-library`
   kotlin("jvm")
}

dependencies {
   implementation(kotlin("stdlib-jdk8"))
   implementation(project(":scrimage-core"))
   implementation("org.imgscalr:imgscalr-lib:4.2")
   implementation("net.coobird:thumbnailator:0.4.11")
}

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
   jvmTarget = "1.8"
}

val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
   jvmTarget = "1.8"
}
