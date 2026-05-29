package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class WeaveFilterTest : FunSpec({
   val image = ImmutableImage.fromResource("/bird.jpg").scaleToWidth(200)
   test("WeaveFilter preserves dimensions") {
      val out = image.filter(WeaveFilter())
      out.width shouldBe image.width
      out.height shouldBe image.height
   }
   test("WeaveFilter produces a non-null result") {
      image.filter(WeaveFilter()).awt() shouldNotBe null
   }
})
