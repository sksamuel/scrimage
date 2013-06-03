package com.sksamuel.scrimage

/** @author Stephen Samuel
  *
  *         RichImage is class that represents an in memory image.
  *
  **/
class RichImage {

    def width: Int = 0
    def height: Int = 0
    def filter(filter: Filter): RichImage = null
    def fit(op: Operation): RichImage = null
}

trait Operation
trait Filter

object RichImage {
    implicit def awt2rich(image: java.awt.Image) = new RichImage
}
