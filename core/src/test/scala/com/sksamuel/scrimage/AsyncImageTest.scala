package com.sksamuel.scrimage

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import scala.concurrent._
import scala.concurrent.duration._

/** @author Stephen Samuel */
class AsyncImageTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  import scala.concurrent.ExecutionContext.Implicits.global

  val in = getClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")
  val image = Image(in)

  test("fit delegates to image") {
    val future = image.toAsync.fit(200, 400)
    Await.ready(future, 10 seconds)
    assert(future.value.get.get === image.fit(200, 400))
  }

  test("scaleTo delegates to image") {
    val future = image.toAsync.scaleTo(400, 500)
    Await.ready(future, 10 seconds)
    assert(future.value.get.get === image.scaleTo(400, 500))
  }

  test("resizeTo delegates to image") {
    val future = image.toAsync.resizeTo(400, 500)
    Await.ready(future, 10 seconds)
    assert(future.value.get.get === image.resizeTo(400, 500))
  }

  test("dimensions happy path") {
    val async = image.toAsync
    assert(async.width === image.width)
    assert(async.height === image.height)
  }

  test("foreach accesses to each pixel") {
    val async = Image.empty(100, 100).toAsync
    var count = 0
    async.foreach((_, _, _) => count = count + 1)
    assert(10000 === count)
  }

  test("map modifies each pixel and returns new image") {
    val async = Image.empty(100, 100).toAsync
    val future = async.map((_, _, _) => 0xFF00FF00)
    Await.ready(future, 10 seconds)
    for (component <- future.value.get.get.toImage.argb)
      assert(component === Array(255, 0, 255, 0))
  }
}
