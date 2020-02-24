package com.sksamuel.scrimage.core.ops

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PngWriter
import io.kotest.core.spec.style.FunSpec

class AlphamaskTest : FunSpec({

   val image = ImmutableImage.fromResource("/tiger.jpg")
   val mask = ImmutableImage.fromResource("/gradation.jpg")

   image.cover(512, 256).alphamask(mask, 3).output(PngWriter.MaxCompression, "alphamask.png")


   test("alphamask happy path") {

   }
})
