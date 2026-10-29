package com.sksamuel.scrimage.core.composite

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.ScaleMethod
import com.sksamuel.scrimage.composite.Composite
import com.sksamuel.scrimage.composite.DifferenceComposite
import com.sksamuel.scrimage.composite.MultiplyComposite
import com.sksamuel.scrimage.composite.RedComposite
import com.sksamuel.scrimage.composite.ScreenComposite
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.random.Random

/**
 * The BlendComposite based composites read the rasters as packed INT_ARGB. Previously
 * ImmutableImage.composite threw for byte backed images (every loaded JPEG / PNG), did
 * nothing for an INT_RGB / INT_BGR overlay, swapped red and blue on an INT_BGR base and
 * misread premultiplied images. Every image type must now give the same result as the
 * TYPE_INT_ARGB composite, stored in the base image's own type.
 */
class BlendCompositeImageTypesTest : FunSpec({

   val w = 8
   val h = 6

   fun pixels(seed: Int, translucent: Boolean): IntArray {
      val r = Random(seed)
      return IntArray(w * h) {
         val a = if (translucent && it % 2 == 1) 128 else 255
         (a shl 24) or (r.nextInt(256) shl 16) or (r.nextInt(256) shl 8) or r.nextInt(256)
      }
   }

   fun image(type: Int, argb: IntArray): ImmutableImage {
      val bi = BufferedImage(w, h, type)
      bi.setRGB(0, 0, w, h, argb, 0, w)
      return ImmutableImage.wrapAwt(bi)
   }

   fun rgb(image: ImmutableImage) = image.awt().getRGB(0, 0, image.width, image.height, null, 0, image.width)

   // what an image of the given type stores for these ARGB values
   fun stored(type: Int, argb: IntArray) = rgb(image(type, argb))

   val composites = listOf<Composite>(
      MultiplyComposite(1.0), ScreenComposite(0.5), DifferenceComposite(1.0), RedComposite(1.0)
   )

   val types = listOf(
      BufferedImage.TYPE_INT_ARGB,
      BufferedImage.TYPE_INT_ARGB_PRE,
      BufferedImage.TYPE_INT_RGB,
      BufferedImage.TYPE_INT_BGR,
      BufferedImage.TYPE_3BYTE_BGR,
      BufferedImage.TYPE_4BYTE_ABGR,
      BufferedImage.TYPE_4BYTE_ABGR_PRE,
   )

   val basePixels = pixels(1, false)
   // premultiplied types can't hold a translucent colour exactly, so only give them opaque pixels
   fun overlayPixels(type: Int) = pixels(2, !BufferedImage(1, 1, type).isAlphaPremultiplied)

   test("blend composites give the TYPE_INT_ARGB result for every base and overlay type") {
      for (composite in composites) {
         for (baseType in types) {
            for (overlayType in types) {
               val overlay = overlayPixels(overlayType)
               // the reference uses what the overlay type actually holds (e.g. INT_RGB drops alpha)
               val overlayHeld = stored(overlayType, overlay)
               val expected = stored(
                  baseType,
                  rgb(image(BufferedImage.TYPE_INT_ARGB, basePixels).composite(composite, image(BufferedImage.TYPE_INT_ARGB, overlayHeld)))
               )
               val result = image(baseType, basePixels).composite(composite, image(overlayType, overlay))
               result.type shouldBe baseType
               withClue("${composite.javaClass.simpleName} base=$baseType overlay=$overlayType") {
                  rgb(result).toList() shouldBe expected.toList()
               }
            }
         }
      }
   }

   test("composite of two loaded JPEGs does not throw") {
      val jpeg = ImmutableImage.loader().fromResource("/chinese_lantern.jpeg")
      jpeg.type shouldBe BufferedImage.TYPE_3BYTE_BGR
      val result = jpeg.composite(MultiplyComposite(1.0), jpeg)
      result.type shouldBe BufferedImage.TYPE_3BYTE_BGR
      result.width shouldBe jpeg.width
   }

   test("composite with a loaded RGBA PNG overlay does not throw") {
      val png = ImmutableImage.loader().fromResource("/transparent_chip.png")
      png.type shouldBe BufferedImage.TYPE_4BYTE_ABGR
      val base = ImmutableImage.filled(png.width, png.height, Color.WHITE)
      base.composite(MultiplyComposite(1.0), png).width shouldBe png.width
   }

   test("composite with a Progressive scaled (TYPE_INT_RGB) overlay is not a no-op") {
      val overlay = ImmutableImage.filled(80, 80, Color(200, 150, 100), BufferedImage.TYPE_INT_RGB)
         .scaleTo(40, 40, ScaleMethod.Progressive)
      overlay.type shouldBe BufferedImage.TYPE_INT_RGB
      val base = ImmutableImage.filled(40, 40, Color(10, 20, 30))
      val p = base.composite(RedComposite(1.0), overlay).pixel(5, 5)
      listOf(p.red(), p.green(), p.blue()) shouldBe listOf(200, 20, 30)
   }

   test("composite onto a TYPE_INT_BGR base keeps red and blue in place") {
      val base = ImmutableImage.filled(4, 4, Color(10, 20, 30), BufferedImage.TYPE_INT_BGR)
      val overlay = ImmutableImage.filled(4, 4, Color(200, 150, 100))
      val p = base.composite(RedComposite(1.0), overlay).pixel(1, 1)
      listOf(p.red(), p.green(), p.blue()) shouldBe listOf(200, 20, 30)
   }

   test("composite onto a smaller overlay leaves pixels outside the overlay unchanged") {
      val base = ImmutableImage.filled(8, 8, Color(10, 20, 30), BufferedImage.TYPE_3BYTE_BGR)
      val overlay = ImmutableImage.filled(4, 4, Color(200, 150, 100), BufferedImage.TYPE_3BYTE_BGR)
      val result = base.composite(RedComposite(1.0), overlay)
      result.pixel(6, 6).toARGBInt() shouldBe Color(10, 20, 30).rgb
      result.pixel(1, 1).red() shouldBe 200
   }
})
