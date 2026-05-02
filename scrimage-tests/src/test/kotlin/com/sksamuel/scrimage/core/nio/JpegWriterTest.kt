@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.metadata.Tag
import com.sksamuel.scrimage.nio.JpegWriter
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.shouldBe

class JpegWriterTest : FunSpec({

   val original = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird.jpg").scaleTo(600, 400)

   test("jpeg compression happy path") {
      repeat(10) { k ->
         original.bytes(JpegWriter().withCompression(k * 10)) // make sure no exceptions for each format level
      }
   }

   test("jpeg writing with alpha #84") {
      val img = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/nio/issue84.jpg")
      val w = JpegWriter()
      img.bytes(w) // was throwing with bug
   }

   test("JpegWriter write does not leak resources on repeated writes") {
      repeat(100) {
         original.bytes(JpegWriter.Default)
      }
   }

   test("!jpeg writer should write metadata") {
      val img = ImmutableImage.loader().fromResource("/vossen.jpg")
      val file = img.output(JpegWriter.Default, "metadatatest.jpg")
      val tags = ImmutableImage.loader().fromPath(file).metadata.tags().toSet()
      tags.shouldContainAll(
         Tag("a", -3, "0", "foo"),
         Tag("b", 0, "8", "boo")
      )
   }

   // The single-Graphics2D-pass alpha removal must produce the same blend
   // result as the previous two-pass replaceTransparency + toNewBufferedImage:
   // alpha-blend the source over a white background. We use a 16x16 image
   // because JPEG's 8x8 DCT block quantization produces large artifacts on
   // tiny images; sampling away from the edge gives much closer values.
   test("alpha is composited over white when writing JPEG") {
      // 16x16 image: top half fully transparent, bottom half semi-transparent
      // blue (alpha=128). Both halves should converge to white / blue-over-white
      // after compositing.
      val w = 16; val h = 16
      val pixels = Array(w * h) { i ->
         val x = i % w; val y = i / w
         if (y < h / 2) Pixel(x, y, 100, 50, 25, 0)        // fully transparent
         else Pixel(x, y, 0, 0, 255, 128)                  // semi blue
      }
      val image = ImmutableImage.create(w, h, pixels)
      val bytes = image.bytes(JpegWriter().withCompression(95))
      val decoded = ImmutableImage.loader().fromBytes(bytes)

      decoded.width shouldBe w
      decoded.height shouldBe h

      // Sample from the centre of each half, away from block edges.
      val transparent = decoded.pixel(w / 2, 1)
      // alpha=0 over white → white
      (255 - transparent.red()) shouldBeLessThanOrEqual 8
      (255 - transparent.green()) shouldBeLessThanOrEqual 8
      (255 - transparent.blue()) shouldBeLessThanOrEqual 8

      // Semi-transparent blue alpha-composited over white:
      // r = 255*(1-128/255) + 0*(128/255) ≈ 127
      // g ≈ 127, b ≈ 255
      val semi = decoded.pixel(w / 2, h - 2)
      Math.abs(semi.red() - 127) shouldBeLessThanOrEqual 12
      Math.abs(semi.green() - 127) shouldBeLessThanOrEqual 12
      Math.abs(semi.blue() - 255) shouldBeLessThanOrEqual 12
   }
})
