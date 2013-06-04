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

    def resize(dimensions: (Int, Int), centering: Centering = Center): Future[AsyncImage] = future {
        image.resize(dimensions, centering)
        this
    }

    def scale(dimensions: (Int, Int), scaleMethod: ScaleMethod = Bicubic): Future[AsyncImage] = future {
        image.scale(dimensions, scaleMethod)
        this
    }
}

object AsyncImage {
    implicit def image2async(image: Image) = new AsyncImage(image)
}
