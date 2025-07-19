plugins {
   id("jvm-conventions")
   id("testing-conventions")
   id("publishing-conventions")
}

dependencies {
   implementation(project(":scrimage-core"))
   implementation("commons-io:commons-io:2.19.0")
}
