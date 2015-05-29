package com.sksamuel.scrimage

import org.scalatest.{ OneInstancePerTest, FunSuite }

/** @author Stephen Samuel */
class Issue29Test extends FunSuite with OneInstancePerTest {

  test("image for issue 29 should load") {
    assert(Image(getClass.getResourceAsStream("/issue29.jpeg")) != null)
  }
}
