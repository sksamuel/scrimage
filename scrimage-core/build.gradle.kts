plugins {
   `java-library`
   kotlin("jvm")
}

dependencies {
   implementation(kotlin("stdlib-jdk8"))
   api(Libs.TwelveMonkeys.imageIoCore)
   api(Libs.TwelveMonkeys.jpeg)
   api(Libs.Drewnoaks.metadataExtractor)
   implementation(Libs.Zh.opengif)
   implementation(Libs.Commons.io)
   implementation(Libs.Hjg.pngj)
   testImplementation(Libs.Kotest.junit5)
   testImplementation(Libs.Kotest.assertions)
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
