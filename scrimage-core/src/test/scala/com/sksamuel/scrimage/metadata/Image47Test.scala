package com.sksamuel.scrimage.metadata

import com.sksamuel.scrimage.{ImageMetadata, Image}
import org.scalatest.{Matchers, WordSpec}

class Image47Test extends WordSpec with Matchers {

  import ImageMetadata._

   "cover" should {
     "not rotate image from iphone" in {
       val src = Image.fromResource("/issue47.JPG")
       println(ImageMetadata.fromResource("/issue47.JPG"))
       println(src.metadata.tags)
       src.scale(0.25).output("test.png")
     }
   }
 }
