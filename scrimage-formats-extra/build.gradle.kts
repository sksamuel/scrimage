plugins {
   id("jvm-conventions")
   id("testing-conventions")
   id("publishing-conventions")
}

dependencies {
   api(project(":scrimage-core"))
   implementation("com.twelvemonkeys.imageio:imageio-pcx:3.12.0")
   implementation("com.twelvemonkeys.imageio:imageio-pnm:3.12.0")
   implementation("com.twelvemonkeys.imageio:imageio-tga:3.12.0")
   implementation("com.twelvemonkeys.imageio:imageio-tiff:3.12.0")
   implementation("com.twelvemonkeys.imageio:imageio-bmp:3.12.0")
   implementation("com.twelvemonkeys.imageio:imageio-iff:3.12.0")
   implementation("com.twelvemonkeys.imageio:imageio-sgi:3.12.0")
}
