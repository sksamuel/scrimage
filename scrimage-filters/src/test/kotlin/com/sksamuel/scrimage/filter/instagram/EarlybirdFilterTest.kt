package com.sksamuel.scrimage.filter.instagram

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class EarlybirdFilterTest : FunSpec({
   val image = ImmutableImage.fromResource("/bird.jpg").scaleToWidth(120)
   test("earlybird preserves the image dimensions and alters the image") {
      val out = image.filter(EarlybirdFilter())
      out.width shouldBe image.width
      out.height shouldBe image.height
      out shouldNotBe image
   }
})
