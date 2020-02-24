plugins {
   scala
}

dependencies {
   implementation("org.scala-lang:scala-library:2.13.1")
   implementation(project(":scrimage-core"))
}

apply("../publish.gradle.kts")
