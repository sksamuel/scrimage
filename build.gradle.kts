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
   kotlin("jvm").apply(false).version("1.3.61")
}

allprojects {

   repositories {
      mavenCentral()
      jcenter()
      google()
   }

   group = "com.sksamuel.scrimage"

   if (isGithub) {
      version = "3.0.0." + Ci.githubBuildNumber + "-SNAPSHOT"
   }
}

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isReleaseVersion)
      sign(publications)
}
