package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

/**
 * Tests for the ImmutableImage comparison methods: diff and rmse.
 */
class ImageCompareTest : FunSpec({

   fun image1x1(r: Int, g: Int, b: Int, a: Int = 255) =
      ImmutableImage.create(1, 1, arrayOf(Pixel(0, 0, r, g, b, a)))

   test("diff produces absolute per-channel difference, fully opaque") {
      val a = image1x1(200, 100, 50, 255)
      val b = image1x1(50, 100, 200, 255)
      val p = a.diff(b).pixel(0, 0)
      p.red() shouldBe 150
      p.green() shouldBe 0
      p.blue() shouldBe 150
      p.alpha() shouldBe 255
   }

   test("diff of identical images is black") {
      val a = image1x1(123, 45, 67, 255)
      val p = a.diff(a).pixel(0, 0)
      p.red() shouldBe 0
      p.green() shouldBe 0
      p.blue() shouldBe 0
   }

   test("rmse is 0.0 for identical images") {
      val a = image1x1(123, 45, 67, 255)
      a.rmse(a) shouldBe 0.0
   }

   test("rmse of pure red vs black is sqrt(255^2/3)/255") {
      // dr=255, dg=0, db=0 -> mse = 65025/3 = 21675 -> sqrt/255 = 0.5773..
      image1x1(255, 0, 0, 255).rmse(image1x1(0, 0, 0, 255)) shouldBe (0.57735 plusOrMinus 0.0001)
   }

   test("diff and rmse reject mismatched dimensions") {
      val a = ImmutableImage.create(2, 2)
      val b = ImmutableImage.create(3, 3)
      shouldThrow<IllegalArgumentException> { a.diff(b) }
      shouldThrow<IllegalArgumentException> { a.rmse(b) }
   }
})
