plugins {
   id("testing-conventions")
   id("publishing-conventions")
}

dependencies {
   api(project(":scrimage-core"))
   implementation("org.apache.commons:commons-lang3:3.12.0")
   implementation("org.slf4j:slf4j-api:2.0.6")
}
