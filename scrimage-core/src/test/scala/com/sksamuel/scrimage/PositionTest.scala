package com.sksamuel.scrimage

import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class PositionTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  test("centre positions image in the centre of the canvas") {
    assert(20 === Position.Center.calculateX(100, 100, 60, 80))
    assert(10 === Position.Center.calculateY(100, 100, 60, 80))
  }

  test("centre left positions image at (0,center) of the canvas") {
    assert(0 === Position.CenterLeft.calculateX(100, 100, 60, 80))
    assert(10 === Position.CenterLeft.calculateY(100, 100, 60, 80))
  }

  test("centre right positions image at (image-width,centre) of the canvas") {
    assert(40 === Position.CenterRight.calculateX(100, 100, 60, 80))
    assert(10 === Position.CenterRight.calculateY(100, 100, 60, 80))
  }

  test("top left positions image at (0,0) of the canvas") {
    assert(0 === Position.TopLeft.calculateX(100, 100, 60, 80))
    assert(0 === Position.TopLeft.calculateY(100, 100, 60, 80))
  }

  test("top center positions image at (center,top) of the canvas") {
    assert(20 === Position.TopCenter.calculateX(100, 100, 60, 80))
    assert(0 === Position.TopCenter.calculateY(100, 100, 60, 80))
  }

  test("top right positions image at (image-width,0) of the canvas") {
    assert(40 === Position.TopRight.calculateX(100, 100, 60, 80))
    assert(0 === Position.TopRight.calculateY(100, 100, 60, 80))
  }

  test("bottom left positions image at (0,0) of the canvas") {
    assert(0 === Position.BottomLeft.calculateX(100, 100, 60, 80))
    assert(20 === Position.BottomLeft.calculateY(100, 100, 60, 80))
  }

  test("bottom center positions image at (center,bottom) of the canvas") {
    assert(20 === Position.BottomCenter.calculateX(100, 100, 60, 80))
    assert(20 === Position.BottomCenter.calculateY(100, 100, 60, 80))
  }

  test("bottom right positions image at (right,centre) of the canvas") {
    assert(40 === Position.BottomRight.calculateX(100, 100, 60, 80))
    assert(20 === Position.BottomRight.calculateY(100, 100, 60, 80))
  }
}
