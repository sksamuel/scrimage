package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.filter.Filter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Pin-down tests for ImmutableImage.filter(Filter)'s type-dispatch logic
 * after the de-boxing rewrite. The new implementation iterates the int[]
 * returned by Filter.types() directly instead of materialising a List<Integer>.
 *
 * Three cases must hold:
 *  1. types() == [] → use current type
 *  2. types() contains current type → use current type
 *  3. types() does NOT contain current type → convert to types[0]
 */
class FilterTypesDispatchTest : FunSpec({

   class TypeRecordingFilter(private val supported: IntArray) : Filter {
      var observedType: Int = -1
      override fun types(): IntArray = supported
      override fun apply(image: ImmutableImage) {
         observedType = image.awt().type
      }
   }

   test("filter with empty types() runs against the current image type") {
      val image = ImmutableImage.create(2, 2, BufferedImage.TYPE_INT_ARGB)
      val f = TypeRecordingFilter(intArrayOf())
      image.filter(f)
      f.observedType shouldBe BufferedImage.TYPE_INT_ARGB
   }

   test("filter with matching type in types() runs against the current image type") {
      val image = ImmutableImage.create(2, 2, BufferedImage.TYPE_INT_ARGB)
      val f = TypeRecordingFilter(intArrayOf(BufferedImage.TYPE_INT_RGB, BufferedImage.TYPE_INT_ARGB))
      image.filter(f)
      f.observedType shouldBe BufferedImage.TYPE_INT_ARGB
   }

   test("filter with non-matching types() converts the image to types[0]") {
      val image = ImmutableImage.create(2, 2, BufferedImage.TYPE_INT_ARGB)
      val f = TypeRecordingFilter(intArrayOf(BufferedImage.TYPE_INT_RGB, BufferedImage.TYPE_3BYTE_BGR))
      image.filter(f)
      f.observedType shouldBe BufferedImage.TYPE_INT_RGB
   }

   test("filter returns a copy: original image is unchanged") {
      val original = ImmutableImage.create(2, 2, BufferedImage.TYPE_INT_ARGB)
      original.setColor(0, 0, RGBColor(255, 0, 0, 255))
      val mutating = object : Filter {
         override fun apply(image: ImmutableImage) {
            image.setColor(0, 0, RGBColor(0, 255, 0, 255))
         }
      }
      val out = original.filter(mutating)
      // original untouched
      original.pixel(0, 0).red() shouldBe 255
      // result reflects the filter's mutation
      out.pixel(0, 0).green() shouldBe 255
   }
})
