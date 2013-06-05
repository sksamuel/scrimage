package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image
import java.io.File

/** @author Stephen Samuel */
class FreezeFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = Image(getClass.getResourceAsStream("/bird_small.png"))
    val expected = getClass.getResourceAsStream("/bird_small_freeze.png")

    test("filter output matches expected") {
        original.filter(FreezeFilter).write(new File("src/test/resources/bird_small_freeze.png"))
        assert(original.filter(FreezeFilter) === Image(expected))
    }
}
