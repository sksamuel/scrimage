plugins {
   `java-library`
   kotlin("jvm")
}

dependencies {
   api(project(":scrimage-core"))
   testImplementation(kotlin("stdlib-jdk8"))
   implementation("com.twelvemonkeys.imageio:imageio-pcx:3.8.2")
   implementation("com.twelvemonkeys.imageio:imageio-pnm:3.8.2")
   implementation("com.twelvemonkeys.imageio:imageio-tga:3.8.2")
   implementation("com.twelvemonkeys.imageio:imageio-tiff:3.8.2")
   implementation("com.twelvemonkeys.imageio:imageio-bmp:3.8.2")
   implementation("com.twelvemonkeys.imageio:imageio-iff:3.8.2")
   implementation("com.twelvemonkeys.imageio:imageio-sgi:3.8.2")

   testImplementation("io.kotest:kotest-assertions-core:5.3.2")
   testImplementation("io.kotest:kotest-runner-junit5:5.3.2")
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
