package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.filter.GrayscaleFilter
import com.sksamuel.scrimage.filter.PipelineFilter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

/**
 * Regression test for PipelineFilter silently dropping its work when
 * applied to a non-INT-ARGB / non-INT-RGB image.
 *
 * Pre-fix the apply() method:
 *
 *   ImmutableImage copy;
 *   if (image.getType() == ARGB || image.getType() == RGB) {
 *      copy = image;
 *   } else {
 *      copy = image.copy(BufferedImage.TYPE_INT_ARGB);
 *   }
 *   for (Filter filter : filters) {
 *      filter.apply(copy);
 *   }
 *
 * For a non-ARGB/RGB image, `copy` was a *separate* ImmutableImage —
 * filters mutated that copy and the original image was returned
 * unchanged. NashvilleFilter (which extends PipelineFilter) on a
 * TYPE_4BYTE_ABGR source did nothing.
 *
 * The fix declares types()=[INT_ARGB, INT_RGB] so
 * ImmutableImage.filter() converts the input before delegating.
 */
class PipelineFilterTypeConversionTest : FunSpec({

   class TestPipeline : PipelineFilter(listOf(GrayscaleFilter()))

   test("pipeline applied to TYPE_4BYTE_ABGR via image.filter() actually changes the image") {
      val source = ImmutableImage.create(4, 4, BufferedImage.TYPE_4BYTE_ABGR)
      // Fill with a strong red so a Grayscale step will produce a noticeable change.
      for (x in 0 until 4) for (y in 0 until 4) {
         source.setColor(x, y, RGBColor(255, 0, 0, 255))
      }
      val originalRed = source.pixel(0, 0).red()
      originalRed shouldBe 255

      val out = source.filter(TestPipeline())
      // After grayscale, red is ~54 (the Rec. 709 luma weight for pure red).
      // The key invariant: it must NOT still be 255 (which would indicate
      // the pipeline silently no-op'd).
      val newRed = out.pixel(0, 0).red()
      newRed shouldBe 54
   }

   test("pipeline declares types() so the framework knows the conversion is needed") {
      val pipeline = TestPipeline()
      val types = pipeline.types().toList()
      types.contains(BufferedImage.TYPE_INT_ARGB) shouldBe true
      types.contains(BufferedImage.TYPE_INT_RGB) shouldBe true
      // size > 0 is the actual contract; before the fix it was empty
      types.size shouldBeGreaterThan 0
   }

   test("pipeline still works correctly on TYPE_INT_ARGB sources (no regression)") {
      val source = ImmutableImage.create(4, 4, BufferedImage.TYPE_INT_ARGB)
      for (x in 0 until 4) for (y in 0 until 4) {
         source.setColor(x, y, RGBColor(0, 255, 0, 255))
      }
      val out = source.filter(TestPipeline())
      out.pixel(0, 0).red() shouldBe 182 // luma of pure green
      out.pixel(0, 0).green() shouldBe 182
      out.pixel(0, 0).blue() shouldBe 182
   }
})
