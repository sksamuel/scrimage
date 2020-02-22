object Libs {

   val kotlinVersion = "1.3.61"
   val dokkaVersion = "0.10.0"

   val org = "com.sksamuel.scrimage"
   val TwelveMonkeysVersion = "3.5"
   val PngjVersion = "2.1.0"
   val MetadataExtractorVersion = "2.13.0"
   val ScalatestVersion = "3.0.8"
   val CommonsIoVersion = "2.6"

   object TwelveMonkeys {
      val imageIoCore = "com.twelvemonkeys.imageio:imageio-core:$TwelveMonkeysVersion"
      val imageIoJpeg = "com.twelvemonkeys.imageio:imageio-jpeg:$TwelveMonkeysVersion"
      val imageIoPcx = "com.twelvemonkeys.imageio:imageio-pcx:$TwelveMonkeysVersion"
      val imageIoTga = "com.twelvemonkeys.imageio:imageio-tga:$TwelveMonkeysVersion"
      val imageIoTiff = "com.twelvemonkeys.imageio:imageio-tiff:$TwelveMonkeysVersion"
      val imageIoBmp = "com.twelvemonkeys.imageio:imageio-bmp:$TwelveMonkeysVersion"
   }

   object Drewnoaks {
      val metadataExtractor = "com.drewnoakes:metadata-extractor:$MetadataExtractorVersion"
   }

   object Hjg {
      val pngj = "ar.com.hjg:pngj:$PngjVersion"
   }

   object commons {
      val io = "commons-io:commons-io:$CommonsIoVersion"
   }
}
