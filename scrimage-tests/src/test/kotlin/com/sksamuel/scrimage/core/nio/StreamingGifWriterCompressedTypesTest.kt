@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.StreamingGifWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.time.Duration

/**
 * Regression: in compressed mode StreamingGifWriter blanked pixels that matched the previous
 * frame by zeroing raw buffer elements of the frame's own type. That is only correct for
 * TYPE_INT_ARGB:
 * - byte-backed types had single channel bytes zeroed, so an opaque TYPE_4BYTE_ABGR pixel whose
 *   colour changed but whose alpha byte matched became transparent and showed the old colour;
 * - types without alpha (TYPE_3BYTE_BGR, TYPE_INT_RGB) turned unchanged pixels opaque black.
 * The loader returns TYPE_3BYTE_BGR for JPEGs and TYPE_4BYTE_ABGR for PNGs with alpha, and
 * scaleTo returns TYPE_INT_RGB, so this hit ordinary frames.
 */
class StreamingGifWriterCompressedTypesTest : FunSpec({

   val types = listOf(
      "TYPE_INT_ARGB" to BufferedImage.TYPE_INT_ARGB,
      "TYPE_INT_RGB" to BufferedImage.TYPE_INT_RGB,
      "TYPE_3BYTE_BGR" to BufferedImage.TYPE_3BYTE_BGR,
      "TYPE_4BYTE_ABGR" to BufferedImage.TYPE_4BYTE_ABGR,
      "TYPE_USHORT_565_RGB" to BufferedImage.TYPE_USHORT_565_RGB,
   )

   for ((name, type) in types) {
      test("compressed mode reproduces both frames for $name") {
         val frame1 = ImmutableImage.create(8, 8, type)
         val frame2 = ImmutableImage.create(8, 8, type)
         for (y in 0 until 8) for (x in 0 until 8) {
            val color = RGBColor(x * 32, y * 32, 128, 255)
            frame1.setColor(x, y, color)
            frame2.setColor(x, y, color)
         }
         frame2.setColor(3, 3, RGBColor(248, 0, 0, 255))
         // only the green channel differs from frame1 here, so a byte-wise diff zeroes the others
         frame2.setColor(5, 5, RGBColor(160, 0, 128, 255))

         val output = ByteArrayOutputStream()
         val stream = StreamingGifWriter().withCompression(true).withFrameDelay(Duration.ofMillis(100))
            .prepareStream(output, BufferedImage.TYPE_INT_ARGB)
         stream.writeFrame(frame1)
         stream.writeFrame(frame2)
         stream.close()

         val gif = AnimatedGifReader.read(ImageSource.of(output.toByteArray()))
         gif.getFrame(0).argbints().toList() shouldBe frame1.argbints().toList()
         gif.getFrame(1).argbints().toList() shouldBe frame2.argbints().toList()
      }
   }
})
