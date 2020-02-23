@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.Dimension
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.pixels.Pixel
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.awt.Color
import java.awt.image.BufferedImage

class ImageTest : FunSpec({

   val image = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")
   val small = ImmutableImage.fromResource("/bird_small.png")

   test("copy uses a new backing image") {
      val copy = image.copy()
      copy.awt().hashCode() shouldNotBe image.awt().hashCode()
   }

   test("dimensions happy path") {
      val awt = BufferedImage(200, 400, BufferedImage.TYPE_INT_ARGB)
      Dimension(200, 400) shouldBe ImmutableImage.fromAwt(awt).dimensions()
   }

   test("when creating a filled copy then the dimensions are the same as the original") {
      val copy1 = image.fill(Color.RED)
      copy1.width shouldBe 1944
      copy1.height shouldBe 1296

      val copy2 = image.fill(Color(0x00FF00FF))
      1944 shouldBe copy2.width
      1296 shouldBe copy2.height

      val copy3 = image.fill(Color.WHITE)
      1944 shouldBe copy3.width
      1296 shouldBe copy3.height
   }

   test("hashCode and equals reflects proper object equality") {
      val bird = ImmutableImage.fromResource("/com/sksamuel/scrimage/bird.jpg")

      image.hashCode() shouldBe bird.hashCode()
      image shouldBe bird

      val otherImage = ImmutableImage.fromAwt(BufferedImage(445, 464, ImmutableImage.CANONICAL_DATA_TYPE))
      otherImage.hashCode() shouldNotBe image.hashCode()
      otherImage shouldNotBe image
   }

   test("brightness happy path") {
      val brightened = small.brightness(1.5)
      brightened shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_small_brightened.png")
   }

   test("contrast happy path") {
      val contrasted = small.contrast(3.0)
      contrasted shouldBe ImmutableImage.fromResource("/com/sksamuel/scrimage/bird_small_contrasted.png")
   }

   test("foreach loops on each pixel") {
      val i = ImmutableImage.create(100, 100)
      var count = 0
      i.forEach { count++ }
      count shouldBe 10000
   }

   test("map modifies each pixel and returns image") {
      val i = ImmutableImage.create(100, 100)
      val mapped = i.map { p -> Pixel(p.x, p.y, 255, 0, 255, 0) }
      mapped.argb().forAll {
         it shouldBe intArrayOf(0, 255, 0, 255)
      }
   }

   test("column") {

      val red = RGBColor(255, 0, 0).toARGBInt()
      val blue = RGBColor(0, 0, 255).toARGBInt()

      val striped = ImmutableImage.create(200, 100).map { p ->
         if (p.y % 2 == 0) Pixel(p.x, p.y, red) else Pixel(p.x, p.y, blue)
      }
      val col = striped.col(51)
      striped.height shouldBe col.size
      col.forAll { if (it.y % 2 == 0) it.argb shouldBe red else it.argb shouldBe blue }
   }

   test("row") {

      val red = RGBColor(255, 0, 0).toARGBInt()
      val blue = RGBColor(0, 0, 255).toARGBInt()

      val striped = ImmutableImage.create(200, 100).map { p ->
         if (p.x % 2 == 0) Pixel(p.x, p.y, red) else Pixel(p.x, p.y, blue)
      }

      val row = striped.row(44)
      striped.width shouldBe row.size
      row.forAll { if (it.x % 2 == 0) it.argb shouldBe red else it.argb shouldBe blue }
   }


   test("removeTransparency conserve pixels on non transparent image") {
      val i = ImmutableImage.fromResource("/com/sksamuel/scrimage/jazz.jpg")
      val withoutTransparency = i.removeTransparency(Color.BLACK)
      val pixels = i.pixels()
      val pxwt = withoutTransparency.pixels()
      pixels.size shouldBe pxwt.size
      pixels.toList() shouldBe pxwt.toList()
   }

})
