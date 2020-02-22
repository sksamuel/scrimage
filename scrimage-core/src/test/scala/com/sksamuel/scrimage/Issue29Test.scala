package com.sksamuel.scrimage

import org.scalatest.{FunSuite, OneInstancePerTest}

/** @author Stephen Samuel */
class Issue29Test extends FunSuite with OneInstancePerTest {

  test("image for issue 29 should load") {
    assert(ImmutableImage.fromStream(getClass.getResourceAsStream("/issue29.jpeg")) != null)
  }
}
