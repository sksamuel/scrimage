plugins {
   id("java-conventions")
   id("testing-conventions")
   id("publishing-conventions")
}

dependencies {
   api(project(":scrimage-core"))
   implementation("com.twelvemonkeys.imageio:imageio-pcx:3.13.1")
   implementation("com.twelvemonkeys.imageio:imageio-pnm:3.13.1")
   implementation("com.twelvemonkeys.imageio:imageio-tga:3.13.1")
   implementation("com.twelvemonkeys.imageio:imageio-tiff:3.13.1")
   implementation("com.twelvemonkeys.imageio:imageio-bmp:3.13.1")
   implementation("com.twelvemonkeys.imageio:imageio-iff:3.13.1")
   implementation("com.twelvemonkeys.imageio:imageio-sgi:3.13.1")
   implementation("com.twelvemonkeys.imageio:imageio-psd:3.13.1")
   implementation("com.twelvemonkeys.imageio:imageio-pict:3.13.1")
   implementation("com.twelvemonkeys.imageio:imageio-batik:3.13.1")
   // imageio-batik delegates SVG rasterization to Apache Batik, which it declares as a
   // provided dependency, so it must be added explicitly.
   implementation("org.apache.xmlgraphics:batik-transcoder:1.19")
   implementation("org.apache.xmlgraphics:batik-codec:1.18")
}
