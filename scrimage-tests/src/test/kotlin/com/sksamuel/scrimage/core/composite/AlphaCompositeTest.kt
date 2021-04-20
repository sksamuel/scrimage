package com.sksamuel.scrimage.core.composite

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.composite.AlphaComposite
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AlphaCompositeTest : FunSpec({

   val source = ImmutableImage.fromResource("/colosseum.jpg").resizeTo(400, 300)
   val transparent = ImmutableImage.fromResource("/transparent_chip.png")
   val expected1 = ImmutableImage.fromResource("/composite/alpha_composite.png")
   val expected2 = ImmutableImage.fromResource("/composite/alpha_composite_0.5f.png")

   test("alpha composite uses transparency of application image") {
      val actual = source.composite(AlphaComposite(1.0), transparent)
      expected1 shouldBe actual
   }

   test("alpha composite uses transparency of application image combined with alpha") {
      val actual = source.composite(AlphaComposite(0.5), transparent)
      expected2 shouldBe actual
   }

})
