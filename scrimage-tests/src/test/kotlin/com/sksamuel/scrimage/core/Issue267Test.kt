package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThan
import java.awt.image.BufferedImage

class Issue267Test : FunSpec() {
   init {
      test("png compression for all types") {
         val originalImage = ImmutableImage.loader().fromResource("/issue267.png")
         originalImage.copy(BufferedImage.TYPE_4BYTE_ABGR).bytes(PngWriter.MaxCompression).size shouldBeLessThan 15000
         originalImage.copy(BufferedImage.TYPE_3BYTE_BGR).bytes(PngWriter.MaxCompression).size shouldBeLessThan 15000
         originalImage.copy(BufferedImage.TYPE_INT_ARGB).bytes(PngWriter.MaxCompression).size shouldBeLessThan 15000
         originalImage.copy(BufferedImage.TYPE_INT_BGR).bytes(PngWriter.MaxCompression).size shouldBeLessThan 15000
         originalImage.copy(BufferedImage.TYPE_INT_RGB).bytes(PngWriter.MaxCompression).size shouldBeLessThan 15000
      }
   }
}
