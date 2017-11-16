package com.sksamuel.scrimage

import scala.annotation.tailrec

object AutocropOps {

  type PixelsExtractor = (Int, Int, Int, Int) => Array[Pixel]

  @tailrec
  final def scanright(color: Color, height: Int, width: Int, col: Int, f: PixelsExtractor, tolerance: Int = 0): Int = {
    if (col == width || !PixelTools.colorMatches(color, tolerance, f(col, 0, 1, height))) col
    else scanright(color, height, width, col + 1, f, tolerance)
  }

  @tailrec
  final def scanleft(color: Color, height: Int, width: Int, col: Int, f: PixelsExtractor, tolerance: Int = 0): Int = {
    if (col == 0 || !PixelTools.colorMatches(color, tolerance, f(col, 0, 1, height))) col
    else scanleft(color, height, width, col - 1, f, tolerance)
  }

  @tailrec
  final def scandown(color: Color, height: Int, width: Int, row: Int, f: PixelsExtractor, tolerance: Int = 0): Int = {
    if (row == height || !PixelTools.colorMatches(color, tolerance, f(0, row, width, 1))) row
    else scandown(color, height, width, row + 1, f, tolerance)
  }

  @tailrec
  final def scanup(color: Color, height: Int, width: Int, row: Int, f: PixelsExtractor, tolerance: Int = 0): Int = {
    if (row == 0 || !PixelTools.colorMatches(color, tolerance, f(0, row, width, 1))) row
    else scanup(color, height, width, row - 1, f, tolerance)
  }
}
