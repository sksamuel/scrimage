package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.PngWriter
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

/**
 * Regression: PngWriter deferred compression-level validation to
 * write(), so an invalid level both produced a confusing late
 * IOException AND, when the caller had opened a FileOutputStream
 * before passing it in, leaked that stream until GC.
 *
 * Validate up front in the constructor so the misuse fails fast
 * before any IO resources are allocated by the caller.
 */
class PngWriterValidationTest : FunSpec({

   test("PngWriter rejects compressionLevel == -1 in the constructor") {
      shouldThrow<IllegalArgumentException> { PngWriter(-1) }
   }

   test("PngWriter rejects compressionLevel == 10 in the constructor") {
      shouldThrow<IllegalArgumentException> { PngWriter(10) }
   }

   test("PngWriter accepts 0 (none) and 9 (max)") {
      PngWriter(0)
      PngWriter(9)
   }

   test("PngWriter.withCompression rejects an invalid level") {
      shouldThrow<IllegalArgumentException> { PngWriter().withCompression(-1) }
      shouldThrow<IllegalArgumentException> { PngWriter().withCompression(11) }
   }
})
