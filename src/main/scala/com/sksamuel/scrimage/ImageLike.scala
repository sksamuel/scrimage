package com.sksamuel.scrimage

import com.sksamuel.scrimage.ScaleMethod.Bicubic
import com.sksamuel.scrimage.Position.Center

/** @author Stephen Samuel */
trait ImageLike[R] {

    def scaleToHeight(targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): R
    def scaleToWidth(targetWidth: Int, scaleMethod: ScaleMethod = Bicubic): R
    def scaleTo(targetWidth: Int, targetHeight: Int, scaleMethod: ScaleMethod = Bicubic): R
    def scale(scaleFactor: Double, scaleMethod: ScaleMethod = Bicubic): R

    def resize(scaleFactor: Double, position: Position = Center): R
    def resizeTo(targetWidth: Int, targetHeight: Int, position: Position = Center): R
    def resizeToHeight(targetHeight: Int, position: Position = Center): R
    def resizeToWidth(targetWidth: Int, position: Position = Center): R
}
