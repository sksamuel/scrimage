package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AnimatedGifTest : FunSpec() {
   init {
      test("animated gif reader should parse gifs") {
         val source = ImageSource.of(javaClass.getResourceAsStream("/animatedgif1.gif"))
         val gif = AnimatedGifReader.read(source)
         gif.frameCount shouldBe 24
         gif.getDelay(0).toMillis() shouldBe 40L
         gif.getDelay(1).toMillis() shouldBe 40L
         gif.frames.size shouldBe 24
         gif.dimensions.width shouldBe 1400
         gif.dimensions.height shouldBe 1050
      }
   }
}
