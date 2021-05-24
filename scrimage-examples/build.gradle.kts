plugins {
   `java-library`
   kotlin("jvm")
}

dependencies {
   testImplementation(kotlin("stdlib-jdk8"))
   api(project(":scrimage-core"))
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
