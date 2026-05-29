package com.sksamuel.scrimage.filter.instagram

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class InstagramFilterTest : FunSpec({

   val image = ImmutableImage.fromResource("/bird.jpg").scaleToWidth(200)

   test("1977 preserves dimensions and alters the image") {
      val out = image.filter(Filter1977())
      out.width shouldBe image.width
      out.height shouldBe image.height
      out shouldNotBe image
   }

   test("a filter with an overlay (aden) differs from one without (1977)") {
      image.filter(AdenFilter()) shouldNotBe image.filter(Filter1977())
   }

   test("filters are deterministic") {
      image.filter(Filter1977()) shouldBe image.filter(Filter1977())
   }
})
