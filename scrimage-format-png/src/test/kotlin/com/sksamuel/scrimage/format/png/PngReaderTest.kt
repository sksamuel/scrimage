package com.sksamuel.scrimage.format.png

import com.sksamuel.scrimage.Dimension
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PngReaderTest : FunSpec({

   test("load png files dimensions") {
      PngReader().read(javaClass.getResourceAsStream("/1.png")).dimensions() shouldBe Dimension(800, 600)
      PngReader().read(javaClass.getResourceAsStream("/2.png")).dimensions() shouldBe Dimension(400, 479)
      PngReader().read(javaClass.getResourceAsStream("/3.png")).dimensions() shouldBe Dimension(576, 1118)
      PngReader().read(javaClass.getResourceAsStream("/4.png")).dimensions() shouldBe Dimension(581, 564)
      PngReader().read(javaClass.getResourceAsStream("/5.png")).dimensions() shouldBe Dimension(256, 256)
      PngReader().read(javaClass.getResourceAsStream("/6.png")).dimensions() shouldBe Dimension(499, 499)
   }

   test("load png pixels") {
//      PngReader().read(javaClass.getResourceAsStream("/1.png")).forWriter(PngWriter.MaxCompression).write("1.png")
//      PngReader().read(javaClass.getResourceAsStream("/2.png")).forWriter(PngWriter.MaxCompression).write("2.png")
//      PngReader().read(javaClass.getResourceAsStream("/3.png")).forWriter(PngWriter.MaxCompression).write("3.png")
//      PngReader().read(javaClass.getResourceAsStream("/4.png")).forWriter(PngWriter.MaxCompression).write("4.png")

      fun equal(resource: String) {
         val a = PngReader().read(javaClass.getResourceAsStream(resource)).pixels().take(10000)
         val b = ImmutableImageLoader.create().fromResource(resource).pixels().take(10000)

         a.zip(b).forEach { (p1, p2) ->
            if (p1.alpha() == 0) {
               p2.alpha() shouldBe 0
            } else {
               withClue("$p1 $p2 ${p1.alpha()} ${p2.alpha()}  ${p1.red()} ${p2.red()}  ${p1.green()} ${p2.green()}  ${p1.blue()} ${p2.blue()}") {
                  p1.red() shouldBe p2.red()
                  p1.blue() shouldBe p2.blue()
                  p1.green() shouldBe p2.green()
               }
            }
         }
      }

      equal("/red.png")
      equal("/blue.png")
      equal("/1.png")
      equal("/2.png")
      equal("/3.png")

   }
})
