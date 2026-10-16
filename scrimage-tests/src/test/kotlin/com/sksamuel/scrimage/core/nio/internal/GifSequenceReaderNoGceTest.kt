package com.sksamuel.scrimage.core.nio.internal

import com.sksamuel.scrimage.DisposeMethod
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.GifSequenceWriter
import com.sksamuel.scrimage.nio.ImageSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

/**
 * Regression for [com.sksamuel.scrimage.nio.internal.GifSequenceReader] compositing the frame
 * that FOLLOWS a frame without a Graphic Control Extension onto a blank canvas.
 *
 * The GCE is optional. When one is present with disposal 0 ("unspecified") the reader maps it
 * to 1 ("do not dispose", keep the previous canvas). When the GCE is omitted entirely the
 * frame's disposal stayed 0, so for the next frame `lastDispose == 0` and setPixels skipped the
 * "copy previous canvas" step. Pillow and ImageMagick `-coalesce` both show the previous frame
 * underneath, and the reader's own GCE path already agrees with them.
 */
class GifSequenceReaderNoGceTest : FunSpec({

   /** Strips every Graphic Control Extension (21 F9 04 .. 00) from a GIF by walking its blocks. */
   fun stripGces(b: ByteArray): ByteArray {
      val out = ByteArrayOutputStream()
      var p = 6
      val packed = b[p + 4].toInt() and 0xff
      p += 7
      if (packed and 0x80 != 0) p += 3 * (2 shl (packed and 7))
      out.write(b, 0, p)
      while (p < b.size) {
         when (b[p].toInt() and 0xff) {
            0x3B -> {
               out.write(b, p, b.size - p)
               return out.toByteArray()
            }
            0x21 -> {
               val label = b[p + 1].toInt() and 0xff
               var q = p + 2
               while (true) {
                  val size = b[q].toInt() and 0xff
                  q += 1 + size
                  if (size == 0) break
               }
               if (label != 0xF9) out.write(b, p, q - p)
               p = q
            }
            0x2C -> {
               var q = p + 10
               val ipacked = b[p + 9].toInt() and 0xff
               if (ipacked and 0x80 != 0) q += 3 * (2 shl (ipacked and 7))
               q += 1 // lzw minimum code size
               while (true) {
                  val size = b[q].toInt() and 0xff
                  q += 1 + size
                  if (size == 0) break
               }
               out.write(b, p, q - p)
               p = q
            }
            else -> error("unexpected block ${b[p]} at $p")
         }
      }
      return out.toByteArray()
   }

   fun twoFrameGifWithoutGces(): ByteArray {
      val frame0 = ImmutableImage.filled(3, 3, Color.RED, BufferedImage.TYPE_INT_RGB)
      val frame1 = ImmutableImage.filled(1, 1, Color.BLUE, BufferedImage.TYPE_INT_RGB)
      val bytes = GifSequenceWriter(100, true).bytes(arrayOf(frame0, frame1))
      val stripped = stripGces(bytes)
      // sanity: two 8-byte GCE blocks were removed
      stripped.size shouldBe bytes.size - 16
      return stripped
   }

   test("a frame following a frame without a graphic control extension keeps the previous canvas") {
      val gif = AnimatedGifReader.read(ImageSource.of(twoFrameGifWithoutGces()))
      gif.frameCount shouldBe 2
      val frame1 = gif.getFrame(1)
      frame1.pixel(0, 0).toARGBInt() shouldBe Color.BLUE.rgb
      // outside the 1x1 sub-frame the previous (red) frame must still be visible
      frame1.pixel(2, 2).toARGBInt() shouldBe Color.RED.rgb
      frame1.pixel(1, 0).toARGBInt() shouldBe Color.RED.rgb
   }

   test("a frame without a graphic control extension reports the same disposal as one with disposal 0") {
      val gif = AnimatedGifReader.read(ImageSource.of(twoFrameGifWithoutGces()))
      gif.getDisposeMethod(0) shouldBe DisposeMethod.DO_NOT_DISPOSE
      gif.getDisposeMethod(1) shouldBe DisposeMethod.DO_NOT_DISPOSE
   }
})
