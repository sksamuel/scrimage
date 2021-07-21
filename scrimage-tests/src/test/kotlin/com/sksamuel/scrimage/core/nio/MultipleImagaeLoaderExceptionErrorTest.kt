package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldMatch

class MultipleImagaeLoaderExceptionErrorTest : FunSpec() {
   init {
      test("multiple image load failures should all be reported") {
         val bytes = byteArrayOf(1, 2, 3)
         val e = shouldThrowAny {
            ImmutableImageLoader.create().load(ImageSource.of(bytes))
         }
         e.message shouldMatch """Image parsing failed. Tried the following ImageReader implementations:
com.sksamuel.scrimage.nio.ImageIOReader@.*? failed due to No ImageInputStream supported this image format
com.sksamuel.scrimage.nio.PngReader@.*? failed
com.sksamuel.scrimage.nio.OpenGifReader@.*? failed due to Image is truncated.""".toRegex()
      }
   }
}
