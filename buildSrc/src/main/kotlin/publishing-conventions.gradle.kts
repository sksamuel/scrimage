plugins {
   id("com.vanniktech.maven.publish")
}

group = "com.sksamuel.scrimage"
version = Ci.version

mavenPublishing {
   publishToMavenCentral(automaticRelease = true)
   signAllPublications()
   pom {
      name.set("Scrimage")
      description.set("JVM Image Library")
      url.set("https://www.github.com/sksamuel/scrimage")

      scm {
         connection.set("scm:git:https://www.github.com/sksamuel/scrimage/")
         developerConnection.set("scm:git:https://github.com/sksamuel/")
         url.set("https://www.github.com/sksamuel/scrimage/")
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
