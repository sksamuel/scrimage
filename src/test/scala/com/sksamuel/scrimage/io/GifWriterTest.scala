package com.sksamuel.scrimage.io

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.{Image, Format}
import java.io.ByteArrayOutputStream

/** @author Stephen Samuel */
class GifWriterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val original = Image(getClass.getResourceAsStream("/bird.jpg")).scale(300, 200)

    test("GIF output happy path") {
        val out = new ByteArrayOutputStream()
        original.write(out, Format.GIF)
        val actual = Image(out.toByteArray)

        val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_compressed.gif"))
        assert(expected.pixels.length === actual.pixels.length)
        assert(expected === actual)

    }

    test("GIF progressive output happy path") {
        val out = new ByteArrayOutputStream()
        original.writer(Format.GIF).withProgressive(true).write(out)
        val actual = Image(out.toByteArray)

        val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_progressive.gif"))
        assert(expected.pixels.length === actual.pixels.length)
        assert(expected === actual)

    }
}
