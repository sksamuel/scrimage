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
  def alpha(pixel: Int): Int = pixel >> 24 & 0xFF
  def red(pixel: Int): Int = pixel >> 16 & 0xFF
  def green(pixel: Int): Int = pixel >> 8 & 0xFF
  def blue(pixel: Int): Int = pixel & 0xFF
  def gray(pixel: Int): Int =
    (red(pixel) + green(pixel) + blue(pixel)) / 3

  def scale(factor: Double, pixel: Int): Int = rgb(
    (factor * red(pixel)).round.toInt,
    (factor * green(pixel)).round.toInt,
    (factor * blue(pixel)).round.toInt)
}
