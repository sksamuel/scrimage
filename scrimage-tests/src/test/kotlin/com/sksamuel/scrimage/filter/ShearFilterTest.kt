package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class ShearFilterTest : FunSpec({

   test("shearing produces a same-sized image without throwing") {
      // The scrimage wrapper requests setResize(false) so the output keeps the
      // input dimensions, but the resize flag was ignored: transformSpace always
      // enlarged the output rectangle, which overflowed the same-sized destination
      // buffer and threw ArrayIndexOutOfBoundsException from setRGB.
      val src = ImmutableImage.filled(20, 20, Color.WHITE)
      val out = src.filter(ShearFilter(0.5f, 0.0f))
      out.width shouldBe 20
      out.height shouldBe 20
      // the top-left corner stays within the source, so it remains white
      out.pixel(0, 0).toARGBInt() shouldBe Color.WHITE.rgb
   }

   test("shearing on both axes also stays in bounds") {
      val out = ImmutableImage.filled(15, 25, Color.WHITE).filter(ShearFilter(0.3f, 0.2f))
      out.width shouldBe 15
      out.height shouldBe 25
   }
})
