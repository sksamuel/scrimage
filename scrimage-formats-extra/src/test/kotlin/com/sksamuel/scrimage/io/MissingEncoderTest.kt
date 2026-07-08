package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.PcxWriter
import com.sksamuel.scrimage.nio.SgiWriter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import java.io.IOException

// The TwelveMonkeys PCX and SGI plugins are read-only (they register no ImageWriterSpi),
// so no encoder exists for these formats. Writing used to fail with a bare
// NoSuchElementException from Iterator.next(); it should be a descriptive IOException.
class MissingEncoderTest : FunSpec({

   val image = ImmutableImage.create(40, 30)

   test("PcxWriter reports a descriptive error when no PCX encoder is available") {
      val e = shouldThrow<IOException> {
         image.bytes(PcxWriter())
      }
      e.message.shouldContain("pcx")
   }

   test("SgiWriter reports a descriptive error when no SGI encoder is available") {
      val e = shouldThrow<IOException> {
         image.bytes(SgiWriter())
      }
      e.message.shouldContain("sgi")
   }
})
