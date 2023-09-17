package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.image.BufferedImage

class OldPhotoFilterTest : FunSpec() {
   init {

      val filter = OldPhotoFilter()

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
   }
}
