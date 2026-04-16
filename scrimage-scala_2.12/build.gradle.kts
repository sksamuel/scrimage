plugins {
   id("java-conventions")
   id("publishing-conventions")
   scala
}

dependencies {
   implementation(project(":scrimage-core"))
   implementation("org.scala-lang:scala-library:3.8.3")
}
