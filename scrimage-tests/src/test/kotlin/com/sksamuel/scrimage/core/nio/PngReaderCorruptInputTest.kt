@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.PngReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO

/**
 * Regression: PngReader (the fallback reader, also usable directly) let pngj's unchecked
 * PngjInputException escape for truncated PNGs, although ImageReader declares IOException for
 * unreadable input.
 */
class PngReaderCorruptInputTest : FunSpec({

   val png: ByteArray = run {
      val bi = BufferedImage(16, 8, BufferedImage.TYPE_INT_RGB)
      for (y in 0 until 8) for (x in 0 until 16) bi.setRGB(x, y, (x * 16 shl 16) or (y * 32 shl 8) or 0x40)
      val baos = ByteArrayOutputStream()
      ImageIO.write(bi, "png", baos)
      baos.toByteArray()
   }

   test("the complete png reads") {
      PngReader().read(png, null)!!.width shouldBe 16
   }

   for (cut in listOf(1, 12, 20)) {
      test("a png missing its last $cut bytes throws IOException") {
         shouldThrow<IOException> { PngReader().read(png.copyOf(png.size - cut), null) }
      }
   }

   test("a png cut inside its header throws IOException") {
      shouldThrow<IOException> { PngReader().read(png.copyOf(20), null) }
   }
})
