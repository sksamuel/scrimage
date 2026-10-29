@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.DisposeMethod
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.StreamingGifWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel
import java.io.ByteArrayOutputStream
import java.time.Duration

/**
 * Regression: prepareStream(TYPE_BYTE_INDEXED) built the shared frame metadata from the JDK's
 * default TYPE_BYTE_INDEXED type, whose metadata carries the default 6x6x6 palette as a
 * LocalColorTable. That table was written for every frame, so every frame's palette indices
 * were decoded against the wrong colours (even the first frame).
 */
class StreamingGifWriterPaletteTest : FunSpec({

   fun paletteFrame(vararg colours: Int): ImmutableImage {
      val r = ByteArray(4) { (colours[it] shr 16).toByte() }
      val g = ByteArray(4) { (colours[it] shr 8).toByte() }
      val b = ByteArray(4) { colours[it].toByte() }
      val bi = BufferedImage(8, 8, BufferedImage.TYPE_BYTE_INDEXED, IndexColorModel(8, 4, r, g, b))
      for (y in 0 until 8) for (x in 0 until 8) bi.raster.setSample(x, y, 0, (x + y) % 4)
      return ImmutableImage.wrapAwt(bi)
   }

   val frames = listOf(
      paletteFrame(0x7b2d43, 0x0ac81e, 0xc864fa, 0x4d4d4d),
      paletteFrame(0x112233, 0xffee00, 0x00ffee, 0x808000),
   )

   for ((name, type) in listOf(
      "TYPE_BYTE_INDEXED" to BufferedImage.TYPE_BYTE_INDEXED,
      "TYPE_INT_ARGB" to BufferedImage.TYPE_INT_ARGB,
   )) {
      test("palette frames keep their own colours with prepareStream($name)") {
         val output = ByteArrayOutputStream()
         val stream = StreamingGifWriter().withFrameDelay(Duration.ofMillis(100)).prepareStream(output, type)
         stream.writeFrame(frames[0])
         // the override overload rewrites the shared metadata; the palette must still be the frame's own
         stream.writeFrame(frames[1], Duration.ofMillis(200), DisposeMethod.RESTORE_TO_BACKGROUND_COLOR)
         stream.close()

         val gif = AnimatedGifReader.read(ImageSource.of(output.toByteArray()))
         gif.frameCount shouldBe 2
         gif.getFrame(0).argbints().toList() shouldBe frames[0].argbints().toList()
         gif.getFrame(1).argbints().toList() shouldBe frames[1].argbints().toList()
         gif.getDelay(1) shouldBe Duration.ofMillis(200)
      }
   }
})
