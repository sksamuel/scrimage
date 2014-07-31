package com.sksamuel.scrimage.canvas.composite

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.canvas.Canvas._

/** @author Stephen Samuel */
class AlphaCompositeTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val source = Image(getClass.getResourceAsStream("/colosseum.jpg")).resizeTo(400, 300)
  val transparent = Image(getClass.getResourceAsStream("/transparent_chip.png"))
  val expected1 = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/composite/alpha_composite.png"))
  val expected2 = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/composite/alpha_composite_0.5f.png"))

  test("alpha composite uses transparency of application image") {
    val actual = source.composite(AlphaComposite(1f), transparent)
    assert(expected1 === actual)
  }

  test("alpha composite uses transparency of application image combined with alpha") {
    val actual = source.composite(AlphaComposite(0.5f), transparent)
    assert(expected2 === actual)
  }
}
