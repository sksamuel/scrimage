package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.io.IOException

// these tests cannot run on the server as we cannot include webp with our code
class WebpTest : FunSpec() {
   init {

      test("webp reader") {
         val image = ImmutableImage.loader().fromResource("/test.webp")
         image.width shouldBe 2000
         image.height shouldBe 2000
      }

      test("webp writer") {
         ImmutableImage.loader().fromResource("/spacedock.jpg").scale(0.5)
            .bytes(WebpWriter.MAX_LOSSLESS_COMPRESSION) shouldBe
            javaClass.getResourceAsStream("/spacedock.webp").readBytes()
      }

      test("dwebp should capture error on failure") {
         shouldThrow<IOException> {
            ImmutableImage.loader().fromResource("/dist_webp_binaries/dwebp")
         }.message.shouldContain("BITSTREAM_ERROR")
      }
   }
}
