package com.sksamuel.scrimage.core.nio.internal

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.GifWriter
import com.sksamuel.scrimage.nio.ImageSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Regression for [com.sksamuel.scrimage.nio.internal.GifSequenceReader.setPixels] mis-placing
 * the rows of an interlaced frame whose height is 2, 3 or 4.
 *
 * GIF interlacing stores rows in four passes (rows 0,8,..; 4,12,..; 2,6,..; 1,3,..). For a frame
 * shorter than 5 rows at least one pass is empty, but the de-interlacer only advanced a single
 * pass per source row, so it still consumed a source row for the empty pass and wrote it to a
 * destination line outside the frame. The row was dropped and every later row landed one slot
 * too late, leaving a transparent gap. Heights 1 and >= 5 were unaffected.
 *
 * The frames below are produced by our own [GifWriter.Progressive]; the expected rows are
 * hardcoded rather than compared against ImageIO, whose GIF reader has the same bug.
 */
class GifSequenceReaderShortInterlaceTest : FunSpec({

   fun rowsOf(height: Int): List<Pair<Int, Int>> {
      val src = ImmutableImage.create(2, height, BufferedImage.TYPE_INT_RGB)
      for (y in 0 until height) for (x in 0 until 2) src.awt().setRGB(x, y, Color(y * 40, 0, 0).rgb)
      val gif = AnimatedGifReader.read(ImageSource.of(src.bytes(GifWriter.Progressive)))
      gif.frameCount shouldBe 1
      val frame = gif.getFrame(0)
      return (0 until height).map { frame.pixel(0, it).red() to frame.pixel(0, it).alpha() }
   }

   for (height in 1..6) {
      test("interlaced GIF frame of height $height decodes every row in order") {
         rowsOf(height) shouldBe (0 until height).map { it * 40 to 255 }
      }
   }

   test("a 2x3 image written with GifWriter.Progressive round-trips through AnimatedGifReader") {
      val src = ImmutableImage.create(2, 3, BufferedImage.TYPE_INT_RGB)
      for (x in 0 until 2) {
         src.awt().setRGB(x, 0, Color.RED.rgb)
         src.awt().setRGB(x, 1, Color.GREEN.rgb)
         src.awt().setRGB(x, 2, Color.BLUE.rgb)
      }
      val frame = AnimatedGifReader.read(ImageSource.of(src.bytes(GifWriter.Progressive))).getFrame(0)
      frame.pixel(0, 0).toARGBInt() shouldBe Color.RED.rgb
      frame.pixel(0, 1).toARGBInt() shouldBe Color.GREEN.rgb
      frame.pixel(0, 2).toARGBInt() shouldBe Color.BLUE.rgb
   }
})
