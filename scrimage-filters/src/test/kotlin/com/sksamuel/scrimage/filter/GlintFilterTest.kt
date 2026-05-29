package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class GlintFilterTest : FunSpec({
   val image = ImmutableImage.fromResource("/bird.jpg").scaleToWidth(200)
   test("GlintFilter preserves dimensions") {
      val out = image.filter(GlintFilter())
      out.width shouldBe image.width
      out.height shouldBe image.height
   }
   test("GlintFilter produces a non-null result") {
      image.filter(GlintFilter()).awt() shouldNotBe null
   }
})
