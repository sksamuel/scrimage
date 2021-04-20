package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.Dimension
import com.sksamuel.scrimage.DimensionTools
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DimensionToolsTest : FunSpec({

   test("when given a vertical box then it is scaled to fit") {
      val fitted = DimensionTools.dimensionsToFit(Dimension(500, 500), Dimension(500, 2000))
      fitted shouldBe Dimension(125, 500)
   }

   test("when given a horizontal box then it is scaled to fit") {
      val fitted = DimensionTools.dimensionsToFit(Dimension(500, 500), Dimension(1000, 500))
      fitted shouldBe Dimension(500, 250)
   }

   test("when given a box that is already the same size as the target then the same dimensions are returned") {
      val fitted = DimensionTools.dimensionsToFit(Dimension(333, 333), Dimension(333, 333))
      fitted shouldBe Dimension(333, 333)
   }

   test("when scaling the constraining dimension should clamp without rounding errors") {
      val fitted = DimensionTools.dimensionsToFit(Dimension(100, 100), Dimension(50, 97))
      fitted.y shouldBe 100
   }

   test("when scaling a square then both dimensions should scale equally without rounding errors") {
      val fitted = DimensionTools.dimensionsToFit(Dimension(100, 100), Dimension(97, 97))
      fitted shouldBe Dimension(100, 100)
   }

   test("when covering an area that is the same size as the source then the same dimensions are returned") {
      val covered = DimensionTools.dimensionsToCover(Dimension(333, 333), Dimension(333, 333))
      covered shouldBe Dimension(333, 333)
   }

   test("when covering an area that is wider than the source then the dimensions have the larger width") {
      val covered = DimensionTools.dimensionsToCover(Dimension(600, 40), Dimension(400, 40))
      covered shouldBe Dimension(600, 60)
   }

   test("when covering an area that is taller than the source then the dimensions have the larger height") {
      val covered = DimensionTools.dimensionsToCover(Dimension(400, 800), Dimension(400, 400))
      covered shouldBe Dimension(800, 800)
   }

   test("when covering an area that is wider and taller than the aspect ratio is maintained") {
      val covered = DimensionTools.dimensionsToCover(Dimension(106, 120), Dimension(80, 100))
      Dimension(106, 133) shouldBe covered
   }

   test("when covering an area that is smaller the dimensions reduce to match") {
      val covered = DimensionTools.dimensionsToCover(Dimension(145, 300), Dimension(657, 526))
      Dimension(375, 300) shouldBe covered
   }

})