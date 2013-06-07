package com.sksamuel.scrimage.filter

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image
import java.io.File

/** @author Stephen Samuel */
class TelevisionFilterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = Image(getClass.getResourceAsStream("/bird_small.png"))

    test("filter output matches expected") {
        original.filter(TelevisionFilter).write(new File("src/test/resources/bird_small_television.png"))

        val expected = Image(getClass.getResourceAsStream("/bird_small_television.png"))
        assert(original.filter(TelevisionFilter) === expected)
    }
}
