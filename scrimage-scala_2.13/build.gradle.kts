plugins {
   scala
}

dependencies {
   implementation("org.scala-lang:scala-library:2.13.10")
   implementation(project(":scrimage-core"))
}

apply("../publish.gradle.kts")
