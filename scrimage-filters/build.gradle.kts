plugins {
   id("jvm-conventions")
   id("testing-conventions")
   id("publishing-conventions")
}

dependencies {
   implementation(project(":scrimage-core"))
   implementation("commons-io:commons-io:2.11.0")
   testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
   testImplementation("io.kotest:kotest-assertions-core:5.5.4")
}
