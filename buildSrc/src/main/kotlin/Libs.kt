object Libs {

   const val kotlinVersion = "1.3.72"

   const val org = "com.sksamuel.scrimage"
   const val TwelveMonkeysVersion = "3.5"
   const val CommonsIoVersion = "2.6"

   object TwelveMonkeys {
      const val imageIoCore = "com.twelvemonkeys.imageio:imageio-core:$TwelveMonkeysVersion"
      const val jpeg = "com.twelvemonkeys.imageio:imageio-jpeg:$TwelveMonkeysVersion"
      const val pcx = "com.twelvemonkeys.imageio:imageio-pcx:$TwelveMonkeysVersion"
      const val pnm = "com.twelvemonkeys.imageio:imageio-pnm:$TwelveMonkeysVersion"
      const val tga = "com.twelvemonkeys.imageio:imageio-tga:$TwelveMonkeysVersion"
      const val tiff = "com.twelvemonkeys.imageio:imageio-tiff:$TwelveMonkeysVersion"
      const val bmp = "com.twelvemonkeys.imageio:imageio-bmp:$TwelveMonkeysVersion"
      const val iff = "com.twelvemonkeys.imageio:imageio-iff:$TwelveMonkeysVersion"
      const val sgi = "com.twelvemonkeys.imageio:imageio-sgi:$TwelveMonkeysVersion"
   }

   object Zh {
      const val opengif = "com.github.zh79325:open-gif:1.0.4"
   }

   object Kotest {
      private const val version = "4.1.1"
      const val assertions = "io.kotest:kotest-assertions-core-jvm:$version"
      const val junit5 = "io.kotest:kotest-runner-junit5-jvm:$version"
      const val console = "io.kotest:kotest-runner-console-jvm:$version"
   }

   object Drewnoaks {
      private const val Version = "2.14.0"
      const val metadataExtractor = "com.drewnoakes:metadata-extractor:$Version"
   }

   object Hjg {
      private const val Version = "2.1.0"
      const val pngj = "ar.com.hjg:pngj:$Version"
   }

   object Commons {
      const val io = "commons-io:commons-io:$CommonsIoVersion"
   }
}
