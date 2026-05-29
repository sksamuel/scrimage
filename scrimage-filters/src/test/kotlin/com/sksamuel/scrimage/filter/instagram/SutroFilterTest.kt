package com.sksamuel.scrimage.filter.instagram

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SutroFilterTest : FunSpec({
   val image = ImmutableImage.fromResource("/bird.jpg").scaleToWidth(120)
   test("sutro preserves the image dimensions and alters the image") {
      val out = image.filter(SutroFilter())
      out.width shouldBe image.width
      out.height shouldBe image.height
      out shouldNotBe image
   }
})
