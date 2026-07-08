package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Regression test for brightness() on palette-based images.
 *
 * rescaleInPlace used java.awt.image.RescaleOp unconditionally, and RescaleOp
 * rejects IndexColorModel images. Since the default loader preserves the
 * underlying image type, any GIF (TYPE_BYTE_INDEXED) crashed with
 * "IllegalArgumentException: Rescaling cannot be performed on an indexed image".
 */
class BrightnessIndexedTest : FunSpec({

   fun luminanceSum(image: ImmutableImage): Long {
      var sum = 0L
      image.forEach { p -> sum += p.red() + p.green() + p.blue() }
      return sum
   }

   test("brightness works on an indexed image with the default palette") {
      // default-palette TYPE_BYTE_INDEXED, filled with a mid-gray
      val buf = BufferedImage(20, 20, BufferedImage.TYPE_BYTE_INDEXED)
      val g2 = buf.createGraphics()
      g2.color = java.awt.Color(102, 102, 102)
      g2.fillRect(0, 0, 20, 20)
      g2.dispose()
      val original = ImmutableImage.wrapAwt(buf)

      val brightened = original.brightness(1.5)
      brightened.width shouldBe 20
      brightened.height shouldBe 20
      // 102 * 1.5 = 153, quantized to the nearest default-palette entry
      (brightened.pixel(10, 10).red() > 102) shouldBe true
   }

   test("brightness works on an indexed image loaded from a GIF") {
      val original = ImmutableImage.loader().fromStream(javaClass.getResourceAsStream("/github174.gif"))
      original.awt().type shouldBe BufferedImage.TYPE_BYTE_INDEXED

      // compare against a factor-1.0 pass through the same pipeline so the assertion
      // holds regardless of any quirks elsewhere in the copy() path
      val unchanged = original.brightness(1.0)
      val brightened = original.brightness(1.5)
      luminanceSum(brightened) shouldBeGreaterThan luminanceSum(unchanged)
   }

   test("brightness on an indexed image leaves the original untouched") {
      val original = ImmutableImage.loader().fromStream(javaClass.getResourceAsStream("/github174.gif"))
      val before = original.argbints()
      original.brightness(1.5)
      original.argbints() shouldBe before
   }
})
