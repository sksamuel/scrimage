@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.ImageIOReader
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldNotBe
import javax.imageio.ImageIO
import javax.imageio.spi.ImageReaderSpi
import javax.imageio.stream.ImageInputStream

/**
 * Verifies the multi-reader fallback path in ImageIOReader rewinds the shared
 * ImageInputStream between attempts. A bad reader that consumes the stream and
 * then fails must not prevent a subsequent good reader from decoding the full
 * image data: tryLoad calls iis.seek(0) before each setInput.
 */
class ImageIOReaderFallbackTest : FunSpec({

   val jpegBytes = javaClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")!!.readBytes()

   // A reader that consumes part of the stream (advancing its position) and
   // then throws, simulating a reader that partially reads before failing.
   class ConsumingFailingReader(spi: ImageReaderSpi?) : javax.imageio.ImageReader(spi) {
      override fun setInput(input: Any?, seekForwardOnly: Boolean, ignoreMetadata: Boolean) {
         super.setInput(input, seekForwardOnly, ignoreMetadata)
         // advance the underlying stream so a later reader, if not rewound,
         // would read from a garbage position
         (input as ImageInputStream).skipBytes(64)
      }

      override fun getNumImages(allowSearch: Boolean): Int = 1
      override fun getWidth(imageIndex: Int): Int = throw java.io.IOException("boom")
      override fun getHeight(imageIndex: Int): Int = throw java.io.IOException("boom")
      override fun getImageTypes(imageIndex: Int) = throw java.io.IOException("boom")
      override fun getStreamMetadata() = null
      override fun getImageMetadata(imageIndex: Int) = null
      override fun read(imageIndex: Int, param: javax.imageio.ImageReadParam?) =
         throw java.io.IOException("boom: this reader cannot decode the image")
   }

   test("falls back to a working reader after a previous reader consumed the stream") {
      val realReader = ImageIO.getImageReadersByFormatName("jpeg").next()
      val readers = listOf(ConsumingFailingReader(null), realReader)

      val image = ImageIOReader(readers).read(jpegBytes, null)
      image shouldNotBe null
      image.width shouldBeGreaterThan 0
      image.height shouldBeGreaterThan 0
   }
})
