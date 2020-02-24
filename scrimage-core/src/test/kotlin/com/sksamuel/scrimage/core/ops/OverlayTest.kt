@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.X11Colorlist
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OverlayTest : FunSpec({

   test("overlay should retain source background") {
      val image1 = ImmutableImage.filled(100, 100, X11Colorlist.PaleVioletRed1.awt())
      val image2 = ImmutableImage.filled(75, 75, X11Colorlist.GreenYellow.awt())
      val result = image1.overlay(image2, 10, 10)
      result.color(0, 0) shouldBe X11Colorlist.PaleVioletRed1
      result.color(10, 10) shouldBe X11Colorlist.GreenYellow
      result.color(84, 84) shouldBe X11Colorlist.GreenYellow
      result.color(85, 85) shouldBe X11Colorlist.PaleVioletRed1
      result.color(99, 99) shouldBe X11Colorlist.PaleVioletRed1
   }
})
