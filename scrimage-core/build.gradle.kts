plugins {
   `java-library`
   kotlin("jvm")
}

dependencies {
   api(Libs.TwelveMonkeys.imageIoCore)
   api(Libs.TwelveMonkeys.imageIoJpeg)
   api(Libs.Drewnoaks.metadataExtractor)
   implementation(Libs.commons.io)
   implementation(Libs.Hjg.pngj)

   testImplementation(Libs.Kotest.junit5)

//   "com.twelvemonkeys.common"  % "common-lang"         % TwelveMonkeysVersion,
//   "com.twelvemonkeys.common"  % "common-io"           % TwelveMonkeysVersion,
//   "com.twelvemonkeys.common"  % "common-image"        % TwelveMonkeysVersion,
//   "commons-io"                % "commons-io"          % CommonsIoVersion,
//   "org.apache.commons"        % "commons-lang3"       % "3.9"
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