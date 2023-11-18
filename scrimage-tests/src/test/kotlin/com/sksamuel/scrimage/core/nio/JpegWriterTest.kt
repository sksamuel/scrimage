@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.metadata.Tag
import com.sksamuel.scrimage.nio.JpegWriter
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainAll

class JpegWriterTest : FunSpec({

   val original = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird.jpg").scaleTo(600, 400)

   test("jpeg compression happy path") {
      repeat(10) { k ->
         original.bytes(JpegWriter().withCompression(k * 10)) // make sure no exceptions for each format level
      }
   }

   test("jpeg writing with alpha #84") {
      val img = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/nio/issue84.jpg")
      val w = JpegWriter()
      img.bytes(w) // was throwing with bug
   }

   test("!jpeg writer should write metadata") {
      val img = ImmutableImage.loader().fromResource("/vossen.jpg")
      val file = img.output(JpegWriter.Default, "metadatatest.jpg")
      val tags = ImmutableImage.loader().fromPath(file).metadata.tags().toSet()
      tags.shouldContainAll(
         Tag("a", -3, "0", "foo"),
         Tag("b", 0, "8", "boo")
      )
   }
})
