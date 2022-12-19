package com.sksamuel.scrimage.hash

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.GrayscaleMethod
import com.sksamuel.scrimage.nio.PngWriter

class DifferenceHasher(private val cols: Int, private val rows: Int) {

   /**
    * Returns the dhash for this input [image] based on the configured [cols] and [rows].
    */
   fun dhash(image: ImmutableImage): List<Int> {
      val gs = image.toGrayscale(GrayscaleMethod.WEIGHTED)
      val r = gs.resizeTo(cols, rows)
      r.output(PngWriter.MaxCompression, "r${System.currentTimeMillis()}.png")
      return r.rows()
         .flatMap { row ->
            row.toList().windowed(2).map { if (it.first().argb < it.last().argb) 1 else 0 }
         }
   }

}

fun ImmutableImage.dhash(cols: Int = 9, rows: Int = 8) = DifferenceHasher(cols, rows).dhash(this)
