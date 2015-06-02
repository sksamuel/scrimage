package com.sksamuel.scrimage

import org.scalatest.{FunSuite, Matchers}

/** @author Stephen Samuel */
class Issue54Test extends FunSuite with Matchers {

  import scala.concurrent.ExecutionContext.Implicits.global

  test("image for issue 54 should load as expected") {
    Image(getClass.getResourceAsStream("/issue54.jpg")).width shouldBe 2560
    Image(getClass.getResourceAsStream("/issue54.jpg")).scaleToWidth(400) shouldBe
      Image(getClass.getResourceAsStream("/issue54.png"))
  }
}
