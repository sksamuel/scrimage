package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Regression tests for the filters whose whole effect is on the alpha channel: DissolveFilter,
 * InvertAlphaFilter and AlphaMaskFilter. None of them declared supported types, so on an image
 * without an alpha channel (a loaded JPEG is TYPE_3BYTE_BGR, or TYPE_INT_RGB) the alpha they wrote
 * was discarded by the raster and the filter was a silent no-op. They must declare TYPE_INT_ARGB so
 * ImmutableImage.filter converts the image first.
 */
class AlphaFilterTypesTest : FunSpec({

   val jpg = ImmutableImage.loader().fromResource("/bird.jpg").scaleTo(200, 150)
   val rgb = jpg.copy(BufferedImage.TYPE_INT_RGB)

   test("loaded jpeg is TYPE_3BYTE_BGR (fixture sanity)") {
      jpg.type shouldBe BufferedImage.TYPE_3BYTE_BGR
   }

   test("DissolveFilter on a loaded jpeg dissolves pixels") {
      val out = jpg.filter(DissolveFilter(0.5f))
      out shouldNotBe jpg
      out.argbints().count { (it ushr 24) == 0 } shouldNotBe 0
   }

   test("DissolveFilter on TYPE_INT_RGB dissolves pixels") {
      val out = rgb.filter(DissolveFilter(0.5f))
      out shouldNotBe rgb
      out.argbints().count { (it ushr 24) == 0 } shouldNotBe 0
   }

   test("InvertAlphaFilter on a loaded jpeg makes every pixel transparent") {
      jpg.filter(InvertAlphaFilter()).argbints().all { (it ushr 24) == 0 } shouldBe true
   }

   test("InvertAlphaFilter on TYPE_INT_RGB makes every pixel transparent") {
      rgb.filter(InvertAlphaFilter()).argbints().all { (it ushr 24) == 0 } shouldBe true
   }

   test("AlphaMaskFilter on a loaded jpeg applies the mask alpha") {
      val mask = ImmutableImage.filled(jpg.width, jpg.height, Color(0, 0, 0, 0))
      jpg.filter(AlphaMaskFilter(mask)).pixel(5, 5).alpha() shouldBe 0
   }

   test("AlphaMaskFilter on TYPE_INT_RGB applies the mask alpha") {
      val mask = ImmutableImage.filled(rgb.width, rgb.height, Color(0, 0, 0, 128))
      rgb.filter(AlphaMaskFilter(mask)).pixel(5, 5).alpha() shouldBe 128
   }

   test("colour channels are preserved when the alpha filters are applied to a loaded jpeg") {
      val mask = ImmutableImage.filled(jpg.width, jpg.height, Color(0, 0, 0, 77))
      jpg.filter(AlphaMaskFilter(mask)).argbints().map { it and 0xffffff } shouldBe jpg.argbints().map { it and 0xffffff }
      jpg.filter(InvertAlphaFilter()).argbints().map { it and 0xffffff } shouldBe jpg.argbints().map { it and 0xffffff }
   }
})
