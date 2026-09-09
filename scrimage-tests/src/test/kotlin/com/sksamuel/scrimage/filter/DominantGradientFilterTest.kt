package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.transform.DominantGradient
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class DominantGradientFilterTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg").scaleToWidth(200)

   test("leaves a blank image with no dominant colours unchanged") {
      // A blank image quantizes to an empty palette; indexing dominant[0]/[1] used to
      // throw ArrayIndexOutOfBoundsException.
      val white = ImmutableImage.filled(20, 20, Color.WHITE)
      val out = white.filter(DominantGradientFilter())
      out shouldBe white
   }

   test("preserves the image dimensions") {
      val out = image.filter(DominantGradientFilter())
      out.width shouldBe image.width
      out.height shouldBe image.height
   }

   test("produces the same output as the deprecated DominantGradient transform") {
      @Suppress("DEPRECATION")
      val viaTransform = image.transform(DominantGradient())
      image.filter(DominantGradientFilter()) shouldBe viaTransform
   }
})
