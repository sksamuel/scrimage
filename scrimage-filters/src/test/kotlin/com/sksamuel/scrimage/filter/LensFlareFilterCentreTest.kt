package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import java.awt.Color

/**
 * Verifies the color and centre constructor parameters are passed through to the
 * jhlabs FlareFilter (setColor / setCentre), so changing either changes the
 * rendered flare.
 */
class LensFlareFilterCentreTest : FunSpec({

   val src = ImmutableImage.create(80, 80).map { p -> Color((p.x() * 3) % 256, (p.y() * 3) % 256, 90) }

   test("centre is passed through to the flare") {
      val centred = src.filter(LensFlareFilter())
      val offset = src.filter(LensFlareFilter(0xffffffff.toInt(), 0.2f, 0.8f))
      centred shouldNotBe offset
   }

   test("color is passed through to the flare") {
      val white = src.filter(LensFlareFilter(0xffffffff.toInt(), 0.5f, 0.5f))
      val red = src.filter(LensFlareFilter(0xffff0000.toInt(), 0.5f, 0.5f))
      white shouldNotBe red
   }
})
