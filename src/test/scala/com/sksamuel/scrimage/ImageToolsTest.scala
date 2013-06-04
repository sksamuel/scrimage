package com.sksamuel.scrimage

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import java.awt.image.BufferedImage

/** @author Stephen Samuel */
class ImageToolsTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    test("when writing out an image then the array is the right length") {
        val image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_BGR)
        val actual = ImageTools.toBytes(image, "PNG")
        assert(actual.length === 138)
    }

    test("when normalizing dimensions with width 0 then the aspect ratio is used") {
        val image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB)
        val size = ImageTools.normalizeSize(image, (0, 100))
        assert(size === (50, 100))
    }

    test("when normalizing dimensions with height 0 then the aspect ratio is used") {
        val image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB)
        val size = ImageTools.normalizeSize(image, (150, 0))
        assert(size === (150, 100))
    }

    test("when normalizing dimensions with width and height 0 then 0,0 is returned") {
        val image = new BufferedImage(300, 200, BufferedImage.TYPE_INT_RGB)
        val size = ImageTools.normalizeSize(image, (0, 0))
        assert(size === (0, 0))
    }

    test("when given a vertical box then it is scaled to fit") {
        val fitted = ImageTools.dimensionsToFit((500, 500), (500, 2000))
        assert(fitted === (125, 500))
    }

    test("when given a horizontal box then it is scaled to fit") {
        val fitted = ImageTools.dimensionsToFit((500, 500), (1000, 500))
        assert(fitted === (500, 250))
    }

    test("when given the same dimensions the ratio returns 1") {
        val ratio = ImageTools.ratio(5, 5)
        assert(ratio === 1)
    }

    test("when given some dimensions the correct ratio is returned") {
        val ratio = ImageTools.ratio(10, 5)
        assert(ratio === 2)

        val ratio2 = ImageTools.ratio(5, 20)
        assert(ratio2 === 0.25)
    }

    test("when given a zero dimension the ratio returns zero") {
        val ratio = ImageTools.ratio(5, 0)
        assert(ratio === 0)

        val ratio2 = ImageTools.ratio(0, 5)
        assert(ratio2 === 0)
    }

    test("when given a box that is already the same size as the target then the same dimensions are returned") {
        val fitted = ImageTools.dimensionsToFit((333, 333), (333, 333))
        assert(fitted === (333, 333))
    }

    test("when given a .gif file then image/gif is detected for the content type") {
        val contentType = ImageTools.contentType("coldplay.gif")
        assert(contentType === "image/gif")
    }

    test("when given a .png file then image/png is detected for the content type") {
        val contentType = ImageTools.contentType("coldplay.png")
        assert(contentType === "image/png")
    }

    test("when given a .jpeg file then image/jpeg is detected for the content type") {
        val contentType = ImageTools.contentType("coldplay.jpeg")
        assert(contentType === "image/jpeg")
    }

    test("when given a .jpg file then image/jpeg is detected for the content type") {
        val contentType = ImageTools.contentType("coldplay.jpg")
        assert(contentType === "image/jpg")
    }

    test("when given a non image file then the content type is detected from the filename") {
        assert(ImageTools.contentType("coldplay.pdf") === "application/pdf")
        assert(ImageTools.contentType("coldplay.doc") === "application/octet-stream")
        assert(ImageTools.contentType("coldplay.txt") === "text/plain")
    }

    test("when resizing an image then the resultant image is the expected size") {
        val image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB)
        val actual = ImageTools.resize(image, (50, 80))
        assert(actual.getWidth === 50)
        assert(actual.getHeight === 80)
    }

    test("when fitting an image then the resultant image is the expected size") {
        val image = new BufferedImage(100, 200, BufferedImage.TYPE_INT_RGB)
        val actual = ImageTools.fit(image, (50, 80))
        assert(actual.getWidth === 50)
        assert(actual.getHeight === 80)
    }
}

