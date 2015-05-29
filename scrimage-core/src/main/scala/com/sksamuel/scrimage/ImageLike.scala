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

import java.io.{ File, OutputStream }

import com.sksamuel.scrimage.Format.PNG
import com.sksamuel.scrimage.Position.Center
import com.sksamuel.scrimage.ScaleMethod.Bicubic
import com.sksamuel.scrimage.io.ImageWriter
import org.apache.commons.io.{ FileUtils, IOUtils }

/** Read-only image operations.
  *
  * @author Stephen Samuel
  */
trait ImageLike[R] {

  lazy val points: Seq[(Int, Int)] = for (x <- 0 until width; y <- 0 until height) yield (x, y)

  /** Returns the centre coordinates for the image.
    */
  lazy val center: (Int, Int) = (width / 2, height / 2)
  lazy val radius: Int = Math.sqrt(Math.pow(width / 2.0, 2) + Math.pow(height / 2.0, 2)).toInt
  lazy val dimensions: (Int, Int) = (width, height)

  /** @return Returns the aspect ratio for this image.
    */
  lazy val ratio: Double = if (height == 0) 0 else width / height.toDouble

  def width: Int
  def height: Int

  def forall(f: (Int, Int, Pixel) => Boolean): Boolean = points.forall(p => f(p._1, p._2, pixel(p)))
  def foreach(f: (Int, Int, Pixel) => Unit): Unit = points.foreach(p => f(p._1, p._2, pixel(p)))

  def row(y: Int): Array[Pixel] = pixels(0, y, width, 1)
  def col(x: Int): Array[Pixel] = pixels(x, 0, 1, height)

  /** Returns the pixel at the given coordinates.
    *
    * @param x the x coordinate of the pixel to grab
    * @param y the y coordinate of the pixel to grab
    *
    * @return the Pixel at the location
    */
  def pixel(x: Int, y: Int): Pixel

  /** Returns the color at the given coordinates.
    *
    * @return the RGBColor value of the pixel
    */
  def color(x: Int, y: Int): RGBColor = pixel(x, y).toInt

  /** Returns the ARGB components for the pixel at the given coordinates
    *
    * @param x the x coordinate of the pixel component to grab
    * @param y the y coordinate of the pixel component to grab
    *
    * @return an array containing ARGB components in that order.
    */
  def argb(x: Int, y: Int): Array[Int] = {
    val p = pixel(x, y)
    Array(p.alpha, p.red, p.green, p.blue)
  }

  /** Returns the ARGB components for all pixels in this image
    *
    * @return an array containing ARGB components in that order.
    */
  def argb: Array[Array[Int]] = {
    pixels.map(p => Array(p.alpha, p.red, p.green, p.blue))
  }

  def rgb(x: Int, y: Int): Array[Int] = {
    val p = pixel(x, y)
    Array(p.red, p.green, p.blue)
  }

  def rgb: Array[Array[Int]] = {
    pixels.map(p => Array(p.red, p.green, p.blue))
  }

  /** Returns the pixel at the given coordinates as a integer in RGB format.
    *
    * @param p the pixel as an integer tuple
    *
    * @return the ARGB value of the pixel
    */
  def pixel(p: (Int, Int)): Pixel = pixel(p._1, p._2)

  /** Returns a rectangular region within the given boundaries as a single
    * dimensional array of integers.
    *
    * Eg, pixels(10, 10, 30, 20) would result in an array of size 600 with
    * the first row of the region in indexes 0,..,29, second row 30,..,59 etc.
    *
    * @param x the start x coordinate
    * @param y the start y coordinate
    * @param w the width of the region
    * @param h the height of the region
    * @return an Array of pixels for the region
    */
  def pixels(x: Int, y: Int, w: Int, h: Int): Array[Pixel] = {
    for (
      y1 <- Array.range(y, y + h);
      x1 <- Array.range(x, x + w)
    ) yield pixel(x1, y1)
  }

  /** Creates a new image which is the result of this image
    * padded with the given number of pixels on each edge.
    *
    * Eg, requesting a pad of 30 on an image of 250,300 will result
    * in a new image with a canvas size of 310,360
    *
    * @param size the number of pixels to add on each edge
    * @param color the background of the padded area.
    *
    * @return A new image that is the result of the padding
    */
  def pad(size: Int, color: Color = X11Colorlist.White): R = {
    padTo(width + size * 2, height + size * 2, color)
  }

  /** Creates a new image which is the result of this image padded to the canvas size specified.
    * If this image is already larger than the specified pad then the sizes of the existing
    * image will be used instead.
    *
    * Eg, requesting a pad of 200,200 on an image of 250,300 will result
    * in keeping the 250,300.
    *
    * Eg2, requesting a pad of 300,300 on an image of 400,250 will result
    * in the width staying at 400 and the height padded to 300.
    *
    * @param targetWidth the size of the output canvas width
    * @param targetHeight the size of the output canvas height
    * @param color the background of the padded area.
    *
    * @return A new image that is the result of the padding
    */
  def padTo(targetWidth: Int, targetHeight: Int, color: Color = X11Colorlist.White): R

  /** Creates an empty Image with the same dimensions of this image.
    *
    * @return a new Image that is a clone of this image but with uninitialized data
    */
  def empty: Image

  /** Returns the number of pixels in the image.
    *
    * @return the number of pixels
    */
  def count: Int = pixels.length

  /** Returns a set of the distinct colours used in this image.
    *
    * @return the set of distinct Colors
    */
  def colours: Set[Color] = pixels.map(argb => Color(argb.toInt)).toSet

  /** Counts the number of pixels with the given colour.
    *
    * @param color the colour to detect.
    * @return the number of pixels that matched the colour of the given pixel
    */
  def count(color: Color): Int = pixels.find(_ == color.toInt).size

  /** Creates a new image with the same data as this image.
    * Any operations to the copied image will not write back to the original.
    *
    * @return A copy of this image.
    */
  def copy: Image

  def cover(targetWidth: Int,
            targetHeight: Int,
            scaleMethod: ScaleMethod = Bicubic,
            position: Position = Center): R

  /** Maps the pixels of this image into another image by applying the given function to each pixel.
    *
    * The function accepts three parameters: x,y,p where x and y are the coordinates of the pixel
    * being transformed and p is the pixel at that location.
    *
    * @param f the function to transform pixel x,y with existing value p into new pixel value p' (p prime)
    * @return
    */
  def map(f: (Int, Int, Pixel) => Pixel): R

  /** Creates a copy of this image with the given filter applied.
    * The original (this) image is unchanged.
    *
    * @param filter the filter to apply. See com.sksamuel.scrimage.Filter.
    *
    * @return A new image with the given filter applied.
    */
  def filter(filter: Filter): R

  def fit(targetWidth: Int, targetHeight: Int, color: Color, scaleMethod: ScaleMethod, position: Position): R

  def fitToHeight(targetHeight: Int, color: Color = X11Colorlist.White,
                  scaleMethod: ScaleMethod = Bicubic, position: Position = Center): R =
    fit((targetHeight / height.toDouble * height).toInt, targetHeight, color, scaleMethod, position)

  def fitToWidth(targetWidth: Int, color: Color = X11Colorlist.White,
                 scaleMethod: ScaleMethod = Bicubic, position: Position = Center): R =
    fit(targetWidth, (targetWidth / width.toDouble * height).toInt, color, scaleMethod, position)

  def resizeTo(targetWidth: Int,
               targetHeight: Int,
               position: Position,
               background: Color = X11Colorlist.White): R

  /** Resize will resize the canvas, it will not scale the image.
    * This is like a "canvas resize" in Photoshop.
    *
    * @param scaleFactor the scaleFactor. 1 retains original size. 0.5 is half. 2 double. etc
    * @param position where to position the original image after the canvas size change. Defaults to centre.
    * @param background the color to use for expande background areas. Defaults to White.
    *
    * @return a new Image that is the result of resizing the canvas.
    */
  def resize(scaleFactor: Double, position: Position = Center, background: Color = X11Colorlist.White): R =
    resizeTo((width * scaleFactor).toInt, (height * scaleFactor).toInt, position, background)

  /** Resize will resize the canvas, it will not scale the image.
    * This is like a "canvas resize" in Photoshop.
    *
    * @param position where to position the original image after the canvas size change
    *
    * @return a new Image that is the result of resizing the canvas.
    */
  def resizeToHeight(targetHeight: Int,
                     position: Position = Center,
                     background: Color = X11Colorlist.White): R =
    resizeTo((targetHeight / height.toDouble * height).toInt, targetHeight, position, background)

  /** Resize will resize the canvas, it will not scale the image.
    * This is like a "canvas resize" in Photoshop.
    *
    * @param position where to position the original image after the canvas size change
    *
    * @return a new Image that is the result of resizing the canvas.
    */
  def resizeToWidth(targetWidth: Int, position: Position = Center, background: Color = X11Colorlist.White): R =
    resizeTo(targetWidth, (targetWidth / width.toDouble * height).toInt, position, background)

  /** Scale will resize the canvas and scale the image to match.
    * This is like a "image resize" in Photoshop.
    *
    * @param targetWidth the target width
    * @param targetHeight the target width
    * @param scaleMethod the type of scaling method to use.
    *
    * @return a new Image that is the result of scaling this image
    */
  def scaleTo(targetWidth: Int, targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): R

  /** Scale will resize the canvas and scale the image to match.
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

  /** Scale will resize the canvas and scale the image to match.
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

  /** Scale will resize the canvas and the image.
    * This is like a "image resize" in Photoshop.
    *
    * @param scaleFactor the target increase or decrease. 1 is the same as original.
    * @param scaleMethod the type of scaling method to use.
    *
    * @return a new Image that is the result of scaling this image
    */
  def scale(scaleFactor: Double, scaleMethod: ScaleMethod = Bicubic): R =
    scaleTo((width * scaleFactor).toInt, (height * scaleFactor).toInt, scaleMethod)

  def pixels: Array[Pixel]

  /** Returns true if a pixel with the given color exists.
    *
    * @param color the pixel colour to look for.
    * @return true if there exists at least one pixel that has the given pixels color
    */
  def exists(color: Color): Boolean = pixels.exists(pixel => pixel.toInt == color.toRGB.toInt)

  // This tuple contains all the state that identifies this particular image.
  private[scrimage] def imageState = (width, height, pixels.toList)

  // See this Stack Overflow question to see why this is implemented this way.
  // http://stackoverflow.com/questions/7370925/what-is-the-standard-idiom-for-implementing-equals-and-hashcode-in-scala
  override def hashCode: Int = imageState.hashCode

  override def equals(other: Any): Boolean = {
    other match {
      case that: ImageLike[_] => imageState == that.imageState
      case _ => false
    }
  }
}

trait WritableImageLike {

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
    val fos = FileUtils.openOutputStream(file)
    write(fos, format)
    IOUtils.closeQuietly(fos)
  }
  def write(out: OutputStream) {
    write(out, PNG)
  }
  def write(out: OutputStream, format: Format[_ <: ImageWriter]) {
    writer(format).write(out)
  }

}
