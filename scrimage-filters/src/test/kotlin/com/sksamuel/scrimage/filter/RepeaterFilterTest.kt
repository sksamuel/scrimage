package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class RepeaterFilterTest : FunSpec({

   test("RepeaterFilter preserves the image dimensions") {
      val image = ImmutableImage.create(120, 90)
      val out = image.filter(RepeaterFilter(2, 3))
      out.width shouldBe 120
      out.height shouldBe 90
   }

   test("RepeaterFilter(1, 1) leaves the image unchanged") {
      val image = ImmutableImage.create(40, 40).fill(java.awt.Color(10, 20, 30))
      val out = image.filter(RepeaterFilter(1, 1))
      out shouldBe image
   }

   test("RepeaterFilter rejects non-positive grid dimensions") {
      shouldThrow<IllegalArgumentException> { RepeaterFilter(0, 2) }
      shouldThrow<IllegalArgumentException> { RepeaterFilter(2, 0) }
   }
})
