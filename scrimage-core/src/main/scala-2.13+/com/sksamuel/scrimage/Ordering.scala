package com.sksamuel.scrimage

object Ordering {

  implicit val floatOrder: math.Ordering[Float] = scala.Ordering.Float.IeeeOrdering

}
