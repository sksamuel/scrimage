package com.sksamuel.scrimage.format.png

import com.sksamuel.scrimage.nio.PngWriter
import io.kotest.core.spec.style.FunSpec

class PngReaderTest : FunSpec({

//   test("load png files dimensions") {
//      PngReader().read(javaClass.getResourceAsStream("/1.png")).dimensions() shouldBe Dimension(800, 600)
//      PngReader().read(javaClass.getResourceAsStream("/2.png")).dimensions() shouldBe Dimension(400, 479)
//      PngReader().read(javaClass.getResourceAsStream("/3.png")).dimensions() shouldBe Dimension(576, 1118)
//      PngReader().read(javaClass.getResourceAsStream("/4.png")).dimensions() shouldBe Dimension(581, 564)
//      PngReader().read(javaClass.getResourceAsStream("/5.png")).dimensions() shouldBe Dimension(256, 256)
//      PngReader().read(javaClass.getResourceAsStream("/6.png")).dimensions() shouldBe Dimension(499, 499)
//   }

   test("load png pixels") {
//      PngReader().read(javaClass.getResourceAsStream("/1.png")).forWriter(PngWriter.MaxCompression).write("scrimage1.png")
      PngReader().read(javaClass.getResourceAsStream("/red.png")).forWriter(PngWriter.MaxCompression).write("red.png")
      PngReader().read(javaClass.getResourceAsStream("/blue.png")).forWriter(PngWriter.MaxCompression).write("blue.png")
//      PngReader().read(javaClass.getResourceAsStream("/1.png")).pixel(20, 20).argb shouldBe 8
   }
})
