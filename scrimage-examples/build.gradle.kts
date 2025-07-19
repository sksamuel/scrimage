plugins {
   id("java-conventions")
   id("testing-conventions")
}

dependencies {
   api(project(":scrimage-core"))
   testImplementation(kotlin("stdlib-jdk8"))
}
