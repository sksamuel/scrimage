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

import com.sksamuel.scrimage.Position.Center
import com.sksamuel.scrimage.ScaleMethod.Bicubic

/**
 * Operations that can be performed in place on an image.
 */
trait InPlaceOperations[R] extends PixelOps[R] {

  /**
   * Sets all pixels on this image to be the given color.
   *
   * @return The result of the pixels set to the given color.
   */
  def fill(color: Color): R

  /**
   * Maps the pixels of this image into another image by applying the given function to each pixel.
   *
   * The function accepts three parameters: x,y,p where x and y are the coordinates of the pixel
   * being transformed and p is the pixel at that location.
   *
   * @param f the function to transform pixel x,y with existing value p into new pixel value p' (p prime)
   * @return
   */
  def map(f: (Int, Int, Pixel) => Pixel): R

  /**
   * Creates a copy of this image with the given filter applied.
   * The original (this) image is unchanged.
   *
   * @param filter the filter to apply. See com.sksamuel.scrimage.Filter.
   *
   * @return A new image with the given filter applied.
   */
  def filter(filter: Filter): R

  /**
   * Flips this image horizontally.
   *
   * @return The result of flipping this image horizontally.
   */
  def flipX: R

  /**
   * Flips this image vertically.
   *
   * @return The result of flipping this image vertically.
   */
  def flipY: R
}



