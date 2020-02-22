package com.sksamuel.scrimage

import com.sksamuel.scrimage.pixels.PixelTools
import org.scalatest.{FunSuite, Matchers}

class PixelToolsTest extends FunSuite with Matchers {

  val white = 0xFFFFFFFF
  val yellow = 0xFFFFFF00
  val pink = 0xFFFFAFAF

  test("non transparent alpha component") {
    assert(white === java.awt.Color.WHITE.getRGB)
    assert(PixelTools.alpha(white) === 255)
  }

  test("transparent alpha component") {
    assert(PixelTools.alpha(0xDD001122) === 221)
  }

  test("red component") {
    assert(pink === java.awt.Color.PINK.getRGB)
    assert(PixelTools.red(yellow) === 255)
    assert(PixelTools.red(pink) === 255)
  }

  test("blue component") {
    assert(pink === java.awt.Color.PINK.getRGB)
    assert(PixelTools.blue(yellow) === 0)
    assert(PixelTools.blue(pink) === 175)
  }

  test("green component") {
    assert(yellow === java.awt.Color.YELLOW.getRGB)
    assert(PixelTools.green(yellow) === 255)
    assert(PixelTools.green(pink) === 175)
  }

  test("rgb combination") {
    assert(0xFFFF00FF === PixelTools.rgb(255, 0, 255))
    assert(0xFF556677 === PixelTools.rgb(85, 102, 119))
  }

  test("coordinate to offset") {
    assert(160 === PixelTools.coordinateToOffset(10, 3, 50))
    assert(10 === PixelTools.coordinateToOffset(10, 0, 50))
    assert(99 === PixelTools.coordinateToOffset(49, 1, 50))
  }

  test("offset to coordinate") {
    PixelTools.offsetToCoordinate(0, 100) shouldBe new Coordinate(0, 0)
    PixelTools.offsetToCoordinate(100, 100) shouldBe new Coordinate(0, 1)
    PixelTools.offsetToCoordinate(99, 100) shouldBe new Coordinate(99, 0)
    PixelTools.offsetToCoordinate(199, 100) shouldBe new Coordinate(99, 1)
    PixelTools.offsetToCoordinate(101, 100) shouldBe new Coordinate(1, 1)
  }
}
