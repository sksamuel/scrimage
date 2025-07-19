plugins {
   id("jvm-conventions")
   id("testing-conventions")
}

dependencies {
   implementation(project(":scrimage-core"))
   implementation(project(":scrimage-filters"))
   implementation("com.twelvemonkeys.imageio:imageio-core:3.9.4")
   implementation("com.twelvemonkeys.imageio:imageio-jpeg:3.9.4")
   implementation("com.drewnoakes:metadata-extractor:2.18.0")
   implementation("commons-io:commons-io:2.11.0")
   testImplementation("io.kotest:kotest-framework-datatest:5.5.5")
   testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
   testImplementation("io.kotest:kotest-assertions-core:5.5.5")
}
