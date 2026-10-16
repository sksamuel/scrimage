package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.Color

/**
 * Covers the raysOnly constructor parameter, which is passed through to
 * thirdparty.jhlabs.image.RaysFilter.setRaysOnly.
 *
 * Regression: raysOnly used to have no observable effect. BufferedOpFilter
 * calls op().filter(img, img) with the same image as src and dst, and in the
 * jhlabs RaysFilter the flag only controlled whether the source was drawn onto
 * dst before the rays were added. Since dst already *was* the source, skipping
 * that draw changed nothing and the rays were always composited over the
 * original image. The jhlabs filter now clears dst to opaque black when
 * raysOnly is set, so the output contains only the rays.
 */
class RaysFilterRaysOnlyTest : FunSpec({

   // A bright spot on a dark background, plus a mid-grey block in the corner that is
   // below the threshold and so must vanish when only the rays are rendered.
   val src = ImmutableImage.create(100, 100).map { p ->
      val dx = p.x() - 50; val dy = p.y() - 50
      if (dx * dx + dy * dy < 10 * 10) Color(255, 255, 255)
      else if (p.x() < 10 && p.y() < 10) Color(100, 100, 100)
      else Color.BLACK
   }

   test("raysOnly = true is accepted and the filter applies") {
      val out = src.filter(RaysFilter(1.0f, 0.5f, 0.5f, 0f, 0f, 0.35f, 0f, true))
      out.width shouldBe 100
      out.height shouldBe 100
   }

   test("raysOnly = true output differs from the composited output") {
      val raysOnly = src.filter(RaysFilter(1.0f, 0.5f, 0.5f, 0f, 0f, 0.35f, 0f, true))
      val composited = src.filter(RaysFilter(1.0f, 0.5f, 0.5f, 0f, 0f, 0.35f, 0f, false))
      raysOnly shouldNotBe composited
   }

   test("raysOnly = true renders the rays over opaque black, dropping the source image") {
      val raysOnly = src.filter(RaysFilter(1.0f, 0.5f, 0.5f, 0f, 0f, 0.35f, 0f, true))
      // the grey block is below the threshold and far from the bright spot, so no rays reach it
      val corner = raysOnly.pixel(2, 2)
      corner.red() shouldBe 0
      corner.green() shouldBe 0
      corner.blue() shouldBe 0
      corner.alpha() shouldBe 255
      // the rays themselves are still present
      raysOnly.argbints().count { (it and 0xffffff) != 0 } shouldNotBe 0
   }

   test("raysOnly = false keeps the source image under the rays") {
      val composited = src.filter(RaysFilter(1.0f, 0.5f, 0.5f, 0f, 0f, 0.35f, 0f, false))
      composited.pixel(2, 2).red() shouldBe 100
   }

   test("the seven-arg constructor defaults raysOnly to false") {
      val sevenArg = src.filter(RaysFilter(1.0f, 0f, 0.5f, 0f, 0f, 0.35f, 0f))
      val explicitFalse = src.filter(RaysFilter(1.0f, 0f, 0.5f, 0f, 0f, 0.35f, 0f, false))
      sevenArg shouldBe explicitFalse
   }
})
