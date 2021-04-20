@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.metadata

import com.sksamuel.scrimage.metadata.ImageMetadata
import com.sksamuel.scrimage.metadata.Tag
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContain

class ImageMetadataTest : WordSpec({

   val stream = javaClass.getResourceAsStream("/vossen.jpg")

   "metadata" should {
      "read EXIF" {
         val meta = ImageMetadata.fromStream(stream)
         meta.tags().toList().shouldContain(
            Tag(
               "ISO Speed Ratings",
               34855,
               "2500",
               "2500"
            )
         )
         meta.tags().toList().shouldContain(
            Tag(
               "Image Width",
               256,
               "4928",
               "4928 pixels"
            )
         )
         meta.tags().toList().shouldContain(
            Tag(
               "White Balance Mode",
               41987,
               "0",
               "Auto white balance"
            )
         )
      }
   }

})
