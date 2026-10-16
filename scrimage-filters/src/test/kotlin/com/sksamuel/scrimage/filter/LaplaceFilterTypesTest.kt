package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.image.BufferedImage

/**
 * Regression: the jhlabs LaplaceFilter used the destination image as scratch space for its first
 * pass, writing raw grey ints through setRGB and reading them back with getRGB. That only round
 * trips on images whose raster stores raw ints (TYPE_INT_ARGB / TYPE_INT_RGB); through any other
 * ColorModel (a loaded JPEG is TYPE_3BYTE_BGR, also TYPE_INT_BGR) the grey landed in the blue
 * channel with alpha forced to 0xff, so the second pass's `r <= 128` test always failed and the
 * output was entirely black. The filter now uses a separate grey buffer, so every type works.
 */
class LaplaceFilterTypesTest : FunSpec({

   val jpg = ImmutableImage.loader().fromResource("/bird.jpg").scaleTo(200, 150)
   val expected = jpg.copy(BufferedImage.TYPE_INT_ARGB).filter(LaplaceFilter()).argbints().toList()

   test("loaded jpeg is TYPE_3BYTE_BGR (fixture sanity)") {
      jpg.type shouldBe BufferedImage.TYPE_3BYTE_BGR
   }

   test("LaplaceFilter on a loaded jpeg is not all black") {
      jpg.filter(LaplaceFilter()).argbints().count { it != 0xff000000.toInt() } shouldNotBe 0
   }

   test("LaplaceFilter on a loaded jpeg matches the TYPE_INT_ARGB result") {
      jpg.filter(LaplaceFilter()).argbints().toList() shouldBe expected
   }

   listOf(
      BufferedImage.TYPE_INT_RGB to "TYPE_INT_RGB",
      BufferedImage.TYPE_INT_BGR to "TYPE_INT_BGR",
      BufferedImage.TYPE_4BYTE_ABGR to "TYPE_4BYTE_ABGR",
      BufferedImage.TYPE_3BYTE_BGR to "TYPE_3BYTE_BGR",
   ).forEach { (type, name) ->
      test("LaplaceFilter on $name matches the TYPE_INT_ARGB result") {
         jpg.copy(type).filter(LaplaceFilter()).argbints().toList() shouldBe expected
      }
   }

   test("LaplaceFilter on TYPE_BYTE_GRAY is not all black") {
      // exact equality with the ARGB path is not expected here: writing into a BYTE_GRAY raster
      // goes through the linear grey colour space, which is lossy
      jpg.copy(BufferedImage.TYPE_BYTE_GRAY).filter(LaplaceFilter()).argbints().count { it != 0xff000000.toInt() } shouldNotBe 0
   }

   test("TYPE_INT_ARGB output is bit-identical to the output before the scratch-buffer change") {
      // checksum captured on master before the change, so the refactor is proven output-neutral
      expected.hashCode() shouldBe -500998158
   }
})
