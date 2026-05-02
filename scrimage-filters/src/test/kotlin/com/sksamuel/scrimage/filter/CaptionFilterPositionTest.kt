package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.FontUtils
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.Position
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class CaptionFilterPositionTest : FunSpec({

   // Build a fully-black source image so the white caption background pixels
   // are easy to detect. Use a generous height so TopLeft and the buggy
   // (image.height - captionHeight) y are clearly different.
   val font = FontUtils.createTrueType(
      this::class.java.getResourceAsStream("/fonts/Roboto-Light.ttf"),
      12,
   )

   fun captionAt(position: Position): ImmutableImage {
      val source = ImmutableImage.filled(400, 400, Color.BLACK)
      return source.filter(
         CaptionFilter(
            "Caption",
            position,
            font,
            Color.WHITE,         // text colour
            1.0,
            false,
            false,
            Color.WHITE,         // caption background — we look for this
            1.0,                 // fully opaque background to make detection clean
            Padding(10, 10, 10, 10),
         )
      )
   }

   fun whiteCount(image: ImmutableImage, yRange: IntRange): Int {
      var count = 0
      for (y in yRange) for (x in 0 until image.width) {
         if (image.pixel(x, y).toARGBInt() == Color.WHITE.rgb) count++
      }
      return count
   }

   test("Position.TopLeft places the caption background near the top, not the bottom") {
      val out = captionAt(Position.TopLeft)

      // The caption background is a large filled rectangle, so wherever it
      // ends up there will be many white pixels. The text alone is small
      // and accounts for at most a few hundred white pixels.
      val topWhite = whiteCount(out, 0 until 60)
      val bottomWhite = whiteCount(out, (out.height - 60) until out.height)

      // With the fix the background lives in the top region, so most of
      // the white pixels should be there. With the previous bug the
      // background was pinned to (image.height - captionHeight), so most
      // of the white pixels would be at the bottom.
      (topWhite > bottomWhite * 4) shouldBe true
   }
})
