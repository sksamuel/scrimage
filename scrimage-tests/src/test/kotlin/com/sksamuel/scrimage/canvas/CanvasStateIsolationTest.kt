package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.canvas.drawables.FilledRect
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.Color

// A GraphicsContext that sets the paint colour.
private fun colorContext(color: Color): GraphicsContext = GraphicsContext { g -> g.setColor(color) }

/**
 * Regression: Canvas.draw / drawInPlace iterated drawables and ran each
 * drawable's GraphicsContext.configure() against a shared Graphics2D.
 * State mutations applied for drawable N (setColor, setStroke, setFont,
 * translate, rotate, setComposite, …) leaked into drawable N+1.
 *
 * The fix wraps each drawable in a Graphics2D.create() snapshot that is
 * disposed after the drawable finishes, scoping its state mutations.
 */
class CanvasStateIsolationTest : FunSpec({

   test("a previous drawable's colour does not leak into a context-less drawable") {
      // Scenario A: red rectangle then a plain (no-context) rectangle.
      // Scenario B: blue rectangle then a plain (no-context) rectangle.
      // Pre-fix, the plain rectangle inherited whatever colour the
      // previous drawable left on the shared Graphics2D, so the second
      // rectangle was red in A and blue in B. With proper scoping the
      // plain rectangle is drawn in whatever the underlying Graphics2D
      // defaults to — the same colour in both scenarios.
      val plain = FilledRect(50, 50, 10, 10)
      val redRect = FilledRect(0, 0, 10, 10, colorContext(Color.RED))
      val blueRect = FilledRect(0, 0, 10, 10, colorContext(Color.BLUE))

      val afterRed = Canvas(ImmutableImage.create(100, 100)).draw(redRect, plain).image
      val afterBlue = Canvas(ImmutableImage.create(100, 100)).draw(blueRect, plain).image

      // First-position rectangle differs (red vs blue) — sanity.
      afterRed.pixel(0, 0).red() shouldBe 255
      afterBlue.pixel(0, 0).blue() shouldBe 255

      // Plain rectangle should look the same in both scenarios — its
      // appearance must not depend on the previous drawable.
      afterRed.pixel(50, 50) shouldBe afterBlue.pixel(50, 50)
   }

   test("a configured colour does not bleed into the NEXT drawable's context-free draw") {
      // Even when both drawables use context-less constructors, drawing
      // them after a coloured drawable should still yield identical
      // colour to drawing them without that preamble.
      val plain1 = FilledRect(20, 20, 10, 10)
      val plain2 = FilledRect(60, 60, 10, 10)
      val redRect = FilledRect(0, 0, 10, 10, colorContext(Color.RED))

      val withRedPreamble = Canvas(ImmutableImage.create(100, 100)).draw(redRect, plain1, plain2).image
      val withoutPreamble = Canvas(ImmutableImage.create(100, 100)).draw(plain1, plain2).image

      withRedPreamble.pixel(20, 20) shouldBe withoutPreamble.pixel(20, 20)
      withRedPreamble.pixel(60, 60) shouldBe withoutPreamble.pixel(60, 60)
   }

   test("two coloured drawables both honour their own colour") {
      val redRect = FilledRect(0, 0, 10, 10, colorContext(Color.RED))
      val blueRect = FilledRect(20, 0, 10, 10, colorContext(Color.BLUE))

      val out = Canvas(ImmutableImage.create(100, 100)).draw(redRect, blueRect).image

      val red = out.pixel(0, 0)
      val blue = out.pixel(20, 0)
      red.red() shouldBe 255
      red.blue() shouldBe 0
      blue.blue() shouldBe 255
      blue.red() shouldBe 0
   }
})
