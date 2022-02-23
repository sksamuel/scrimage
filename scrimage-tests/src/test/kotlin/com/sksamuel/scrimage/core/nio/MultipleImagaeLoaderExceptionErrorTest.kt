package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain

class MultipleImagaeLoaderExceptionErrorTest : FunSpec() {
   init {
      test("multiple image load failures should all be reported") {
         val bytes = byteArrayOf(1, 2, 3)
         val e = shouldThrowAny {
            ImmutableImageLoader.create().load(ImageSource.of(bytes))
         }
         e.message shouldContain """Image parsing failed. Tried the following ImageReader implementations"""
         e.message shouldContain """No ImageInputStream supported this image format"""
      }
   }
}
