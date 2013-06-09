package com.sksamuel.scrimage.io

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Image

/** @author Stephen Samuel */
class GifWriterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = Image(getClass.getResourceAsStream("/bird.jpg")).scale(300, 200)

    test("GIF writer outputs") {
        val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_compressed.gif"))
        assert(original === expected)
    }
}
