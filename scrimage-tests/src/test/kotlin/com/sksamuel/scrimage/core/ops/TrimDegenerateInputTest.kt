package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.awt.Color

class TrimDegenerateInputTest : FunSpec({

   // Regression: trim(left, top, right, bottom) used to delegate straight
   // to subimage(left, top, width-left-right, height-bottom-top), and when
   // the trim amounts equalled or exceeded the source dimensions the
   // caller saw a bare "Width cannot be <= 0" / "Height cannot be <= 0"
   // RuntimeException out of subimage with no context about which trim
   // amounts caused it. Now trim validates up front and throws a clear
   // IllegalArgumentException naming the offending arguments.

   val source = ImmutableImage.filled(100, 80, Color.RED)

   test("trim throws IllegalArgumentException when left+right equals width") {
      val ex = shouldThrow<IllegalArgumentException> {
         source.trim(50, 0, 50, 0)
      }
      ex.message!!.shouldContain("trim amounts leave no remaining image")
      ex.message!!.shouldContain("width=100")
   }

   test("trim throws IllegalArgumentException when top+bottom exceeds height") {
      val ex = shouldThrow<IllegalArgumentException> {
         source.trim(0, 50, 0, 40)
      }
      ex.message!!.shouldContain("trim amounts leave no remaining image")
      ex.message!!.shouldContain("height=80")
   }

   test("trim rejects negative trim amounts") {
      val ex = shouldThrow<IllegalArgumentException> {
         source.trim(-1, 0, 0, 0)
      }
      ex.message!!.shouldContain("trim amounts must be non-negative")
   }

   test("trim still works for valid amounts that leave a positive remaining area") {
      val out = source.trim(10, 5, 10, 5)
      out.width shouldBe 80
      out.height shouldBe 70
   }
})
