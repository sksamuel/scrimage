package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.canvas.Canvas
import com.sksamuel.scrimage.canvas.Drawable
import com.sksamuel.scrimage.canvas.GraphicsContext
import com.sksamuel.scrimage.graphics.RichGraphics2D
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

/**
 * Regression test for Canvas.drawInPlace and Canvas.draw not disposing
 * the Graphics2D when a Drawable threw. The previous code structure was:
 *
 *     Graphics2D g = g2(image);
 *     drawables.forEach(d -> { ... d.draw(...); ... });
 *     g.dispose();
 *
 * If any drawable threw, control jumped past dispose() and the
 * Graphics2D leaked. The fix wraps the iteration in try/finally so
 * dispose() always runs.
 *
 * Hard to assert the leak directly (no public hook into AWT's native
 * resource accounting), so the test just exercises the throwing path
 * many times. Without the fix this would steadily leak Graphics2D
 * instances; with the fix it completes cleanly.
 */
class CanvasDisposeOnThrowTest : FunSpec({

   class ThrowingDrawable : Drawable {
      override fun draw(g: RichGraphics2D) {
         throw RuntimeException("test failure")
      }
      override fun context(): GraphicsContext = GraphicsContext.identity()
   }

   test("drawInPlace propagates the drawable's exception (smoke check)") {
      val image = ImmutableImage.create(64, 64)
      val canvas = Canvas(image)
      shouldThrow<RuntimeException> {
         canvas.drawInPlace(ThrowingDrawable())
      }
   }

   test("draw propagates the drawable's exception (smoke check)") {
      val image = ImmutableImage.create(64, 64)
      val canvas = Canvas(image)
      shouldThrow<RuntimeException> {
         canvas.draw(ThrowingDrawable())
      }
   }

   test("drawInPlace does not leak Graphics2D when the drawable throws") {
      val image = ImmutableImage.create(64, 64)
      val canvas = Canvas(image)
      // 200 iterations exercising the exception path. Without dispose() in
      // a finally block this would have leaked one Graphics2D per call.
      repeat(200) {
         shouldThrow<RuntimeException> {
            canvas.drawInPlace(ThrowingDrawable())
         }
      }
   }
})
