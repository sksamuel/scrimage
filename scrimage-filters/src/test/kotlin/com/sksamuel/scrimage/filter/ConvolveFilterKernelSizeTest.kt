package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.awt.Color

/**
 * Regression: ConvolveFilter accepted even-sized kernels but the jhlabs convolution is centre-tap
 * based (it loops from -size/2 to +size/2, i.e. size + 1 taps for an even size), so applying a 2x2
 * or 2x1 kernel threw ArrayIndexOutOfBoundsException from deep inside the convolution. The
 * constructor now rejects even kernel dimensions up front.
 */
class ConvolveFilterKernelSizeTest : FunSpec({

   val src = ImmutableImage.filled(16, 16, Color(90, 140, 200))

   test("constructor rejects an even kernel width") {
      val ex = shouldThrow<IllegalArgumentException> { ConvolveFilter(2, 1, floatArrayOf(0.5f, 0.5f)) }
      ex.message!!.shouldContain("odd")
   }

   test("constructor rejects an even kernel height") {
      val ex = shouldThrow<IllegalArgumentException> { ConvolveFilter(1, 2, floatArrayOf(0.5f, 0.5f)) }
      ex.message!!.shouldContain("odd")
   }

   test("constructor rejects a 2x2 kernel") {
      shouldThrow<IllegalArgumentException> { ConvolveFilter(2, 2, floatArrayOf(0.25f, 0.25f, 0.25f, 0.25f)) }
   }

   test("odd kernels of any size still apply cleanly") {
      shouldNotThrowAny {
         src.filter(ConvolveFilter(1, 1, floatArrayOf(1f)))
         src.filter(ConvolveFilter(3, 1, floatArrayOf(1 / 3f, 1 / 3f, 1 / 3f)))
         src.filter(ConvolveFilter(1, 5, FloatArray(5) { 0.2f }))
         src.filter(ConvolveFilter(5, 3, FloatArray(15) { 1 / 15f }))
      }
   }

   test("a 1x1 identity kernel returns the image unchanged") {
      src.filter(ConvolveFilter(1, 1, floatArrayOf(1f))) shouldBe src
   }
})
