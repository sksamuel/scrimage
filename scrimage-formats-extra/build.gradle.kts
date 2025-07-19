plugins {
   id("jvm-conventions")
   id("testing-conventions")
   id("publishing-conventions")
}

dependencies {
   api(project(":scrimage-core"))
   implementation("com.twelvemonkeys.imageio:imageio-pcx:3.9.4")
   implementation("com.twelvemonkeys.imageio:imageio-pnm:3.9.4")
   implementation("com.twelvemonkeys.imageio:imageio-tga:3.9.4")
   implementation("com.twelvemonkeys.imageio:imageio-tiff:3.9.4")
   implementation("com.twelvemonkeys.imageio:imageio-bmp:3.9.4")
   implementation("com.twelvemonkeys.imageio:imageio-iff:3.9.4")
   implementation("com.twelvemonkeys.imageio:imageio-sgi:3.9.4")

   testImplementation("io.kotest:kotest-assertions-core:5.5.4")
   testImplementation("io.kotest:kotest-runner-junit5:5.5.4")
}
