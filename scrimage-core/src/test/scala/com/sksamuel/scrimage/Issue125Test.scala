package com.sksamuel.scrimage

import java.awt.image.BufferedImage
import java.io.{BufferedOutputStream, ByteArrayInputStream, ByteArrayOutputStream, File}
import javax.imageio.ImageIO

import org.scalatest.{Matchers, WordSpec}

class Issue125Test extends WordSpec with Matchers {
  private val jpeg: BufferedImage = {
    ImageIO.read(getClass.getResourceAsStream("/issue125.jpg"))
  }

  "Image written with ImageIO" should {
//    "not swap color channels" in {
//      {
//        val outDirect = new File(s"tire_direct.jpg")
//        ImageIO.write(jpeg, "jpg", outDirect)
//      }
//      {
//        val outDirect = new File(s"tire_direct.png")
//        ImageIO.write(jpeg, "png", outDirect)
//      }
//      {
//        val outConvert = new File(s"tire_convert.jpg")
//        val converted = Image.awtToScrimage(jpeg).awt
//        ImageIO.write(converted, "jpg", outConvert)
//      }
//      {
//        val outConvert = new File(s"tire_convert.png")
//        val converted = Image.awtToScrimage(jpeg).awt
//        ImageIO.write(converted, "png", outConvert)
//      }
//    }

    "not swap color channels2" in {
      val image = Image.filled(10, 10, Color.apply(10, 100, 200), BufferedImage.TYPE_3BYTE_BGR)

      val converted: Image = {
        Image.awtToScrimage(image.awt)
      }

      image.awt.getType shouldBe converted.awt.getType
    }
  }
}
