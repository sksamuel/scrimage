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
sealed trait Position {

  /**
   * Returns the x coordinate for where a target should be placed inside the source.
   */
  def calculateX(sourceWidth: Int, sourceHeight: Int, targetW: Int, targetH: Int): Int

  /**
   * Returns the y coordinate for where an image should be placed inside the canvas.
   */
  def calculateY(sourceWidth: Int, sourceHeight: Int, targetW: Int, targetH: Int): Int

  def calculateXY(sourceWidth: Int, sourceHeight: Int, targetW: Int, targetH: Int): (Int, Int) =
    (calculateX(sourceWidth, sourceHeight, targetW, targetH),
      calculateY(sourceWidth, sourceHeight, targetW, targetH))
}

trait Left extends Position {
  def calculateX(sourceWidth: Int, sourceHeight: Int, targetW: Int, targetH: Int): Int = 0
}

trait Right extends Position {
  def calculateX(sourceWidth: Int, sourceHeight: Int, targetW: Int, targetH: Int): Int = sourceWidth - targetW
}

trait Top extends Position {
  def calculateY(sourceWidth: Int, sourceHeight: Int, targetW: Int, targetH: Int): Int = 0
}

trait Bottom extends Position {
  def calculateY(sourceWidth: Int,
                 sourceHeight: Int,
                 targetW: Int,
                 targetH: Int): Int = sourceHeight - targetH
}

trait CenterX extends Position {
  def calculateX(sourceWidth: Int, sourceHeight: Int, targetW: Int, targetH: Int): Int =
    ((sourceWidth - targetW) / 2.0).toInt
}

trait CenterY extends Position {
  def calculateY(sourceWidth: Int, sourceHeight: Int, targetW: Int, targetH: Int): Int =
    ((sourceHeight - targetH) / 2.0).toInt
}

object Position {
  object TopLeft extends Top with Left
  object TopCenter extends Top with CenterX
  object TopRight extends Top with Right
  object CenterLeft extends CenterY with Left
  object Center extends CenterY with CenterX
  object CenterRight extends CenterY with Right
  object BottomLeft extends Bottom with Left
  object BottomCenter extends Bottom with CenterX
  object BottomRight extends Bottom with Right
}