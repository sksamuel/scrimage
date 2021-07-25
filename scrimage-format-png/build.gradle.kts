plugins {
   `java-library`
   kotlin("jvm")
}

dependencies {
   implementation(kotlin("stdlib-jdk8"))
   implementation(project(":scrimage-core"))
   testImplementation(Libs.Kotest.api)
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

apply("../publish.gradle.kts")
