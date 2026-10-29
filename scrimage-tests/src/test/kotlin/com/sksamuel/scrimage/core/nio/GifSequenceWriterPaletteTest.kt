@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.GifSequenceWriter
import com.sksamuel.scrimage.nio.ImageSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel

/**
 * Regression: GifSequenceWriter built one image metadata object from the first frame's type and
 * used it for every frame. When the first frame was palette based (TYPE_BYTE_INDEXED, which the
 * loader returns for GIFs and PNG-8s, each with its own palette) that metadata carried the first
 * frame's LocalColorTable, which the JDK writer emitted for every later frame, so later frames
 * were drawn with the first frame's colours.
 */
class GifSequenceWriterPaletteTest : FunSpec({

   fun paletteFrame(vararg colours: Int): ImmutableImage {
      val r = ByteArray(4) { (colours[it] shr 16).toByte() }
      val g = ByteArray(4) { (colours[it] shr 8).toByte() }
      val b = ByteArray(4) { colours[it].toByte() }
      val bi = BufferedImage(8, 8, BufferedImage.TYPE_BYTE_INDEXED, IndexColorModel(8, 4, r, g, b))
      for (y in 0 until 8) for (x in 0 until 8) bi.raster.setSample(x, y, 0, (x + y) % 4)
      return ImmutableImage.wrapAwt(bi)
   }

   fun rgbFrame(): ImmutableImage {
      val bi = BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB)
      for (y in 0 until 8) for (x in 0 until 8) bi.setRGB(x, y, (x * 30 shl 16) or (y * 30 shl 8) or 0x40)
      return ImmutableImage.wrapAwt(bi)
   }

   fun roundTrip(frames: Array<ImmutableImage>) {
      val gif = AnimatedGifReader.read(ImageSource.of(GifSequenceWriter(100, false).bytes(frames)))
      gif.frameCount shouldBe frames.size
      for (i in frames.indices) {
         gif.getFrame(i).argbints().toList() shouldBe frames[i].argbints().toList()
      }
   }

   val first = { paletteFrame(0x7b2d43, 0x0ac81e, 0xc864fa, 0x4d4d4d) }
   val second = { paletteFrame(0x112233, 0xffee00, 0x00ffee, 0x808000) }

   test("palette frames with different palettes keep their own colours") {
      roundTrip(arrayOf(first(), second()))
   }

   test("an rgb frame after a palette frame keeps its own colours") {
      roundTrip(arrayOf(first(), rgbFrame()))
   }

   test("a palette frame after an rgb frame keeps its own colours") {
      roundTrip(arrayOf(rgbFrame(), first()))
   }
})
