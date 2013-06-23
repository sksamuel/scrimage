package com.sksamuel.scrimage

import org.scalatest.{OneInstancePerTest, BeforeAndAfter, FunSuite}
import com.sksamuel.scrimage.Position._

/** @author Stephen Samuel */
class PositionTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

    test("centre positions image in the centre of the canvas") {
        assert(20 === Center.calculateX(100, 100, 60, 80))
        assert(10 === Center.calculateY(100, 100, 60, 80))
    }

    test("centre left positions image at (0,center) of the canvas") {
        assert(0 === CenterLeft.calculateX(100, 100, 60, 80))
        assert(10 === CenterLeft.calculateY(100, 100, 60, 80))
    }

    test("centre right positions image at (image-width,centre) of the canvas") {
        assert(40 === CenterRight.calculateX(100, 100, 60, 80))
        assert(10 === CenterRight.calculateY(100, 100, 60, 80))
    }

    test("top left positions image at (0,0) of the canvas") {
        assert(0 === TopLeft.calculateX(100, 100, 60, 80))
        assert(0 === TopLeft.calculateY(100, 100, 60, 80))
    }

    test("top center positions image at (center,top) of the canvas") {
        assert(20 === TopCenter.calculateX(100, 100, 60, 80))
        assert(0 === TopCenter.calculateY(100, 100, 60, 80))
    }

    test("top right positions image at (image-width,0) of the canvas") {
        assert(40 === TopRight.calculateX(100, 100, 60, 80))
        assert(0 === TopRight.calculateY(100, 100, 60, 80))
    }

    test("bottom left positions image at (0,0) of the canvas") {
        assert(0 === BottomLeft.calculateX(100, 100, 60, 80))
        assert(20 === BottomLeft.calculateY(100, 100, 60, 80))
    }

    test("bottom center positions image at (center,bottom) of the canvas") {
        assert(20 === BottomCenter.calculateX(100, 100, 60, 80))
        assert(20 === BottomCenter.calculateY(100, 100, 60, 80))
    }

    test("bottom right positions image at (right,centre) of the canvas") {
        assert(40 === BottomRight.calculateX(100, 100, 60, 80))
        assert(20 === BottomRight.calculateY(100, 100, 60, 80))
    }
}
