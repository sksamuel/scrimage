package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

// cwebp documents -z as "a shortcut for predefined quality/method settings; if -q or -m
// are also used they invalidate the effect of this option". Without an explicit -lossless
// flag, WebpWriter.withZ(...) combined with withQ/withM therefore silently produced a
// plain lossy file. The writer must always emit -lossless when z is set.
class WebpWriterZLosslessTest : FunSpec({

   // a noisy image with alpha so lossy and lossless output are distinguishable
   val img = ImmutableImage.loader().fromResource("/alpha.png")

   fun roundTrip(writer: WebpWriter): ImmutableImage = ImmutableImage.loader().fromBytes(img.bytes(writer))

   test("MAX_LOSSLESS_COMPRESSION is lossless") {
      roundTrip(WebpWriter.MAX_LOSSLESS_COMPRESSION).argbints() shouldBe img.argbints()
   }

   test("MAX_LOSSLESS_COMPRESSION.withQ(80) is still lossless") {
      roundTrip(WebpWriter.MAX_LOSSLESS_COMPRESSION.withQ(80)).argbints() shouldBe img.argbints()
   }

   test("MAX_LOSSLESS_COMPRESSION.withM(6) is still lossless") {
      roundTrip(WebpWriter.MAX_LOSSLESS_COMPRESSION.withM(6)).argbints() shouldBe img.argbints()
   }

   test("withZ on a writer with q and m set is lossless") {
      roundTrip(WebpWriter.DEFAULT.withQ(50).withM(4).withZ(3)).argbints() shouldBe img.argbints()
   }
})
