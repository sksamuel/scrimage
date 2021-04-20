package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.OpenGifReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class OpenGifReaderTest : FunSpec() {
   init {
      test("reading all frames of an animated gif") {
         val frames = OpenGifReader().readAll(this::class.java.getResourceAsStream("/animated.gif").readBytes())
         frames.size shouldBe 34
         frames.forAll {
            it.width shouldBe 648
            it.height shouldBe 648
         }
      }
   }
}
