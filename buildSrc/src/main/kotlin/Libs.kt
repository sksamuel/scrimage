object Libs {

   val kotlinVersion = "1.3.61"
   val dokkaVersion = "0.10.0"

   const val org = "com.sksamuel.scrimage"
   const val TwelveMonkeysVersion = "3.5"
   const val PngjVersion = "2.1.0"
   const val MetadataExtractorVersion = "2.13.0"
   const val CommonsIoVersion = "2.6"

   object TwelveMonkeys {
      const val imageIoCore = "com.twelvemonkeys.imageio:imageio-core:$TwelveMonkeysVersion"
      const val imageIoJpeg = "com.twelvemonkeys.imageio:imageio-jpeg:$TwelveMonkeysVersion"
      val imageIoPcx = "com.twelvemonkeys.imageio:imageio-pcx:$TwelveMonkeysVersion"
      val imageIoTga = "com.twelvemonkeys.imageio:imageio-tga:$TwelveMonkeysVersion"
      val imageIoTiff = "com.twelvemonkeys.imageio:imageio-tiff:$TwelveMonkeysVersion"
      val imageIoBmp = "com.twelvemonkeys.imageio:imageio-bmp:$TwelveMonkeysVersion"
   }

   object Kotest {
      const val junit5 = "io.kotest:kotest-runner-junit5:4.0.0-BETA1"
   }

   object Drewnoaks {
      const val metadataExtractor = "com.drewnoakes:metadata-extractor:$MetadataExtractorVersion"
   }

   object Hjg {
      const val pngj = "ar.com.hjg:pngj:$PngjVersion"
   }

   object commons {
      const val io = "commons-io:commons-io:$CommonsIoVersion"
   }
}
