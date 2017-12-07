package com.sksamuel.scrimage

class PipelineFilter(filters: Filter*) extends Filter {
  def apply(image: Image): Unit = filters.foreach(_.apply(image))
}
