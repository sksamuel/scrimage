plugins {
   id("jvm-conventions")
   id("kotlin-conventions")
   id("testing-conventions")
   id("publishing-conventions")
}

dependencies {
   implementation(project(":scrimage-core"))
   testImplementation(project(":scrimage-webp"))
}
