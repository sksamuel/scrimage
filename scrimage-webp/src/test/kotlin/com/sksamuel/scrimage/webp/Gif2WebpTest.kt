package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File

class Gif2WebpTest : FunSpec() {

   init {

      test("gif2webp writer") {
         AnimatedGifReader.read(ImageSource.of(File("src/test/resources/animated.gif")))
            .bytes(Gif2WebpWriter.DEFAULT) shouldBe
            javaClass.getResourceAsStream("/animated.webp").readBytes()
      }
   }
}
