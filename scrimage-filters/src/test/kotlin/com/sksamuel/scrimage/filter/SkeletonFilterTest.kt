package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SkeletonFilterTest : FunSpec({
   val image = ImmutableImage.fromResource("/bird.jpg").scaleToWidth(200)
   test("SkeletonFilter preserves dimensions") {
      val out = image.filter(SkeletonFilter())
      out.width shouldBe image.width
      out.height shouldBe image.height
   }
   test("SkeletonFilter produces a non-null result") {
      image.filter(SkeletonFilter()).awt() shouldNotBe null
   }
})
