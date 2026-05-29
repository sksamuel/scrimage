package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class BrushedMetalFilterTest : FunSpec({
   val image = ImmutableImage.fromResource("/bird.jpg").scaleToWidth(200)
   test("BrushedMetalFilter preserves dimensions") {
      val out = image.filter(BrushedMetalFilter())
      out.width shouldBe image.width
      out.height shouldBe image.height
   }
   test("BrushedMetalFilter produces a non-null result") {
      image.filter(BrushedMetalFilter()).awt() shouldNotBe null
   }
})
