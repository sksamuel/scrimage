import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
   id("java-conventions")
   kotlin("jvm")
}

dependencies {
   testImplementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.10")
   val kotest = "6.1.7"
   testImplementation("io.kotest:kotest-runner-junit5:$kotest")
   testImplementation("io.kotest:kotest-assertions-core:$kotest")
}

kotlin {
   compilerOptions {
      jvmTarget.set(JvmTarget.JVM_1_8)
      apiVersion.set(KotlinVersion.KOTLIN_2_2)
      languageVersion.set(KotlinVersion.KOTLIN_2_2)
   }
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
         TestLogEvent.FAILED,
         TestLogEvent.PASSED
      )
      exceptionFormat = TestExceptionFormat.FULL
   }
}
