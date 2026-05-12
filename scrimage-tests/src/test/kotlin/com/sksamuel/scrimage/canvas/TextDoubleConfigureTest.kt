package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.canvas.drawables.Text
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.Font

class TextDoubleConfigureTest : FunSpec({

   // Regression for Text.draw calling context.configure() a second time.
   // Canvas.draw / drawInPlace already invokes d.context().configure(g2)
   // before calling d.draw(g2) — same as it does for every other Drawable —
   // and Text was the only Drawable that re-applied its context inside
   // draw(). For overwriting setters (setColor, setFont, setComposite) the
   // duplicate is a no-op, but for *additive* operations (translate, rotate,
   // transform) it doubled the effect.
   //
   // Concrete demonstration: build a Text with a context that translates by
   // (50, 50) then sets a black foreground, draw it at coordinates (0, 0)
   // on a white canvas, and check where the text actually lands. With the
   // previous double-configure the translate ran twice, so the text rendered
   // at canvas position (100, 100) instead of (50, 50).
   test("Text honours its translate context exactly once") {
      val canvas = Canvas(ImmutableImage.filled(160, 160, Color.WHITE))

      val drawn = canvas.draw(
         Text("X", 0, 0) { g ->
            g.translate(50, 50)
            g.color = Color.BLACK
            g.font = Font(Font.SANS_SERIF, Font.PLAIN, 14)
         }
      ).image

      // Helper: count how many pixels in a row band are markedly darker
      // than the white background (i.e. ink pixels from the rendered glyph).
      fun inkPixelsInRow(y: Int): Int {
         var count = 0
         for (x in 0 until drawn.width) {
            val p = drawn.pixel(x, y)
            // White background is (255,255,255). Anything noticeably darker
            // counts as ink. AA produces grey pixels around the glyph, so
            // pick a generous threshold.
            if (p.red() < 200 && p.green() < 200 && p.blue() < 200) count++
         }
         return count
      }

      // The glyph baseline of a 14pt sans-serif "X" rendered at y=0 lands
      // a few pixels above y, so look in a band around the requested top
      // (y=50, after one translate) versus the doubled position (y=100,
      // after the buggy double translate).
      val inkAround50 = (40..70).sumOf { inkPixelsInRow(it) }
      val inkAround100 = (90..120).sumOf { inkPixelsInRow(it) }

      // Expect: ink lands around y=50, not y=100.
      (inkAround50 > 0) shouldBe true
      inkAround100 shouldBe 0
   }
})
