package com.sksamuel.scrimage

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import java.awt.Color

/** @author Stephen Samuel */
class PixelToolsTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val white = 0xFFFFFFFF

    test("alpha component") {
        assert(white === Color.WHITE.getRGB)
        assert(PixelTools.alpha(white) === Color.WHITE.getAlpha.toByte)
    }

    test("red component") {
        assert(white === Color.WHITE.getRGB)
        assert(PixelTools.red(white) === Color.RED.getRed.toByte)
    }

    test("blue component") {
        assert(white === Color.WHITE.getRGB)
        assert(PixelTools.blue(white) === Color.BLUE.getBlue.toByte)
    }

    test("green component") {
        assert(white === Color.WHITE.getRGB)
        assert(PixelTools.green(white) === Color.GREEN.getGreen.toByte)
    }
}
