package com.sksamuel.scrimage.composite

import com.sksamuel.scrimage.Image
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

/** @author Stephen Samuel */
class AlphaCompositeTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val source = Image.fromStream(getClass.getResourceAsStream("/colosseum.jpg")).resizeTo(400, 300)
  val transparent = Image.fromStream(getClass.getResourceAsStream("/transparent_chip.png"))
  val expected1 = Image.fromStream(getClass.getResourceAsStream("/composite/alpha_composite.png"))
  val expected2 = Image.fromStream(getClass.getResourceAsStream("/composite/alpha_composite_0.5f.png"))

  test("alpha composite uses transparency of application image") {
    val actual = source.composite(AlphaComposite(1f), transparent)
    assert(expected1 === actual)
  }

  test("alpha composite uses transparency of application image combined with alpha") {
    val actual = source.composite(AlphaComposite(0.5f), transparent)
    assert(expected2 === actual)
  }
}
