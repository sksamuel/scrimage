package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage

/**
 * RescaleOp scales the raw premultiplied samples of a premultiplied image and clamps them
 * at 255 instead of at alpha, so brightening a translucent pixel wrapped its colour around
 * (red 200 at alpha 128 times 1.5 came back as 43). brightness() must clamp like it does
 * for straight-alpha images.
 */
class BrightnessPremultipliedTest : FunSpec({

   for (type in listOf(BufferedImage.TYPE_INT_ARGB_PRE, BufferedImage.TYPE_4BYTE_ABGR_PRE)) {

      test("brightness on a translucent premultiplied image (type $type) clamps instead of wrapping") {
         val image = ImmutableImage.filled(4, 4, Color(200, 100, 50, 128), type)
         val result = image.brightness(1.5)
         result.type shouldBe type
         val p = result.pixel(1, 1)
         p.alpha() shouldBe 128
         p.red() shouldBe 255
         // 100 * 1.5 and 50 * 1.5, within premultiplied precision at alpha 128
         p.green().shouldBeBetween(148, 152)
         p.blue().shouldBeBetween(73, 77)
      }

      test("brightness on a premultiplied image (type $type) matches the straight-alpha result") {
         val colours = listOf(Color(200, 100, 50, 128), Color(10, 240, 130, 255), Color(255, 255, 255, 128))
         for (colour in colours) {
            for (factor in listOf(0.5, 1.5, 3.0)) {
               val expected = ImmutableImage.filled(2, 2, colour, BufferedImage.TYPE_INT_ARGB).brightness(factor).pixel(0, 0)
               val actual = ImmutableImage.filled(2, 2, colour, type).brightness(factor).pixel(0, 0)
               actual.alpha() shouldBe expected.alpha()
               actual.red().shouldBeBetween(expected.red() - 2, expected.red() + 2)
               actual.green().shouldBeBetween(expected.green() - 2, expected.green() + 2)
               actual.blue().shouldBeBetween(expected.blue() - 2, expected.blue() + 2)
            }
         }
      }
   }

   test("MutableImage.rescaleInPlace on a translucent premultiplied image clamps instead of wrapping") {
      val image = ImmutableImage.filled(4, 4, Color(200, 100, 50, 128), BufferedImage.TYPE_INT_ARGB_PRE)
      image.rescaleInPlace(1.5)
      image.pixel(1, 1).red() shouldBe 255
   }
})
