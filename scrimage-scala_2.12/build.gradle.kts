plugins {
   scala
}

dependencies {
   implementation("org.scala-lang:scala-library:2.12.10")
   implementation(project(":scrimage-core"))
}

apply("../publish.gradle.kts")
