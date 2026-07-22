package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: LensBlurFilter's FFT quadrant-remap loop assumed a square tile.
 * Once tileWidth/tileHeight are clamped to the image size, an image with a
 * small dimension produces a non-square tile (w != h). The remap iterated
 * `y < w` and flipped both axes by `w`, which indexed past the w*h buffers:
 * a wider-than-tall image threw ArrayIndexOutOfBoundsException, and a
 * taller-than-wide one silently corrupted the output (stale/dark bands).
 *
 * Blurring a uniform image must return (approximately) the same uniform
 * colour, so the centre pixel is a good probe for both the crash and the
 * corruption. Large images (both dims clamp to 128) keep a square tile and
 * are unaffected.
 */
class LensBlurFilterNonSquareTest : FunSpec({

   val gray = 100

   fun uniform(width: Int, height: Int): ImmutableImage =
      ImmutableImage.filled(width, height, Color(gray, gray, gray))

   fun assertBlurredUniform(width: Int, height: Int) {
      val result = uniform(width, height).filter(LensBlurFilter())
      result.width shouldBe width
      result.height shouldBe height
      val centre = result.pixel(width / 2, height / 2)
      centre.red() shouldBeInRange (gray - 12)..(gray + 12)
      centre.green() shouldBeInRange (gray - 12)..(gray + 12)
      centre.blue() shouldBeInRange (gray - 12)..(gray + 12)
   }

   test("lens blur of a wider-than-tall small image does not crash and stays uniform") {
      assertBlurredUniform(60, 20)
   }

   test("lens blur of a taller-than-wide small image does not corrupt and stays uniform") {
      assertBlurredUniform(20, 60)
   }

   test("lens blur of a small square image stays uniform") {
      assertBlurredUniform(40, 40)
   }
})
