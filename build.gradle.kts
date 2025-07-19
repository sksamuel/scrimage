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
   id("com.adarshr.test-logger") version "2.0.0"
   kotlin("jvm").apply(false).version("1.6.21")
   id("com.vanniktech.maven.publish") version "0.34.0"
}

mavenPublishing {
   publishToMavenCentral()
   signAllPublications()

   pom {
      name.set("Scrimage")
      description.set("JVM Image Library")
      url.set("httsp://www.github.com/sksamuel/scrimage")

      scm {
         connection.set("scm:git:http://www.github.com/sksamuel/scrimage/")
         developerConnection.set("scm:git:http://github.com/sksamuel/")
         url.set("http://www.github.com/sksamuel/scrimage/")
      }

      licenses {
         license {
            name.set("The Apache 2.0 License")
            url.set("https://opensource.org/licenses/Apache-2.0")
         }
      }

      developers {
         developer {
            id.set("sksamuel")
            name.set("Stephen Samuel")
            email.set("sam@sksamuel.com")
         }
      }
   }
}

subprojects {

   repositories {
      mavenCentral()
      google()
      mavenLocal()
      maven {
         url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
      }
   }

   group = "com.sksamuel.scrimage"
   version = Ci.version
}
