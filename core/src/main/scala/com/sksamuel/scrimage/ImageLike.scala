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

import java.io.{OutputStream, File}
import org.apache.commons.io.FileUtils
import com.sksamuel.scrimage.Format.PNG
import com.sksamuel.scrimage.io.ImageWriter
import com.sksamuel.scrimage.ScaleMethod.Bicubic
import com.sksamuel.scrimage.Position.Center
import java.awt.Color

/** @author Stephen Samuel */
trait ImageLike[R] {

  def width: Int
  def height: Int
  def dimensions: (Int, Int) = (width, height)
  lazy val points: Seq[(Int, Int)] = for ( x <- 0 until width; y <- 0 until height ) yield (x, y)

  /**
   * Creates an empty Image with the same dimensions of this image.
   *
   * @return a new Image that is a clone of this image but with uninitialized data
   */
  def empty: Image

  /**
   * Counts the number of pixels with the given colour.
   *
   * @param pixel the colour to detect.
   * @return the number of pixels that matched the colour of the given pixel
   */
  def count(pixel: Int) = pixels.find(_ == pixel).size

  /**
   * Creates a new image with the same data as this image.
   * Any operations to the copied image will not write back to the original.
   * Images can be copied multiple times as well as copies copied etc.
   *
   * @return A copy of this image.
   */
  def copy: Image

  /**
   * @return Returns the ratio for this image.
   */
  def ratio: Double = if (height == 0) 0 else width / height.toDouble

  /**
   * Maps the pixels of this image into another image by applying the given function to each point.
   *
   * The function accepts three parameters: x,y,p where x and y are the coordinates of the pixel
   * being transformed and p is the current pixel value in ABGR format.
   *
   * @param f the function to transform pixel x,y with existing value p into new pixel value p' (p prime)
   * @return
   */
  def map(f: (Int, Int, Int) => Int): R

  def foreach(f: (Int, Int, Int) => Unit)

  /**
   * Creates a copy of this image with the given filter applied.
   * The original (this) image is unchanged.
   *
   * @param filter the filter to apply. See com.sksamuel.scrimage.Filter.
   *
   * @return A new image with the given filter applied.
   */
  def filter(filter: Filter): R

  def fit(targetWidth: Int, targetHeight: Int, color: java.awt.Color, scaleMethod: ScaleMethod, position: Position): R

  def fitToHeight(targetHeight: Int, color: java.awt.Color = java.awt.Color.WHITE,
                  scaleMethod: ScaleMethod = Bicubic, position: Position = Center): R =
    fit((targetHeight / height.toDouble * height).toInt, targetHeight, color, scaleMethod, position)

  def fitToWidth(targetWidth: Int, color: java.awt.Color = java.awt.Color.WHITE,
                 scaleMethod: ScaleMethod = Bicubic, position: Position = Center): R =
    fit(targetWidth, (targetWidth / width.toDouble * height).toInt, color, scaleMethod, position)

  def resizeTo(targetWidth: Int, targetHeight: Int, position: Position, background: Color = Color.WHITE): R

  /**
   *
   * Resize will resize the canvas, it will not scale the image.
   * This is like a "canvas resize" in Photoshop.
   *
   * @param scaleFactor the scaleFactor. 1 retains original size. 0.5 is half. 2 double. etc
   * @param position where to position the original image after the canvas size change. Defaults to centre.
   * @param background the color to use for expande background areas. Defaults to White.
   *
   * @return a new Image that is the result of resizing the canvas.
   */
  def resize(scaleFactor: Double, position: Position = Center, background: Color = Color.WHITE): R =
    resizeTo((width * scaleFactor).toInt, (height * scaleFactor).toInt, position, background)

  /**
   *
   * Resize will resize the canvas, it will not scale the image.
   * This is like a "canvas resize" in Photoshop.
   *
   * @param position where to position the original image after the canvas size change
   *
   * @return a new Image that is the result of resizing the canvas.
   */
  def resizeToHeight(targetHeight: Int, position: Position = Center, background: Color = Color.WHITE): R =
    resizeTo((targetHeight / height.toDouble * height).toInt, targetHeight, position, background)

  /**
   *
   * Resize will resize the canvas, it will not scale the image.
   * This is like a "canvas resize" in Photoshop.
   *
   * @param position where to position the original image after the canvas size change
   *
   * @return a new Image that is the result of resizing the canvas.
   */
  def resizeToWidth(targetWidth: Int, position: Position = Center, background: Color = Color.WHITE): R =
    resizeTo(targetWidth, (targetWidth / width.toDouble * height).toInt, position, background)

  /**
   *
   * Scale will resize the canvas and scale the image to match.
   * This is like a "image resize" in Photoshop.
   *
   * @param targetWidth the target width
   * @param targetHeight the target width
   * @param scaleMethod the type of scaling method to use.
   *
   * @return a new Image that is the result of scaling this image
   */
  def scaleTo(targetWidth: Int, targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): R

  /**
   *
   * Scale will resize the canvas and scale the image to match.
   * This is like a "image resize" in Photoshop.
   *
   * This overloaded version of scale will scale the image so that the new image
   * has a width that matches the given targetWidth
   * and the same aspect ratio as the original.
   *
   * Eg, an image of 200,300 with a scaleToWidth of 400 will result
   * in a scaled image of 400,600
   *
   * @param targetWidth the target width
   * @param scaleMethod the type of scaling method to use.
   *
   * @return a new Image that is the result of scaling this image
   */
  def scaleToWidth(targetWidth: Int, scaleMethod: ScaleMethod = Bicubic): R =
    scaleTo(targetWidth, (targetWidth / width.toDouble * height).toInt, scaleMethod)

  /**
   *
   * Scale will resize the canvas and scale the image to match.
   * This is like a "image resize" in Photoshop.
   *
   * This overloaded version of scale will scale the image so that the new image
   * has a height that matches the given targetHeight
   * and the same aspect ratio as the original.
   *
   * Eg, an image of 200,300 with a scaleToHeight of 450 will result
   * in a scaled image of 300,450
   *
   * @param targetHeight the target height
   * @param scaleMethod the type of scaling method to use.
   *
   * @return a new Image that is the result of scaling this image
   */
  def scaleToHeight(targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): R =
    scaleTo((targetHeight / height.toDouble * width).toInt, targetHeight, scaleMethod)

  /**
   *
   * Scale will resize the canvas and the image.
   * This is like a "image resize" in Photoshop.
   *
   * @param scaleFactor the target increase or decrease. 1 is the same as original.
   * @param scaleMethod the type of scaling method to use.
   *
   * @return a new Image that is the result of scaling this image
   */
  def scale(scaleFactor: Double, scaleMethod: ScaleMethod = Bicubic): R =
    scaleTo((width * scaleFactor).toInt, (height * scaleFactor).toInt, scaleMethod)

  def writer[T <: ImageWriter](format: Format[T]): T

  def write: Array[Byte] = write(Format.PNG)
  def write(format: Format[_ <: ImageWriter]): Array[Byte] = writer(format).write()

  def write(path: String) {
    write(path, Format.PNG)
  }
  def write(path: String, format: Format[_ <: ImageWriter]) {
    write(new File(path), format)
  }
  def write(file: File) {
    write(file, Format.PNG)
  }
  def write(file: File, format: Format[_ <: ImageWriter]) {
    write(FileUtils.openOutputStream(file), format)
  }
  def write(out: OutputStream) {
    write(out, PNG)
  }
  def write(out: OutputStream, format: Format[_ <: ImageWriter]) {
    writer(format).write(out)
  }

  def pixels: Array[Int]

  /**
   *
   * @param pixel the pixel colour to look for.
   * @return true if there exists at least one pixel that has the given pixels color
   */
  def exists(pixel: Int) = pixels.exists(_ == pixel)

  override def equals(obj: Any): Boolean = obj match {
    case other: ImageLike[_] => other.pixels.sameElements(pixels)
    case _ => false
  }
}
