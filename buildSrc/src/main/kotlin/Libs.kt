object Libs {

   const val kotlinVersion = "1.6.21"

   const val org = "com.sksamuel.scrimage"

   object TwelveMonkeys {
      private const val Version = "3.8.2"
      const val imageIoCore = "com.twelvemonkeys.imageio:imageio-core:$Version"
      const val jpeg = "com.twelvemonkeys.imageio:imageio-jpeg:$Version"
      const val pcx = "com.twelvemonkeys.imageio:imageio-pcx:$Version"
      const val pnm = "com.twelvemonkeys.imageio:imageio-pnm:$Version"
      const val tga = "com.twelvemonkeys.imageio:imageio-tga:$Version"
      const val tiff = "com.twelvemonkeys.imageio:imageio-tiff:$Version"
      const val bmp = "com.twelvemonkeys.imageio:imageio-bmp:$Version"
      const val iff = "com.twelvemonkeys.imageio:imageio-iff:$Version"
      const val sgi = "com.twelvemonkeys.imageio:imageio-sgi:$Version"
   }

   object Zh {
      const val opengif = "com.github.zh79325:open-gif:1.0.4"
   }

   object Kotest {
      private const val version = "5.3.2"
      const val assertions = "io.kotest:kotest-assertions-core:$version"
      const val datatest = "io.kotest:kotest-framework-datatest:$version"
      const val junit5 = "io.kotest:kotest-runner-junit5:$version"
   }

   object Drewnoaks {
      private const val Version = "2.18.0"
      const val metadataExtractor = "com.drewnoakes:metadata-extractor:$Version"
   }

   object Hjg {
      private const val Version = "2.1.0"
      const val pngj = "ar.com.hjg:pngj:$Version"
   }

   object Commons {
      private const val CommonsIoVersion   = "2.11.0"
      private const val CommonsLangVersion = "3.11"
      const val io   = "commons-io:commons-io:$CommonsIoVersion"
      const val lang = "org.apache.commons:commons-lang3:$CommonsLangVersion"
   }
}
