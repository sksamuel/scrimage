package com.sksamuel.scrimage

import scala.util.Using.Releasable

object Using {

  def resource[R, A](resource: R)(body: R => A)(implicit releasable: Releasable[R]): A  =
    scala.util.Using.resource(resource)(body)

}
