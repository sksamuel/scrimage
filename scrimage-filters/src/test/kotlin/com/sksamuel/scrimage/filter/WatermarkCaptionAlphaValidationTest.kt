package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.awt.Color
import java.awt.Font

/**
 * Regression: WatermarkFilter, WatermarkStampFilter, WatermarkCoverFilter and
 * CaptionFilter accepted any alpha and only failed later, inside apply(), with
 * an IllegalArgumentException thrown by AlphaComposite.getInstance ("alpha
 * value out of range"). They now validate 0 <= alpha <= 1 in the constructor,
 * like VignetteFilter does for its parameters.
 */
class WatermarkCaptionAlphaValidationTest : FunSpec({

   val font = Font(Font.SANS_SERIF, Font.PLAIN, 12)
   val bad = listOf(-0.1, 1.1, Double.NaN)

   test("WatermarkFilter rejects alpha outside [0, 1] at construction") {
      bad.forEach { a ->
         val ex = shouldThrow<IllegalArgumentException> { WatermarkFilter("x", 1, 1, font, true, a, Color.WHITE) }
         ex.message!!.shouldContain("alpha")
      }
   }

   test("WatermarkStampFilter rejects alpha outside [0, 1] at construction") {
      bad.forEach { a ->
         val ex = shouldThrow<IllegalArgumentException> { WatermarkStampFilter("x", font, true, a, Color.WHITE) }
         ex.message!!.shouldContain("alpha")
      }
   }

   test("WatermarkCoverFilter rejects alpha outside [0, 1] at construction") {
      bad.forEach { a ->
         val ex = shouldThrow<IllegalArgumentException> { WatermarkCoverFilter("x", font, true, a, Color.WHITE) }
         ex.message!!.shouldContain("alpha")
      }
   }

   test("CaptionFilter rejects textAlpha and captionAlpha outside [0, 1] in both constructors") {
      bad.forEach { a ->
         shouldThrow<IllegalArgumentException> {
            CaptionFilter("x", Position.BottomLeft, font, Color.WHITE, a, true, false, Color.BLACK, 0.5, Padding(2))
         }.message!!.shouldContain("textAlpha")
         shouldThrow<IllegalArgumentException> {
            CaptionFilter("x", Position.BottomLeft, font, Color.WHITE, 0.5, true, false, Color.BLACK, a, Padding(2))
         }.message!!.shouldContain("captionAlpha")
         shouldThrow<IllegalArgumentException> {
            CaptionFilter("x", 1, 1, font, Color.WHITE, a, true, false, Color.BLACK, 0.5, Padding(2))
         }.message!!.shouldContain("textAlpha")
         shouldThrow<IllegalArgumentException> {
            CaptionFilter("x", 1, 1, font, Color.WHITE, 0.5, true, false, Color.BLACK, a, Padding(2))
         }.message!!.shouldContain("captionAlpha")
      }
   }

   test("boundary alphas 0 and 1 are accepted and still apply") {
      val image = ImmutableImage.filled(40, 40, Color.BLACK)
      listOf(0.0, 1.0).forEach { a ->
         image.filter(WatermarkFilter("x", 1, 10, font, true, a, Color.WHITE)).width shouldBe 40
         image.filter(WatermarkStampFilter("x", font, true, a, Color.WHITE)).width shouldBe 40
         image.filter(WatermarkCoverFilter("x", font, true, a, Color.WHITE)).width shouldBe 40
         image.filter(CaptionFilter("x", Position.BottomLeft, font, Color.WHITE, a, true, false, Color.BLACK, a, Padding(2))).width shouldBe 40
      }
   }
})
