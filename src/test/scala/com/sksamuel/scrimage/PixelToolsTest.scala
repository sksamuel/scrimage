package com.sksamuel.scrimage

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import java.awt.Color

/** @author Stephen Samuel */
class PixelToolsTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    val white = 0xFFFFFFFF

    test("non transparent alpha component") {
        assert(white === Color.WHITE.getRGB)
        assert(PixelTools.alpha(white) === 255)
    }

    test("transparent alpha component") {
        assert(PixelTools.alpha(0xDD001122) === 221)
    }

    test("red component") {
        assert(white === Color.WHITE.getRGB)
        assert(PixelTools.red(white) === 255)
    }

    test("blue component") {
        assert(white === Color.WHITE.getRGB)
        assert(PixelTools.blue(white) === 255)
    }

    test("green component") {
        assert(white === Color.WHITE.getRGB)
        assert(PixelTools.green(white) === 255)
    }
}
