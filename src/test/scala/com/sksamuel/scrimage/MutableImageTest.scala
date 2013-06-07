package com.sksamuel.scrimage

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.filter.BlurFilter

/** @author Stephen Samuel */
class MutableImageTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val in = getClass.getResourceAsStream("/bird.jpg")
    val image = MutableImage(in)

    test("when flipping on x axis a new image is not created") {
        val flipped = image.flipX
        assert(flipped.eq(image))
    }

    test("when flipping on y axis a new image is not created") {
        val flipped = image.flipY
        assert(flipped.eq(image))
    }

    test("when applying a filter a new image is not created") {
        val flipped = image.filter(BlurFilter)
        assert(flipped.eq(image))
    }
}
