package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class SaltAndPepperFilterTest : FunSpec({

   // With salt=1.0 and pepper=0.0, the inner branch reached for every pixel
   // is the salt path. After the filter, every pixel should be opaque white.
   //
   // Previously the salt branch packed `(255 << 16) | (255 << 8) | 255`
   // (= 0x00FFFFFF) as the ARGB value, so on a TYPE_INT_ARGB image every
   // "white" pixel had alpha=0 and the entire image came out fully
   // transparent.
   test("salt produces opaque white pixels on a TYPE_INT_ARGB image") {
      val source = ImmutableImage.filled(40, 30, Color.RED)
      val out = source.filter(SaltAndPepperFilter(1.0, 0.0))

      // Every pixel must now be opaque white. The earlier behaviour gave
      // alpha=0 for every salt pixel.
      out.pixels().forEach { p ->
         p.alpha() shouldBe 255
         p.red() shouldBe 255
         p.green() shouldBe 255
         p.blue() shouldBe 255
      }
   }

   // Symmetric coverage for the pepper path: with pepper=1.0 every pixel
   // should be opaque black, not transparent black.
   test("pepper produces opaque black pixels on a TYPE_INT_ARGB image") {
      val source = ImmutableImage.filled(40, 30, Color.RED)
      val out = source.filter(SaltAndPepperFilter(0.0, 1.0))

      out.pixels().forEach { p ->
         p.alpha() shouldBe 255
         p.red() shouldBe 0
         p.green() shouldBe 0
         p.blue() shouldBe 0
      }
   }
})
