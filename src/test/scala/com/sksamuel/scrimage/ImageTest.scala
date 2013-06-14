package com.sksamuel.scrimage

import org.scalatest.{BeforeAndAfter, FunSuite}
import java.awt.image.BufferedImage
import com.sksamuel.scrimage.filter.BlurFilter
import java.awt.Color

/** @author Stephen Samuel */
class ImageTest extends FunSuite with BeforeAndAfter {

    val in = getClass.getResourceAsStream("/bird.jpg")
    val image = Image(in)

    test("ratio happy path") {
        val awt1 = new BufferedImage(200, 400, BufferedImage.TYPE_INT_ARGB)
        assert(0.5 === Image(awt1).ratio)

        val awt2 = new BufferedImage(400, 200, BufferedImage.TYPE_INT_ARGB)
        assert(2 === Image(awt2).ratio)

        val awt3 = new BufferedImage(333, 333, BufferedImage.TYPE_INT_ARGB)
        assert(1 === Image(awt3).ratio)

        val awt4 = new BufferedImage(333, 111, BufferedImage.TYPE_INT_ARGB)
        assert(3.0 === Image(awt4).ratio)

        val awt5 = new BufferedImage(111, 333, BufferedImage.TYPE_INT_ARGB)
        assert(1 / 3d === Image(awt5).ratio)
    }

    test("copy returns a new backing image") {
        val copy = image.copy
        assert(copy.awt.hashCode != image.awt.hashCode)
    }

    test("when applying a filter a new image is created") {
        val flipped = image.filter(BlurFilter)
        assert(!flipped.eq(image))
    }

    test("when scaling by pixels then the output image has the given dimensions") {
        val scaled = image.scaleTo(40, 50)
        assert(40 === scaled.width)
        assert(50 === scaled.height)
    }

    test("when scaling by scale factor then the output image has the scaled dimensions") {
        val scaled = image.scale(0.5)
        assert(972 === scaled.width)
        assert(648 === scaled.height)
    }

    test("when resizing by pixels then the output image has the given dimensions") {
        val scaled = image.resizeTo(440, 505)
        assert(440 === scaled.width)
        assert(505 === scaled.height)
    }

    test("when resizing by scale factor then the output image has the scaled dimensions") {
        val scaled = image.resize(0.5)
        assert(972 === scaled.width)
        assert(648 === scaled.height)
    }

    test("dimensions happy path") {
        val awt = new BufferedImage(200, 400, BufferedImage.TYPE_INT_ARGB)
        assert((200, 400) === Image(awt).dimensions)
    }

    test("pixel returns correct ARGB integer") {
        val image = new Image(new BufferedImage(50, 30, BufferedImage.TYPE_INT_ARGB))
        val g = image.awt.getGraphics
        g.setColor(java.awt.Color.RED)
        g.fillRect(10, 10, 10, 10)
        g.dispose()
        assert(0 === image.pixel(0, 0))
        assert(0 === image.pixel(9, 10))
        assert(0 === image.pixel(10, 9))
        assert(0xFFFF0000 === image.pixel(10, 10))
        assert(0xFFFF0000 === image.pixel(19, 19))
        assert(0 === image.pixel(20, 20))
    }

    test("pixel array has correct number of pixels") {
        val image = new Image(new BufferedImage(50, 30, BufferedImage.TYPE_INT_ARGB))
        assert(1500 === image.pixels.size)
    }

    test("pixel array has correct ARGB integer") {
        val image = new Image(new BufferedImage(50, 30, BufferedImage.TYPE_INT_ARGB))
        val g = image.awt.getGraphics
        g.setColor(java.awt.Color.RED)
        g.fillRect(10, 10, 10, 10)
        g.dispose()
        assert(0 === image.pixels(0))
        assert(0xFFFF0000 === image.pixels(765))
    }

    test("when created a filled copy then the dimensions are the same as the original") {
        val copy1 = image.filled(java.awt.Color.RED)
        assert(1944 === copy1.width)
        assert(1296 === copy1.height)

        val copy2 = image.filled(0x00FF00FF)
        assert(1944 === copy2.width)
        assert(1296 === copy2.height)

        val copy3 = image.filled(java.awt.Color.WHITE)
        assert(1944 === copy3.width)
        assert(1296 === copy3.height)
    }

    test("hashcode is as from AWT") {
        val buffered = new BufferedImage(445, 464, Image.CANONICAL_DATA_TYPE)
        assert(Image(buffered).hashCode === buffered.hashCode)
    }

    test("when creating a blank copy then the dimensions are the same as the original") {
        val copy = image.empty
        assert(1944 === copy.width)
        assert(1296 === copy.height)
    }

    test("when create a new filled image then the dimensions are as specified") {
        val image = Image.filled(595, 911, java.awt.Color.BLACK)
        assert(595 === image.width)
        assert(911 === image.height)
    }

    test("when creating a new empty image then the dimensions are as specified") {
        val image = Image.empty(80, 90)
        assert(80 === image.width)
        assert(90 === image.height)
    }

    test("when padding to a width smaller than the image width then the width is not reduced") {
        val image = Image.empty(85, 56)
        val padded = image.padTo(55, 162)
        assert(85 === padded.width)
    }

    test("when padding to a height smaller than the image height then the height is not reduced") {
        val image = Image.empty(85, 56)
        val padded = image.padTo(90, 15)
        assert(56 === padded.height)
    }

    test("when padding to a width larger than the image width then the width is increased") {
        val image = Image.empty(85, 56)
        val padded = image.padTo(151, 162)
        assert(151 === padded.width)
    }

    test("when padding to a height larger than the image height then the height is increased") {
        val image = Image.empty(85, 56)
        val padded = image.padTo(90, 77)
        assert(77 === padded.height)
    }

    test("when padding to a size larger than the image then the image canvas is increased") {
        val image = Image.empty(85, 56)
        val padded = image.padTo(515, 643)
        assert(515 === padded.width)
        assert(643 === padded.height)
    }

    test("when padding with a border size then the width and height are increased by the right amount") {
        val padded = image.pad(4, java.awt.Color.WHITE)
        assert(1952 === padded.width)
        assert(1304 === padded.height)
    }

    test("when flipping on x axis the dimensions are retained") {
        val flipped = image.flipX
        assert(1944 === flipped.width)
        assert(1296 === flipped.height)
    }

    test("when flipping on y axis the dimensions are retained") {
        val flipped = image.flipY
        assert(1944 === flipped.width)
        assert(1296 === flipped.height)
    }

    test("when flipping on x axis a new image is created") {
        val flipped = image.flipX
        assert(!flipped.eq(image))
    }

    test("when flipping on y axis a new image is created") {
        val flipped = image.flipY
        assert(!flipped.eq(image))
    }

    test("when rotating left the width and height are reversed") {
        val flipped = image.rotateLeft
        assert(1296 === flipped.width)
        assert(1944 === flipped.height)
    }

    test("when rotating right the width and height are reversed") {
        val flipped = image.rotateRight
        assert(1296 === flipped.width)
        assert(1944 === flipped.height)
    }

    test("when fitting an image the output image should have the specified dimensions") {
        val fitted = image.fit(51, 66)
        assert(51 === fitted.width)
        assert(66 === fitted.height)
    }

    test("when resizing an image the output image should match as expected") {
        val resized = image.resize(0.25)
        val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird_resized_025.png"))
        assert(expected.pixels.length === resized.pixels.length)
        assert(expected.pixels === resized.pixels)
    }

    test("when resizing an image the output image should have specified dimensions") {
        val r = image.resizeTo(900, 300)
        assert(900 === r.width)
        assert(300 === r.height)
    }

    test("when scaling an image the output image should match as expected") {
        val scaled = image.scale(0.25)
        val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird_scale_025.png"))
        assert(expected.pixels.length === scaled.pixels.length)
        assert(expected === scaled)
    }

    test("when scaling an image the output image should have specified dimensions") {
        val scaled = image.scaleTo(900, 300)
        assert(900 === scaled.width)
        assert(300 === scaled.height)
    }

    test("when fitting an image the output image should match as expected") {
        val fitted = image.fit(900, 300, java.awt.Color.RED)
        val expected = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird_fitted_900_300.png"))
        assert(fitted.pixels.length === fitted.pixels.length)
        assert(expected === fitted)
    }

    test("when fitting an image the output image should have specified dimensions") {
        val fitted = image.fit(900, 300, java.awt.Color.RED)
        assert(900 === fitted.width)
        assert(300 === fitted.height)
    }

    test("when scaling by width then target image maintains aspect ratio") {
        val scaled = image.scaleToWidth(500)
        assert(scaled.width === 500)
        assert(scaled.ratio - image.ratio < 0.01)
    }

    test("when scaling by height then target image maintains aspect ratio") {
        val scaled = image.scaleToHeight(400)
        assert(scaled.height === 400)
        assert(scaled.ratio - image.ratio < 0.01)
    }

    test("when covering an image the output image should have the specified dimensions") {
        val covered = image.cover(51, 66)
        assert(51 === covered.width)
        assert(66 === covered.height)
    }

    test("components returns array of ARGB bytes") {
        val image = Image.filled(20, 20, Color.YELLOW)
        val components = image.argb
        assert(400 === components.size)
        for ( component <- components )
            assert(component === Array[Byte](255.toByte, 255.toByte, 255.toByte, 0))
    }
}
