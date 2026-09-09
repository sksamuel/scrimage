package com.sksamuel.scrimage.hash

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.GrayscaleMethod
import com.sksamuel.scrimage.pixels.PixelTools

class DifferenceHasher(private val cols: Int, private val rows: Int) {

   /**
    * Returns the dhash for this input [image] based on the configured [cols] and [rows].
    */
   fun dhash(image: ImmutableImage): List<Int> {
      val gs = image.toGrayscale(GrayscaleMethod.LUMA)
      // scaleTo resamples the image down to (cols x rows). The previous
      // code used resizeTo, which is a canvas-resize op — for any image
      // larger than (cols x rows), it cropped to a small region around
      // the centre instead of summarising the whole image. That defeated
      // the entire point of the perceptual hash.
      val r = gs.scaleTo(cols, rows)
      // Compare luminance via the unsigned-byte red channel of the
      // already-grayscale image. The previous code compared `argb`
      // packed ints directly — opaque grayscale pixels have a high
      // byte of 0xFF, making the int negative, so on inputs with
      // varying source alpha the dhash sorted by alpha rather than
      // brightness and produced a perceptually meaningless hash.
      // Fetch the packed ARGB ints once as a flat row-major array and compare
      // adjacent pixels with plain index arithmetic. This avoids materialising
      // a Pixel object per pixel (rows()) plus the per-row List/windowed
      // intermediate allocations of the previous implementation, while
      // producing exactly the same bits.
      val argb = r.argbints()
      val bits = ArrayList<Int>((cols - 1) * rows)
      for (y in 0 until rows) {
         val offset = y * cols
         for (x in 0 until cols - 1) {
            val left = PixelTools.red(argb[offset + x])
            val right = PixelTools.red(argb[offset + x + 1])
            bits.add(if (left < right) 1 else 0)
         }
      }
      return bits
   }

}

fun ImmutableImage.dhash(cols: Int = 9, rows: Int = 8) = DifferenceHasher(cols, rows).dhash(this)
