package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Regression tests for RaysFilter on images that are not TYPE_INT_ARGB. The jhlabs RaysFilter
 * composites the rays with MiscComposite.ADD, whose RGBCompositeContext reads 4 samples per pixel
 * via Raster.getPixels, so on any raster without exactly four bands (a loaded JPEG is
 * TYPE_3BYTE_BGR, TYPE_INT_RGB / TYPE_INT_BGR have 3, TYPE_BYTE_GRAY has 1) it threw
 * ArrayIndexOutOfBoundsException. The wrapper must declare TYPE_INT_ARGB so the image is
 * converted first.
 */
class RaysFilterTypesTest : FunSpec({

   val jpg = ImmutableImage.loader().fromResource("/bird.jpg").scaleTo(200, 150)

   test("loaded jpeg is TYPE_3BYTE_BGR (fixture sanity)") {
      jpg.type shouldBe BufferedImage.TYPE_3BYTE_BGR
   }

   test("RaysFilter on a loaded jpeg does not throw") {
      shouldNotThrowAny { jpg.filter(RaysFilter()) }
   }

   listOf(
      BufferedImage.TYPE_INT_RGB to "TYPE_INT_RGB",
      BufferedImage.TYPE_INT_BGR to "TYPE_INT_BGR",
      BufferedImage.TYPE_BYTE_GRAY to "TYPE_BYTE_GRAY",
      BufferedImage.TYPE_4BYTE_ABGR to "TYPE_4BYTE_ABGR",
   ).forEach { (type, name) ->
      test("RaysFilter on $name does not throw") {
         shouldNotThrowAny { jpg.copy(type).filter(RaysFilter()) }
      }
   }

   test("RaysFilter on TYPE_INT_RGB matches the TYPE_INT_ARGB result") {
      val expected = jpg.copy(BufferedImage.TYPE_INT_ARGB).filter(RaysFilter()).argbints()
      jpg.copy(BufferedImage.TYPE_INT_RGB).filter(RaysFilter()).argbints() shouldBe expected
      jpg.filter(RaysFilter()).argbints() shouldBe expected
   }
})
