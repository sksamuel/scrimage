import Ci.isGithub

buildscript {

   repositories {
      mavenCentral()
      mavenLocal()
   }

   repositories {
      mavenCentral()
   }
}

plugins {
   java
   `java-library`
   id("java-library")
   id("maven-publish")
   signing
   id("com.adarshr.test-logger") version "2.0.0"
   kotlin("jvm").apply(false).version(Libs.kotlinVersion)
}

allprojects {

   repositories {
      mavenCentral()
      jcenter()
      google()
      maven {
         url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
      }
   }

   group = Libs.org

   if (isGithub) {
      version = "4.1.0." + Ci.githubRunId + "-SNAPSHOT"
   }
}

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isReleaseVersion)
      sign(publications)
}
