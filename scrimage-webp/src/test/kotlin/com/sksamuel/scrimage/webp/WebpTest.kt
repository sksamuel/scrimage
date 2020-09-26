package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.IOException

class WebpTest : FunSpec() {
   init {

      test("webp conversion") {
         val image = ImmutableImage.loader().fromResource("/test.webp")
         image.width shouldBe 2000
         image.height shouldBe 2000
      }

      test("webp should capture error on failure") {
         shouldThrow<IOException> {
            ImmutableImage.loader().fromResource("/webp_binaries/dwebp")
         }.message.shouldContain("BITSTREAM_ERROR")
      }
   }
}
