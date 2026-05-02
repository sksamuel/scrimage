package com.sksamuel.scrimage.core.nio.internal

import com.sksamuel.scrimage.nio.internal.GifSequenceReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Regression for a bug where [GifSequenceReader.resetFrame] declared three
 * local variables (`int dispose = 0; boolean transparency = false; int delay = 0`)
 * that shadowed the protected instance fields with the same names. The
 * locals were assigned but never used, so the previous frame's `dispose`,
 * `transparency` and `delay` values silently leaked into the next frame
 * whenever that frame omitted its (optional) graphics control extension.
 *
 * The test subclasses the reader to access the protected fields, sets them
 * to non-default values to simulate "the previous frame had a GCE", invokes
 * `resetFrame`, and asserts they're back to their per-frame defaults.
 */
class GifSequenceReaderResetFrameTest : FunSpec({

   class ProbeReader : GifSequenceReader() {
      fun primeAndReset(d: Int, t: Boolean, dl: Int) {
         dispose = d
         transparency = t
         delay = dl
         resetFrame()
      }

      val currentDispose: Int get() = dispose
      val currentTransparency: Boolean get() = transparency
      val currentDelay: Int get() = delay
   }

   test("resetFrame clears dispose/transparency/delay back to defaults") {
      val reader = ProbeReader()
      reader.primeAndReset(2, true, 250)
      reader.currentDispose shouldBe 0
      reader.currentTransparency shouldBe false
      reader.currentDelay shouldBe 0
   }
})
