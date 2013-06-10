package com.sksamuel.scrimage

import scala.concurrent._
import com.sksamuel.scrimage.Position.Center
import scala.concurrent.ExecutionContext.Implicits.global

/** @author Stephen Samuel */
class AsyncImage(image: Image) extends ImageLike[Future[Image]] {

    def scale(scaleFactor: Double, scaleMethod: ScaleMethod): Future[Image] = future {
        image.scale(scaleFactor, scaleMethod)
    }
    def scaleTo(targetWidth: Int, targetHeight: Int, scaleMethod: ScaleMethod): Future[Image] = future {
        image.scaleTo(targetWidth, targetHeight, scaleMethod)
    }
    def scaleToWidth(targetWidth: Int, scaleMethod: ScaleMethod): Future[Image] = future {
        image.scaleToWidth(targetWidth, scaleMethod)
    }
    def scaleToHeight(targetHeight: Int, scaleMethod: ScaleMethod): Future[Image] = future {
        image.scaleToHeight(targetHeight, scaleMethod)
    }

    def resize(scaleFactor: Double, position: Position = Center): Future[Image] = future {
        image.resize(scaleFactor, position)
    }
    def resizeTo(targetWidth: Int, targetHeight: Int, position: Position = Center): Future[Image] = future {
        image.resizeTo(targetWidth, targetHeight, position)
    }
    def resizeToHeight(targetHeight: Int, position: Position = Center): Future[Image] = future {
        image.resizeToHeight(targetHeight, position)
    }
    def resizeToWidth(targetWidth: Int, position: Position = Center): Future[Image] = future {
        image.resizeToWidth(targetWidth, position)
    }
}
