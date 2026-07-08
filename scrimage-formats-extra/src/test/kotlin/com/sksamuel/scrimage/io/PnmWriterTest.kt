package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PnmWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage

class PnmWriterTest : FunSpec({

   test("PnmWriter round-trips a non-alpha image") {
      val original = ImmutableImage.loader().fromStream(javaClass.getResourceAsStream("/picard.jpeg"))
         .scaleTo(100, 100)
      val actual = ImmutableImage.loader().fromBytes(original.bytes(PnmWriter()))
      actual.width shouldBe original.width
      actual.height shouldBe original.height
   }

   // PNM has no alpha channel and the TwelveMonkeys encoder used to throw an undeclared
   // IllegalArgumentException ("Unknown TupleType for BufferedImage") for alpha types,
   // including scrimage's default TYPE_INT_ARGB. PnmWriter must flatten alpha first.
   test("PnmWriter supports images with an alpha channel") {
      val argb = ImmutableImage.create(40, 30).fill(java.awt.Color.RED)
      argb.awt().type shouldBe BufferedImage.TYPE_INT_ARGB
      val actual = ImmutableImage.loader().fromBytes(argb.bytes(PnmWriter()))
      actual.width shouldBe 40
      actual.height shouldBe 30
      actual.pixel(20, 15).toARGBInt() shouldBe 0xFFFF0000.toInt()
   }

   test("PnmWriter composites transparent pixels onto white") {
      val transparent = ImmutableImage.create(10, 10) // fully transparent ARGB
      val actual = ImmutableImage.loader().fromBytes(transparent.bytes(PnmWriter()))
      actual.pixel(5, 5).toARGBInt() shouldBe 0xFFFFFFFF.toInt()
   }
})
