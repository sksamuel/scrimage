package com.sksamuel.scrimage.nio

import com.sksamuel.scrimage.ImmutableImage
import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class GifWriterTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  val original: ImmutableImage = ImmutableImage.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg")).scaleTo(300, 200)

  test("GIF output happy path") {
    val actual = ImmutableImage(original.bytes(GifWriter.Default))
    val expected = ImmutableImage.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_compressed.gif"))
    assert(expected.pixels.length === actual.pixels.length)
    assert(expected == actual)
  }

  test("GIF progressive output happy path") {
    val actual = ImmutableImage(original.bytes(GifWriter.Progressive))
    val expected = ImmutableImage.fromStream(getClass.getResourceAsStream("/com/sksamuel/scrimage/io/bird_progressive.gif"))
    assert(expected.pixels.length === actual.pixels.length)
    assert(expected == actual)
  }
}
