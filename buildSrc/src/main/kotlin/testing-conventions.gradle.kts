import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
   id("java-conventions")
   kotlin("jvm")
}

dependencies {
   testImplementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.21")
   testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
   testImplementation("io.kotest:kotest-assertions-core:5.9.1")
   testImplementation("io.kotest:kotest-framework-datatest:5.9.1")
}

kotlin {
   compilerOptions {
      jvmTarget.set(JvmTarget.JVM_1_8)
      apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
      languageVersion.set(KotlinVersion.KOTLIN_2_1)
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
