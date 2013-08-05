package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{Image, Filter}

/** @author Stephen Samuel
  *
  *         Applies several filters at once in sequential order
  *
  * */
class PipelineFilter(filters: Filter*) extends Filter {
  def apply(image: Image) = filters.foldLeft(image)((image, filter) => image.filter(filter))
}
