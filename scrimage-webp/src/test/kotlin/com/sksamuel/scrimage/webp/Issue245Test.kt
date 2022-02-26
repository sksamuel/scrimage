package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.ImageIOReader
import io.kotest.core.spec.style.FunSpec

class Issue245Test : FunSpec({

   // https://github.com/sksamuel/scrimage/issues/245
   test("Can't load PNG, JPEG Images with scrimage-webp #245") {

      ImmutableImage.loader()
         .fromResource("/issue245.jpg")

      ImmutableImage.loader()
         .withImageReaders(listOf(WebpImageReader(), ImageIOReader()))
         .fromResource("/issue245.jpg")
   }
})
