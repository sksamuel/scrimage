plugins {
   id("jvm-conventions")
   id("kotlin-conventions")
   id("testing-conventions")
   id("publishing-conventions")
}

dependencies {
   implementation(project(":scrimage-core"))
   testImplementation(project(":scrimage-webp"))
   testImplementation("io.kotest:kotest-framework-datatest:5.5.4")
   testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
   testImplementation("io.kotest:kotest-assertions-core:5.5.4")
}
