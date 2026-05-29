package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SkyFilterTest : FunSpec({
   val image = ImmutableImage.fromResource("/bird.jpg").scaleToWidth(200)
   test("SkyFilter preserves dimensions") {
      val out = image.filter(SkyFilter())
      out.width shouldBe image.width
      out.height shouldBe image.height
   }
   test("SkyFilter produces a non-null result") {
      image.filter(SkyFilter()).awt() shouldNotBe null
   }
})
