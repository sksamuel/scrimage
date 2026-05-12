package com.sksamuel.scrimage.core.scaling

import com.sksamuel.scrimage.scaling.ScrimageNearestNeighbourScale
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import java.awt.image.BufferedImage

/**
 * Regression: ScrimageNearestNeighbourScale unconditionally cast the
 * source DataBuffer to DataBufferInt. For non-int-buffer image types
 * (TYPE_BYTE_GRAY, TYPE_4BYTE_ABGR, TYPE_3BYTE_BGR, etc.) callers got
 * a context-free ClassCastException from inside the scale method.
 *
 * Validate up front with IllegalArgumentException so the contract is
 * obvious.
 */
class ScrimageNearestNeighbourScaleBufferTest : FunSpec({

   test("rejects TYPE_BYTE_GRAY (byte-buffer) with a helpful message") {
      val gray = BufferedImage(20, 20, BufferedImage.TYPE_BYTE_GRAY)
      shouldThrow<IllegalArgumentException> {
         ScrimageNearestNeighbourScale().scale(gray, 10, 10)
      }
   }

   test("rejects TYPE_4BYTE_ABGR (byte-buffer)") {
      val abgr = BufferedImage(20, 20, BufferedImage.TYPE_4BYTE_ABGR)
      shouldThrow<IllegalArgumentException> {
         ScrimageNearestNeighbourScale().scale(abgr, 10, 10)
      }
   }

   test("accepts TYPE_INT_ARGB") {
      val argb = BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB)
      val out = ScrimageNearestNeighbourScale().scale(argb, 10, 10)
      out shouldNotBe null
   }
})
