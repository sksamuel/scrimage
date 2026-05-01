package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class SnowFilterTest : FunSpec() {
   init {

      val filter = SnowFilter()

      test("should work for TYPE_INT_ARGB") {
         val image = ImmutableImage.loader().fromResource("/bird_small.png")
         image.copy(BufferedImage.TYPE_INT_ARGB)
         image.filter(filter).width shouldBe 388
         image.filter(filter).pixel(0, 0) shouldNotBe image.pixel(0, 0)
      }
      test("should work for TYPE_INT_RGB") {
         val image = ImmutableImage.loader().fromResource("/bird_small.png")
         image.copy(BufferedImage.TYPE_INT_RGB)
         image.filter(filter).width shouldBe 388
         image.filter(filter).pixel(0, 0) shouldNotBe image.pixel(0, 0)
      }
      test("should work for TYPE_BYTE_GRAY") {
         val image = ImmutableImage.loader().fromResource("/bird_small.png")
         image.copy(BufferedImage.TYPE_BYTE_GRAY)
         image.filter(filter).width shouldBe 388
         image.filter(filter).pixel(0, 0) shouldNotBe image.pixel(0, 0)
      }
      test("should work for TYPE_4BYTE_ABGR") {
         val image = ImmutableImage.loader().fromResource("/bird_small.png")
         image.copy(BufferedImage.TYPE_4BYTE_ABGR)
         image.filter(filter).width shouldBe 388
         image.filter(filter).pixel(0, 0) shouldNotBe image.pixel(0, 0)
      }

      // Regression: apply() ended with `System.out.println(System.currentTimeMillis())` —
      // leftover debug code that printed a timestamp on every filter application.
      test("apply does not print to stdout") {
         val image = ImmutableImage.create(64, 64, BufferedImage.TYPE_INT_ARGB)
         val originalOut = System.out
         val captured = ByteArrayOutputStream()
         try {
            System.setOut(PrintStream(captured))
            filter.apply(image)
         } finally {
            System.setOut(originalOut)
         }
         captured.toByteArray().size shouldBe 0
      }
   }
}
