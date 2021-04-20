@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class Issue125Test : WordSpec({

   val jpeg: BufferedImage = ImageIO.read(javaClass.getResourceAsStream("/issue125.jpg"))

   "Image written with ImageIO" should {
      "not swap color channels"  {

         val out1 = ByteArrayOutputStream()
         ImageIO.write(jpeg, "jpg", out1)

         val converted = ImmutableImage.fromAwt(jpeg).awt()
         val out2 = ByteArrayOutputStream()
         ImageIO.write(converted, "jpg", out2)

         out1.toByteArray() shouldBe out2.toByteArray()
      }
   }

})
