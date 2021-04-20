package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import io.kotest.core.spec.style.FunSpec

class Issue196 : FunSpec({
   test("cannot load some png images #196") {
      val a = ImmutableImage.loader().fromResource("/github_196.png")
      a.cover(100, 100).bytes(JpegWriter())
   }
})
