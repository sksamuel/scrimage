plugins {
   `java-library`
   kotlin("jvm")
}

dependencies {
   implementation("com.twelvemonkeys.imageio:imageio-core:3.9.4")
   implementation("com.twelvemonkeys.imageio:imageio-jpeg:3.9.4")
   implementation("com.drewnoakes:metadata-extractor:2.18.0")
   implementation("commons-io:commons-io:2.11.0")
   implementation(project(":scrimage-core"))
   testImplementation(kotlin("stdlib"))
   testImplementation(kotlin("stdlib-jdk8"))
   testImplementation("io.kotest:kotest-framework-datatest:5.5.5")
   testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
   testImplementation("io.kotest:kotest-assertions-core:5.5.5")
}

java {
   sourceCompatibility = JavaVersion.VERSION_1_8
   targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.named<Test>("test") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(
         org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
      )
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
   jvmTarget = "1.8"
}
