package com.sksamuel.scrimage.core.scaling

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Regression test for AwtNearestNeighbourScale losing channel precision
 * when the source has alpha != 255. The previous implementation used
 * Graphics2D.drawImage with NEAREST_NEIGHBOR interpolation, which
 * composites via SrcOver and premultiplies alpha — losing ±1 per
 * colour channel. Same root cause as #414 / #416 / #425.
 *
 * AwtNearestNeighbourScale is the FastScale path for any case that
 * isn't "downscale on both axes AND TYPE_INT_ARGB" (those use the
 * already-exact ScrimageNearestNeighbourScale).
 */
class AwtNearestNeighbourScaleTest : FunSpec({

   test("FastScale up-scaling preserves alpha=128 channels exactly") {
      // Force the AwtNearestNeighbourScale path: any dimension >= source.
      val pixels = arrayOf(
         Pixel(0, 0, 100, 150, 200, 128),
         Pixel(1, 0, 50, 75, 25, 200)
      )
      val src = ImmutableImage.create(2, 1, pixels)
      val scaled = src.scaleTo(4, 1, ScaleMethod.FastScale)
      // Pixels (0, 0) and (1, 0) should be exact copies of source pixel 0
      scaled.pixel(0, 0).red() shouldBe 100
      scaled.pixel(0, 0).green() shouldBe 150
      scaled.pixel(0, 0).blue() shouldBe 200
      scaled.pixel(0, 0).alpha() shouldBe 128
      // Pixels (2, 0) and (3, 0) should be exact copies of source pixel 1
      scaled.pixel(2, 0).red() shouldBe 50
      scaled.pixel(2, 0).green() shouldBe 75
      scaled.pixel(2, 0).blue() shouldBe 25
      scaled.pixel(2, 0).alpha() shouldBe 200
   }

   test("FastScale on TYPE_4BYTE_ABGR (non-INT_ARGB) preserves alpha exactly") {
      // Force the AwtNearestNeighbourScale path by using a non-INT_ARGB type.
      val src = ImmutableImage.create(2, 2, BufferedImage.TYPE_4BYTE_ABGR)
      src.setColor(0, 0, com.sksamuel.scrimage.color.RGBColor(100, 150, 200, 128))
      src.setColor(1, 0, com.sksamuel.scrimage.color.RGBColor(50, 75, 25, 200))
      src.setColor(0, 1, com.sksamuel.scrimage.color.RGBColor(255, 0, 0, 64))
      src.setColor(1, 1, com.sksamuel.scrimage.color.RGBColor(0, 255, 0, 192))

      val scaled = src.scaleTo(1, 1, ScaleMethod.FastScale)
      // 2x2 → 1x1 NN samples (0, 0)
      val p = scaled.pixel(0, 0)
      p.red() shouldBe 100
      p.green() shouldBe 150
      p.blue() shouldBe 200
      p.alpha() shouldBe 128
   }
})
