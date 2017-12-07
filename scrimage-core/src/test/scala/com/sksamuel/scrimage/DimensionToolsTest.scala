package com.sksamuel.scrimage

import org.scalatest.{BeforeAndAfter, FunSuite, OneInstancePerTest}

class DimensionToolsTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  test("when given a vertical box then it is scaled to fit") {
    val fitted = DimensionTools.dimensionsToFit(new Dimension(500, 500), new Dimension(500, 2000))
    assert(fitted === new Dimension(125, 500))
  }

  test("when given a horizontal box then it is scaled to fit") {
    val fitted = DimensionTools.dimensionsToFit(new Dimension(500, 500), new Dimension(1000, 500))
    assert(fitted === new Dimension(500, 250))
  }

  test("when given a box that is already the same size as the target then the same dimensions are returned") {
    val fitted = DimensionTools.dimensionsToFit(new Dimension(333, 333), new Dimension(333, 333))
    assert(fitted === new Dimension(333, 333))
  }

  test("when covering an area that is the same size as the source then the same dimensions are returned") {
    val covered = DimensionTools.dimensionsToCover(new Dimension(333, 333), new Dimension(333, 333))
    assert(covered === new Dimension(333, 333))
  }

  test("when covering an area that is wider than the source then the dimensions have the larger width") {
    val covered = DimensionTools.dimensionsToCover(new Dimension(600, 40), new Dimension(400, 40))
    assert(covered === new Dimension(600, 60))
  }

  test("when covering an area that is taller than the source then the dimensions have the larger height") {
    val covered = DimensionTools.dimensionsToCover(new Dimension(400, 800), new Dimension(400, 400))
    assert(covered === new Dimension(800, 800))
  }

  test("when covering an area that is wider and taller than the aspect ratio is maintained") {
    val covered = DimensionTools.dimensionsToCover(new Dimension(106, 120), new Dimension(80, 100))
    assert(new Dimension(106, 133) === covered)
  }

  test("when covering an area that is smaller the dimensions reduce to match") {
    val covered = DimensionTools.dimensionsToCover(new Dimension(145, 300), new Dimension(657, 526))
    assert(new Dimension(375, 300) === covered)
  }
}

