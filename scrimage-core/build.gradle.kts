plugins {
   `java-library`
   kotlin("jvm")
}

dependencies {
   implementation(kotlin("stdlib-jdk8"))
   api(Libs.TwelveMonkeys.imageIoCore)
   api(Libs.TwelveMonkeys.jpeg)
   api(Libs.Drewnoaks.metadataExtractor)
   implementation("com.github.zh79325:open-gif:1.0.4")
   implementation(Libs.commons.io)
   implementation(Libs.Hjg.pngj)
   testImplementation(Libs.Kotest.junit5)
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

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
   jvmTarget = "1.8"
}

val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
   jvmTarget = "1.8"
}

apply("../publish.gradle.kts")