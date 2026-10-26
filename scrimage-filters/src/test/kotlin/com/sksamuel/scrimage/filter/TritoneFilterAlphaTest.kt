package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression: the jhlabs TritoneFilter returned the lookup-table colour verbatim, so the alpha of
 * the output was whatever alpha the three tone colours carried, not the alpha of the source pixel.
 * With the int constructor and 24-bit colours (0xff0000 etc.) every output pixel became fully
 * transparent; with the Color constructor (opaque colours) transparent source pixels became opaque.
 * The source alpha must be preserved and only the RGB taken from the LUT, as ThresholdFilter does.
 */
class TritoneFilterAlphaTest : FunSpec({

   test("int constructor with 24-bit colours keeps an opaque image opaque") {
      val src = ImmutableImage.loader().fromResource("/bird.jpg").scaleTo(60, 40).copy(java.awt.image.BufferedImage.TYPE_INT_ARGB)
      val out = src.filter(TritoneFilter(0xff0000, 0x00ff00, 0x0000ff))
      out.argbints().all { (it ushr 24) == 255 } shouldBe true
   }

   test("Color constructor preserves source transparency") {
      val src = ImmutableImage.create(10, 10) // fully transparent
      val out = src.filter(TritoneFilter(Color.RED, Color.GREEN, Color.BLUE))
      out.argbints().all { (it ushr 24) == 0 } shouldBe true
   }

   test("partial source alpha is preserved while the colour is remapped") {
      val src = ImmutableImage.filled(4, 4, Color(255, 255, 255, 77))
      val out = src.filter(TritoneFilter(Color.RED, Color.GREEN, Color.BLUE))
      val p = out.pixel(1, 1)
      p.alpha() shouldBe 77
      // white has maximum brightness, so it maps to the highlight colour
      p.red() shouldBe 0
      p.green() shouldBe 0
      p.blue() shouldBe 255
   }

   test("the int and Color constructors give the same RGB for the same colours") {
      val src = ImmutableImage.loader().fromResource("/bird.jpg").scaleTo(60, 40)
      val a = src.filter(TritoneFilter(0x402000, 0x808080, 0xffffe0)).argbints().map { it and 0xffffff }
      val b = src.filter(TritoneFilter(Color(0x402000), Color(0x808080), Color(0xffffe0))).argbints().map { it and 0xffffff }
      a shouldBe b
   }
})
