/*
   Copyright 2013 Stephen K Samuel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.sksamuel.scrimage

/** @author Stephen Samuel */
object PixelTools {

  def rgb(r: Int, g: Int, b: Int): Int =
    0xFF << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0

  def argb(a: Int, r: Int, g: Int, b: Int): Int =
    (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0

  /**
   * Returns the alpha component of a pixel as a value between 0 and 255.
   * @return
   */
  def alpha(pixel: Int): Int = pixel >> 24 & 0xFF

  /**
   * Returns the red component of a pixel as a value between 0 and 255.
   * @return
   */
  def red(pixel: Int): Int = pixel >> 16 & 0xFF

  /**
   * Returns the green component of a pixel as a value between 0 and 255.
   * @return
   */
  def green(pixel: Int): Int = pixel >> 8 & 0xFF

  /**
   * Returns the blue component of a pixel as a value between 0 and 255.
   * @return
   */
  def blue(pixel: Int): Int = pixel & 0xFF

  /**
   * Returns the gray value of a pixel as a value between 0 and 255.
   */
  def gray(pixel: Int): Int =
    (red(pixel) + green(pixel) + blue(pixel)) / 3

  def truncate(value: Double): Int = if (value < 0) 0 else if (value > 255) 255 else value.toInt

  /**
   * Returns true if all pixels in the array have the same color
   */
  def uniform(color: Color, pixels: Array[Pixel]): Boolean = pixels.forall(p => p.toInt == color.toInt)

  /**
   * Returns true if the colors of all pixels in the array are within the given tolerance
   * compared to the referenced color
   */
  def approx(color: Color, tolerance: Int, pixels: Array[Pixel]): Boolean = {
    val refColor = color.toRGB

    val minColor = RGBColor(
      red = math.max(refColor.red - tolerance, 0),
      green = math.max(refColor.green - tolerance, 0),
      blue = math.max(refColor.blue - tolerance, 0),
      alpha = refColor.alpha
    )

    val maxColor = RGBColor(
      red = math.min(refColor.red + tolerance, 255),
      green = math.min(refColor.green + tolerance, 255),
      blue = math.min(refColor.blue + tolerance, 255),
      alpha = refColor.alpha
    )

    pixels.forall(p => p.toInt >= minColor.toInt && p.toInt <= maxColor.toInt)
  }

  /**
   * Returns true if the colors of all pixels in the array are within the given tolerance
   * compared to the referenced color
   */
  def colorMatches(color: Color, tolerance: Int, pixels: Array[Pixel]): Boolean = {
    require(tolerance >= 0 && tolerance <= 255, "Tolerance value must be between 0 and 255 inclusive")
    if (tolerance == 0)
      uniform(color, pixels)
    else
      approx(color, tolerance, pixels)
  }

  /**
   * Scales the brightness of a pixel.
   */
  def scale(factor: Double, pixel: Int): Int = rgb(
    (factor * red(pixel)).round.toInt,
    (factor * green(pixel)).round.toInt,
    (factor * blue(pixel)).round.toInt)

  def coordinateToOffset(x: Int, y: Int, w: Int): Int = y * w + x

  def offsetToCoordinate(k: Int, w: Int): (Int, Int) = (k % w, k / w)
}
