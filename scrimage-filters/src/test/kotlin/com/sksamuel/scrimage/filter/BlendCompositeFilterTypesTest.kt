package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.image.BufferedImage

/**
 * Regression tests for filters built on thirdparty.romainguy.BlendComposite. The composite only
 * accepts rasters backed by DataBuffer.TYPE_INT (so a loaded JPEG, TYPE_3BYTE_BGR, threw an
 * IllegalStateException) and reads the alpha byte from the raw data elements, which is 0 for
 * TYPE_INT_RGB / TYPE_INT_BGR, so on those types the blend contributed nothing and the filter was a
 * silent no-op. Each affected filter must therefore declare TYPE_INT_ARGB as its only supported type
 * so that ImmutableImage.filter converts the image first.
 */
class BlendCompositeFilterTypesTest : FunSpec({

   fun rgbOnly(image: ImmutableImage): List<Int> = image.argbints().map { it and 0xffffff }

   val jpg = ImmutableImage.loader().fromResource("/bird.jpg").scaleTo(200, 150)
   val rgb = jpg.copy(BufferedImage.TYPE_INT_RGB)
   val bgr = jpg.copy(BufferedImage.TYPE_INT_BGR)

   test("loaded jpeg is TYPE_3BYTE_BGR (fixture sanity)") {
      jpg.type shouldBe BufferedImage.TYPE_3BYTE_BGR
   }

   test("VignetteFilter on a loaded jpeg does not throw and changes the image") {
      val out = shouldNotThrowAny { jpg.filter(VignetteFilter()) }
      rgbOnly(out) shouldNotBe rgbOnly(jpg)
   }

   test("VignetteFilter on TYPE_INT_RGB is not a no-op") {
      rgbOnly(rgb.filter(VignetteFilter())) shouldNotBe rgbOnly(rgb)
   }

   test("BackgroundBlendFilter on TYPE_INT_RGB is not a no-op") {
      rgbOnly(rgb.filter(BackgroundBlendFilter())) shouldNotBe rgbOnly(rgb)
   }

   test("SnowFilter on TYPE_INT_RGB is not a no-op") {
      rgbOnly(rgb.filter(SnowFilter())) shouldNotBe rgbOnly(rgb)
   }

   test("SummerFilter on TYPE_INT_RGB is not a no-op") {
      rgbOnly(rgb.filter(SummerFilter(false))) shouldNotBe rgbOnly(rgb)
   }

   test("TYPE_INT_RGB and TYPE_INT_BGR results match the TYPE_INT_ARGB result for each filter") {
      val argb = jpg.copy(BufferedImage.TYPE_INT_ARGB)
      listOf(VignetteFilter(), BackgroundBlendFilter(), SnowFilter(), SummerFilter(true), OldPhotoFilter(), NashvilleFilter()).forEach { filter ->
         val expected = argb.filter(filter).argbints()
         rgb.filter(filter).argbints() shouldBe expected
         bgr.filter(filter).argbints() shouldBe expected
      }
   }
})
