package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: QuantizeFilter discarded the source alpha channel. The octree
 * colour table forces every entry to 0xff alpha and the jhlabs filter wrote
 * table entries straight into the output, so a fully transparent pixel and a
 * half-transparent pixel both came out opaque. In dither mode the error
 * diffusion write-back also stripped alpha from every neighbour it touched.
 * The filter now keeps each source pixel's alpha and quantizes only RGB.
 */
class QuantizeFilterAlphaTest : FunSpec({

   fun source(): ImmutableImage {
      val img = ImmutableImage.filled(4, 4, Color.RED)
      img.awt().setRGB(1, 1, 0x00000000)
      img.awt().setRGB(2, 2, 0x8000ff00.toInt())
      return img
   }

   test("QuantizeFilter without dither preserves source alpha") {
      val out = source().filter(QuantizeFilter(8))
      "a11=${out.pixel(1, 1).alpha()} a22=${out.pixel(2, 2).alpha()} a00=${out.pixel(0, 0).alpha()}" shouldBe "a11=0 a22=128 a00=255"
   }

   test("QuantizeFilter with dither preserves source alpha") {
      val out = source().filter(QuantizeFilter(8, true))
      "a11=${out.pixel(1, 1).alpha()} a22=${out.pixel(2, 2).alpha()} a00=${out.pixel(0, 0).alpha()}" shouldBe "a11=0 a22=128 a00=255"
      // and no opaque pixel has lost its alpha to the error-diffusion write-back
      for (y in 0 until 4) for (x in 0 until 4) {
         if (!((x == 1 && y == 1) || (x == 2 && y == 2))) out.pixel(x, y).alpha() shouldBe 255
      }
   }

   test("QuantizeFilter still quantizes colours on an opaque image") {
      val img = ImmutableImage.create(40, 40)
      for (y in 0 until 40) for (x in 0 until 40) img.awt().setRGB(x, y, Color((x * 6) % 256, (y * 6) % 256, 128).rgb)
      val out = img.filter(QuantizeFilter(16))
      (out.argbints().toSet().size <= 16) shouldBe true
      out.argbints().all { (it ushr 24) == 0xff } shouldBe true
   }
})
