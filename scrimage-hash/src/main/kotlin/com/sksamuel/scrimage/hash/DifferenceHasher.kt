package com.sksamuel.scrimage.hash

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.GrayscaleMethod

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
      return r.rows()
         .flatMap { row ->
            row.toList().windowed(2).map { if (it.first().argb < it.last().argb) 1 else 0 }
         }
   }

}

fun ImmutableImage.dhash(cols: Int = 9, rows: Int = 8) = DifferenceHasher(cols, rows).dhash(this)
