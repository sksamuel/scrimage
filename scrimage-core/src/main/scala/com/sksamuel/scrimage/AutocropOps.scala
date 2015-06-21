package com.sksamuel.scrimage

object AutocropOps {

  type PixelsExtractor = (Int, Int, Int, Int) => Array[Pixel]

  def scanright(color: Color, height: Int, col: Int, f: PixelsExtractor): Int = {
    if (PixelTools.uniform(color, f(col, 0, 1, height))) scanright(color, height, col + 1, f)
    else col
  }

  def scanleft(color: Color, height: Int, col: Int, f: PixelsExtractor): Int = {
    if (PixelTools.uniform(color, f(col, 0, 1, height))) scanleft(color, height, col - 1, f)
    else col
  }

  def scandown(color: Color, width: Int, row: Int, f: PixelsExtractor): Int = {
    if (PixelTools.uniform(color, f(0, row, width, 1))) scandown(color, width, row + 1, f)
    else row
  }

  def scanup(color: Color, width: Int, row: Int, f: PixelsExtractor): Int = {
    if (PixelTools.uniform(color, f(0, row, width, 1))) scanup(color, width, row - 1, f)
    else row
  }
}
