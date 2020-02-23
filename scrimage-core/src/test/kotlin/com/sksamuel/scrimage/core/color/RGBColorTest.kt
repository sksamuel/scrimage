package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.color.RGBColor
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.awt.Color

class RGBColorTest : StringSpec({

   "an RGB color convert to hex" {
      "FF00FF" shouldBe RGBColor(255, 0, 255).toHex()
      "FFFFFF" shouldBe RGBColor(255, 255, 255).toHex()
      "EFEFEF" shouldBe RGBColor(239, 239, 239).toHex()
      RGBColor(0, 0, 15).toHex() shouldBe "00000F"
      RGBColor(0, 0, 0).toHex() shouldBe "000000"
   }

   "convert to an integer using correct bit shifting"  {
      RGBColor(239, 223, 207).toARGBInt() shouldBe -1056817
      RGBColor(239, 223, 207, 0).toARGBInt() shouldBe 0xEFDFCF
   }

   "convert to awt.color"  {
      Color(255, 0, 255) shouldBe RGBColor(255, 0, 255).toAWT()
      Color(255, 250, 255) shouldBe RGBColor(255, 250, 255).toAWT()
      Color(14, 250, 255) shouldBe RGBColor(14, 250, 255).toAWT()
      Color(255, 0, 0) shouldBe RGBColor(255, 0, 0).toAWT()
   }

   "convert from awt.color"  {
      RGBColor.fromAwt(Color(255, 0, 255)) shouldBe RGBColor(255, 0, 255)
      RGBColor.fromAwt(Color(255, 250, 255)) shouldBe RGBColor(255, 250, 255)
      RGBColor.fromAwt(Color(14, 250, 255)) shouldBe RGBColor(14, 250, 255)
      RGBColor.fromAwt(Color(255, 0, 0)) shouldBe RGBColor(255, 0, 0)
   }

   "convert from int to color using correct bit shifting"  {
      RGBColor.fromARGBInt(1088511) shouldBe RGBColor(16, 155, 255, 0)
   }

})