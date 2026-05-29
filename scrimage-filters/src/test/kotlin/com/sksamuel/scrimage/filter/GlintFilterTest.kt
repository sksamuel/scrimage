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
   test("a stronger glint (lower threshold, higher amount) differs from the default") {
      val default = image.filter(GlintFilter())
      val strong = image.filter(GlintFilter(0.5f, 0.5f, 10, 0.0f))
      strong shouldNotBe default
   }
   test("the no-arg constructor matches the explicit JH Labs defaults") {
      image.filter(GlintFilter()) shouldBe image.filter(GlintFilter(1.0f, 0.1f, 5, 0.0f))
   }
})
