package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ErodeFilterTest : FunSpec({
   val image = ImmutableImage.fromResource("/bird.jpg").scaleToWidth(200)
   test("ErodeFilter preserves dimensions") {
      val out = image.filter(ErodeFilter(8))
      out.width shouldBe image.width
      out.height shouldBe image.height
   }
   test("ErodeFilter produces a non-null result") {
      image.filter(ErodeFilter(8)).awt() shouldNotBe null
   }
})
