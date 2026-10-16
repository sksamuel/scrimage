package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.image.BufferedImage

/**
 * Regression: UnsharpFilter's edgeAction parameter was mapped onto the jhlabs op but the jhlabs
 * UnsharpFilter hard-coded CLAMP_EDGES in both of its convolution passes, so ZeroEdges, WrapEdges
 * and ClampEdges all produced identical output. The jhlabs op now honours the configured edge
 * action, and the wrapper's no-arg default is ClampEdges so the default output is what it has
 * always effectively been.
 */
class UnsharpFilterEdgeActionTest : FunSpec({

   val src = ImmutableImage.loader().fromResource("/bird.jpg").scaleTo(120, 80).copy(BufferedImage.TYPE_INT_ARGB)

   fun border(image: ImmutableImage): List<Int> =
      image.pixels().filter { it.x() == 0 || it.y() == 0 || it.x() == image.width - 1 || it.y() == image.height - 1 }.map { it.toARGBInt() }

   fun interior(image: ImmutableImage): List<Int> =
      image.pixels().filter { it.x() in 6 until image.width - 6 && it.y() in 6 until image.height - 6 }.map { it.toARGBInt() }

   val zero = src.filter(UnsharpFilter(2f, 0, EdgeAction.ZeroEdges))
   val wrap = src.filter(UnsharpFilter(2f, 0, EdgeAction.WrapEdges))
   val clamp = src.filter(UnsharpFilter(2f, 0, EdgeAction.ClampEdges))

   test("ZeroEdges and ClampEdges differ at the border") {
      border(zero) shouldNotBe border(clamp)
   }

   test("WrapEdges and ClampEdges differ at the border") {
      border(wrap) shouldNotBe border(clamp)
   }

   test("ZeroEdges and WrapEdges differ at the border") {
      border(zero) shouldNotBe border(wrap)
   }

   test("the edge action does not affect pixels far from the border") {
      interior(zero) shouldBe interior(clamp)
      interior(wrap) shouldBe interior(clamp)
   }

   test("the no-arg constructor produces the same output as explicit ClampEdges") {
      src.filter(UnsharpFilter()) shouldBe src.filter(UnsharpFilter(0.5f, 1, EdgeAction.ClampEdges))
   }
})
