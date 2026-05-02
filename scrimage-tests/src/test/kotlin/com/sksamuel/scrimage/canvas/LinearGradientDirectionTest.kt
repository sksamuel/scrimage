package com.sksamuel.scrimage.canvas

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.canvas.painters.LinearGradient
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * Regression test for LinearGradient.horizontal / vertical names being
 * swapped relative to what they actually produce.
 *
 * Pre-fix:
 *
 *   horizontal(c1, c2) → new LinearGradient(0, MIN, c1, 0, MAX, c2)
 *   vertical(c1, c2)   → new LinearGradient(MIN, 0, c1, MAX, 0, c2)
 *
 * The first form has equal x coordinates and varying y, which is a
 * VERTICAL gradient (color varies along y). The second has equal y
 * and varying x, which is a HORIZONTAL gradient. Names and outputs
 * were inverted.
 *
 * The fix swaps the factory bodies so each name matches its visual
 * effect.
 */
class LinearGradientDirectionTest : FunSpec({

   test("horizontal(red, blue) varies left-to-right (red on left, blue on right)") {
      val image = ImmutableImage.create(20, 20, BufferedImage.TYPE_INT_ARGB)
         .fill(LinearGradient.horizontal(Color.RED, Color.BLUE))
      val midRowY = 10
      val left = image.pixel(0, midRowY)
      val right = image.pixel(19, midRowY)
      // red dominates on the left, blue dominates on the right
      left.red() shouldBeGreaterThan right.red()
      right.blue() shouldBeGreaterThan left.blue()
      // and at the very left edge the colour is exactly red (gradient anchored at x=0)
      left.red() shouldBe 255
      left.blue() shouldBe 0
   }

   test("vertical(red, blue) varies top-to-bottom (red on top, blue at bottom)") {
      val image = ImmutableImage.create(20, 20, BufferedImage.TYPE_INT_ARGB)
         .fill(LinearGradient.vertical(Color.RED, Color.BLUE))
      val midColX = 10
      val top = image.pixel(midColX, 0)
      val bottom = image.pixel(midColX, 19)
      // red dominates at the top, blue dominates at the bottom
      top.red() shouldBeGreaterThan bottom.red()
      bottom.blue() shouldBeGreaterThan top.blue()
      // and at the very top the colour is exactly red (gradient anchored at y=0)
      top.red() shouldBe 255
      top.blue() shouldBe 0
   }

   test("horizontal gradient is constant along columns") {
      val image = ImmutableImage.create(20, 20, BufferedImage.TYPE_INT_ARGB)
         .fill(LinearGradient.horizontal(Color.RED, Color.BLUE))
      val col5Top = image.pixel(5, 0)
      val col5Bottom = image.pixel(5, 19)
      // Same column → same colour at top and bottom
      col5Top.argb shouldBe col5Bottom.argb
   }

   test("vertical gradient is constant along rows") {
      val image = ImmutableImage.create(20, 20, BufferedImage.TYPE_INT_ARGB)
         .fill(LinearGradient.vertical(Color.RED, Color.BLUE))
      val row5Left = image.pixel(0, 5)
      val row5Right = image.pixel(19, 5)
      // Same row → same colour on left and right
      row5Left.argb shouldBe row5Right.argb
   }
})
