package com.sksamuel.scrimage

import scala.annotation.tailrec

object AutocropOps {

  type PixelsExtractor = (Int, Int, Int, Int) => Array[Pixel]

  @tailrec
  final def scanright(color: Color, height: Int, width: Int, col: Int, f: PixelsExtractor): Int = {
    if (col == width || !PixelTools.uniform(color, f(col, 0, 1, height))) col
    else scanright(color, height, width, col + 1, f)
  }

  @tailrec
  final def scanleft(color: Color, height: Int, width: Int, col: Int, f: PixelsExtractor): Int = {
    if (col == 0 || !PixelTools.uniform(color, f(col, 0, 1, height))) col
    else scanleft(color, height, width, col - 1, f)
  }

  @tailrec
  final def scandown(color: Color, height: Int, width: Int, row: Int, f: PixelsExtractor): Int = {
    if (row == height || !PixelTools.uniform(color, f(0, row, width, 1))) row
    else scandown(color, height, width, row + 1, f)
  }

  @tailrec
  final def scanup(color: Color, height: Int, width: Int, row: Int, f: PixelsExtractor): Int = {
    if (row == 0 || !PixelTools.uniform(color, f(0, row, width, 1))) row
    else scanup(color, height, width, row - 1, f)
  }
}
