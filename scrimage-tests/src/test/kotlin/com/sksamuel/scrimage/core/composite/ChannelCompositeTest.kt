package com.sksamuel.scrimage.core.composite

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.composite.BlueComposite
import com.sksamuel.scrimage.composite.GreenComposite
import com.sksamuel.scrimage.composite.RedComposite
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Pins the channel selection of the RED/GREEN/BLUE channel-replacement composites.
 *
 * Each mode must take exactly one channel from the overlay (the blend source) and keep
 * the other two channels from the base image (the blend destination):
 *
 *   RED   = (src.red, dst.green, dst.blue)
 *   GREEN = (dst.red, src.green, dst.blue)
 *   BLUE  = (dst.red, dst.green, src.blue)
 *
 * Regression test for the GREEN/BLUE blenders being swapped in
 * thirdparty.romainguy.BlendComposite (GREEN used the source blue channel and
 * BLUE used the source green channel).
 */
class ChannelCompositeTest : FunSpec({

   // base (blend destination) and overlay (blend source) with distinct values per channel
   val base = ImmutableImage.filled(10, 10, Color(10, 20, 30))
   val overlay = ImmutableImage.filled(10, 10, Color(200, 150, 100))

   test("red composite replaces only the red channel with the overlay's red") {
      val pixel = base.composite(RedComposite(1.0), overlay).pixel(5, 5)
      pixel.red() shouldBe 200
      pixel.green() shouldBe 20
      pixel.blue() shouldBe 30
      pixel.alpha() shouldBe 255
   }

   test("green composite replaces only the green channel with the overlay's green") {
      val pixel = base.composite(GreenComposite(1.0), overlay).pixel(5, 5)
      pixel.red() shouldBe 10
      pixel.green() shouldBe 150
      pixel.blue() shouldBe 30
      pixel.alpha() shouldBe 255
   }

   test("blue composite replaces only the blue channel with the overlay's blue") {
      val pixel = base.composite(BlueComposite(1.0), overlay).pixel(5, 5)
      pixel.red() shouldBe 10
      pixel.green() shouldBe 20
      pixel.blue() shouldBe 100
      pixel.alpha() shouldBe 255
   }

})
