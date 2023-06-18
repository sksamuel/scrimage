package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.format.Format
import com.sksamuel.scrimage.format.FormatDetector
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class FormatDetectorTest : WordSpec({

   "format detector" should {
      "detect png"  {
         FormatDetector.detect(javaClass.getResourceAsStream("/com/sksamuel/scrimage/chip_pad.png")).get() shouldBe Format.PNG
      }
      "detect jpeg"  {
         FormatDetector.detect(javaClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")).get() shouldBe Format.JPEG
      }
      "detect gif"  {
         FormatDetector.detect(javaClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_compressed.gif")).get() shouldBe Format.GIF
      }
      "detect webp"  {
         FormatDetector.detect(javaClass.getResourceAsStream("/com/sksamuel/scrimage/landscape.webp")).get() shouldBe Format.WEBP
      }
   }

   "image loader" should {
      "output webp warning" {
         shouldThrowAny {
            ImmutableImageLoader.create().fromResource("/spacedock.webp")
         }.message shouldContain "Image parsing failed for WEBP"
      }
   }

})
