package com.sksamuel.scrimage

import org.scalatest.{WordSpec, Matchers}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class ScalingDeadlockTest extends WordSpec with Matchers {

  import scala.concurrent.ExecutionContext.Implicits.global

  val in = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg"))

  "image scale" should {
    "not deadlock on multiple concurrent scales" in {
      val futures = for ( k <- 0 until 50 ) yield {
        Future {
          in.scaleTo(200, 200)
        }
      }
      val images = Await.result(Future.sequence(futures), 1.minute)
      images.size shouldBe 50
    }
  }
}
