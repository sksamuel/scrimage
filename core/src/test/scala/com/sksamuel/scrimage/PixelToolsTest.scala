package com.sksamuel.scrimage

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import java.awt.Color

/** @author Stephen Samuel */
class PixelToolsTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val white = 0xFFFFFFFF
    val yellow = 0xFFFFFF00
    val pink = 0xFFFFAFAF

    test("non transparent alpha component") {
        assert(white === Color.WHITE.getRGB)
        assert(PixelTools.alpha(white) === 255)
    }

    test("transparent alpha component") {
        assert(PixelTools.alpha(0xDD001122) === 221)
    }

    test("red component") {
        assert(pink === Color.PINK.getRGB)
        assert(PixelTools.red(yellow) === 255)
        assert(PixelTools.red(pink) === 255)
    }

    test("blue component") {
        assert(pink === Color.PINK.getRGB)
        assert(PixelTools.blue(yellow) === 0)
        assert(PixelTools.blue(pink) === 175)
    }

    test("green component") {
        assert(yellow === Color.YELLOW.getRGB)
        assert(PixelTools.green(yellow) === 255)
        assert(PixelTools.green(pink) === 175)
    }

    test("rgb combination") {
        assert(0xFFFF00FF === PixelTools.rgb(255, 0, 255))
        assert(0xFF556677 === PixelTools.rgb(85, 102, 119))
    }

}
