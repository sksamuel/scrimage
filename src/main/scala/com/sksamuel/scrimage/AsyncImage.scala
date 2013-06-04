package com.sksamuel.scrimage

import scala.concurrent._
import com.sksamuel.scrimage.Centering.Center
import com.sksamuel.scrimage.ScaleMethod.Bicubic
import ExecutionContext.Implicits.global

/** @author Stephen Samuel */
class AsyncImage(image: Image) {

    def filter(filter: Filter): Future[AsyncImage] = future {
        image.filter(filter)
        this
    }

    def resize(width: Int, height: Int, centering: Centering = Center): Future[AsyncImage] = future {
        image.resize(width, height, centering)
        this
    }

    def scale(width: Int, height: Int, scaleMethod: ScaleMethod = Bicubic): Future[AsyncImage] = future {
        image.scale(width, height)
        this
    }
}

object AsyncImage {
    implicit def image2async(image: Image) = new AsyncImage(image)
}
