package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Regression tests for frame disposal handling in the GIF reader. When building frame N's
 * canvas the reader must apply frame N-1's disposal method (the GIF spec defines disposal as
 * what to do with the canvas after a frame is shown, before the next is composited). The
 * reader previously consulted frame N's own disposal, which only matters when consecutive
 * frames use different disposal methods - so neither existing all-Background nor all-Previous
 * fixture exposed it. Expected pixels below were cross-checked against ImageMagick `-coalesce`.
 */
class AnimatedGifDisposalTest : FunSpec({

   test("restore-to-background disposal of the previous frame clears its pixels") {
      // mixed_disposal.gif: frame 0 = red 5x5 at top-left, dispose=restoreToBackground;
      // frame 1 = blue 5x5 at bottom-right, dispose=none. Frame 0 must be cleared before
      // frame 1, so frame 1's top-left is transparent background, not frame 0's red.
      val gif = AnimatedGifReader.read(
         ImageSource.of(javaClass.getResourceAsStream("/com/sksamuel/scrimage/nio/mixed_disposal.gif"))
      )
      gif.frameCount shouldBe 2
      val frame1 = gif.getFrame(1)
      // top-left was red in frame 0; after restore-to-background it must be transparent
      frame1.pixel(0, 0).alpha() shouldBe 0
      // the blue patch frame 1 actually carries is present
      frame1.pixel(7, 7).toARGBInt() shouldBe java.awt.Color(0, 0, 255).rgb
   }

   test("restore-to-previous disposal restores to a frame-0 base image") {
      // canvas_prev.gif: frame 0 = full 100x100 skyblue background (doNotDispose); frames 1-4
      // are 32x32 patches with dispose=restoreToPrevious. Each later frame must be composited
      // onto the restored frame-0 background, so background pixels outside the patch survive.
      val gif = AnimatedGifReader.read(
         ImageSource.of(javaClass.getResourceAsStream("/com/sksamuel/scrimage/nio/canvas_prev.gif"))
      )
      val frame0 = gif.getFrame(0)
      val frame1 = gif.getFrame(1)
      val bg = frame0.pixel(0, 0)
      // sanity: frame 0's corner is an opaque background colour, not transparent
      bg.alpha() shouldBe 255
      // frame 1's corner (outside any patch) must still show the frame-0 background,
      // not transparent (which is what the lost-base-frame bug produced).
      frame1.pixel(0, 0).toARGBInt() shouldBe bg.toARGBInt()
      frame1.pixel(99, 99).alpha() shouldNotBe 0
   }
})
