package com.sksamuel.scrimage

import org.scalatest.{ FunSuite, Matchers }

/** @author Stephen Samuel */
class Issue63Test extends FunSuite with Matchers {

  test("image for issue 63 should parse") {
    Image(getClass.getResourceAsStream("/issue63.png")).width shouldBe 1600
  }
}
