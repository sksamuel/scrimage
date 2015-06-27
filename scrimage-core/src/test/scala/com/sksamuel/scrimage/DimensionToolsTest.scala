package com.sksamuel.scrimage

import org.scalatest.{ OneInstancePerTest, BeforeAndAfter, FunSuite }
import java.awt.image.BufferedImage

/** @author Stephen Samuel */
class DimensionToolsTest extends FunSuite with BeforeAndAfter with OneInstancePerTest {

  test("when given a vertical box then it is scaled to fit") {
    val fitted = DimensionTools.dimensionsToFit((500, 500), (500, 2000))
    assert(fitted === (125, 500))
  }

  test("when given a horizontal box then it is scaled to fit") {
    val fitted = DimensionTools.dimensionsToFit((500, 500), (1000, 500))
    assert(fitted === (500, 250))
  }

  test("when given a box that is already the same size as the target then the same dimensions are returned") {
    val fitted = DimensionTools.dimensionsToFit((333, 333), (333, 333))
    assert(fitted === (333, 333))
  }

  test("when covering an area that is the same size as the source then the same dimensions are returned") {
    val covered = DimensionTools.dimensionsToCover((333, 333), (333, 333))
    assert(covered === (333, 333))
  }

  test("when covering an area that is wider than the source then the dimensions have the larger width") {
    val covered = DimensionTools.dimensionsToCover((600, 40), (400, 40))
    assert(covered === (600, 60))
  }

  test("when covering an area that is taller than the source then the dimensions have the larger height") {
    val covered = DimensionTools.dimensionsToCover((400, 800), (400, 400))
    assert(covered === (800, 800))
  }

  test("when covering an area that is wider and taller than the aspect ratio is maintained") {
    val covered = DimensionTools.dimensionsToCover((106, 120), (80, 100))
    assert((106, 132) === covered)
  }

  test("when covering an area that is smaller the dimensions reduce to match") {
    val covered = DimensionTools.dimensionsToCover((145, 300), (657, 526))
    assert((374, 300) === covered)
  }
}

