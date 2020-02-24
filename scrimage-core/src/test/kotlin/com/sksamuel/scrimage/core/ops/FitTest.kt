@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.color.Colors
import com.sksamuel.scrimage.color.X11Colorlist
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FitTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")
   val small = ImmutableImage.fromResource("/bird_small.png")
   val chip = ImmutableImage.fromResource("/transparent_chip.png")
   val turing = ImmutableImage.fromResource("/com/sksamuel/scrimage/turing.jpg")

   test("when fitting an image the output image should have the specified dimensions") {
      val fitted = image.fit(51, 66)
      51 shouldBe fitted.width
      66 shouldBe fitted.height
   }

   test("when fitting an image the output image should match as expected") {
      val fitted = image.fit(900, 300, java.awt.Color.RED)
      val expected = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_fitted2.png")
      fitted.pixels().size shouldBe fitted.pixels().size
      expected shouldBe fitted
   }

   test("when fitting an image the output image should have specified dimensions") {
      val fitted = image.fit(900, 300, java.awt.Color.RED)
      900 shouldBe fitted.width
      300 shouldBe fitted.height
   }

   test("a fitted image can be aligned to centre") {
      val actual1 = small.fit(200, 100, X11Colorlist.Burlywood2.awt(), Position.Center)
      val actual2 = turing.fit(200, 400, X11Colorlist.Azure.awt(), Position.Center)
      actual1 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_small_aligned_centre.png")
      actual2 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/turing_aligned_centre.png")
   }

   test("a fitted image can be aligned to top left") {
      val actual1 = small.fit(200, 100, X11Colorlist.Burlywood2.awt(), Position.TopLeft)
      val actual2 = turing.fit(200, 400, X11Colorlist.Azure.awt(), Position.TopLeft)
      actual1 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_small_aligned_top_left.png")
      actual2 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/turing_aligned_top_left.png")
   }

   test("a fitted image can be aligned to top right") {
      val actual1 = small.fit(200, 100, X11Colorlist.Burlywood2.awt(), ScaleMethod.Bicubic, Position.TopRight)
      val actual2 = turing.fit(200, 400, X11Colorlist.Azure.awt(), ScaleMethod.Bicubic, Position.TopRight)
      actual1 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_small_aligned_top_right.png")
      actual2 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/turing_aligned_top_right.png")
   }

   test("a fitted image can be aligned to bottom right") {
      val actual1 = small.fit(200, 100, X11Colorlist.Burlywood2.awt(), Position.BottomRight)
      val actual2 = turing.fit(200, 400, X11Colorlist.Azure.awt(), Position.BottomRight)
      actual1 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_small_aligned_bottom_right.png")
      actual2 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/turing_aligned_bottom_right.png")
   }

   test("a fitted image can be aligned to bottom left") {
      val actual1 = small.fit(200, 100, X11Colorlist.Burlywood2.awt(), Position.BottomLeft)
      val actual2 = turing.fit(200, 400, X11Colorlist.Azure.awt(), Position.BottomLeft)
      actual1 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_small_aligned_bottom_left.png")
      actual2 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/turing_aligned_bottom_left.png")
   }

   test("a fitted image can be aligned to centre left") {
      val actual1 = small.fit(200, 100, X11Colorlist.Burlywood2.awt(), Position.CenterLeft)
      val actual2 = turing.fit(200, 400, X11Colorlist.Azure.awt(), Position.CenterLeft)
      actual1 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_small_aligned_centre_left.png")
      actual2 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/turing_aligned_centre_left.png")
   }

   test("a fitted image can be aligned to centre right") {
      val actual1 = small.fit(200, 100, X11Colorlist.Burlywood2.awt(), Position.CenterRight)
      val actual2 = turing.fit(200, 400, X11Colorlist.Azure.awt(), Position.CenterRight)
      actual1 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_small_aligned_centre_right.png")
      actual2 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/turing_aligned_centre_right.png")
   }

   test("a fitted image can be aligned to top cente") {
      val actual1 = small.fit(200, 100, X11Colorlist.Burlywood2.awt(), Position.TopCenter)
      val actual2 = turing.fit(200, 400, X11Colorlist.Azure.awt(), Position.TopCenter)
      actual1 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_small_aligned_top_centre.png")
      actual2 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/turing_aligned_top_centre.png")
   }

   test("a fitted image can be aligned to bottom centre") {
      val actual1 = small.fit(200, 100, X11Colorlist.Burlywood2.awt(), Position.BottomCenter)
      val actual2 = turing.fit(200, 400, X11Colorlist.Azure.awt(), Position.BottomCenter)
      actual1 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_small_aligned_bottom_centre.png")
      actual2 shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/turing_aligned_bottom_centre.png")
   }

   test("fit should keep alpha") {
      chip.fit(200, 300, Colors.Transparent.awt()) shouldBe
         ImmutableImage.fromResource("/com/sksamuel/scrimage/chip_fit.png")
   }
})
