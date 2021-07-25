package com.sksamuel.scrimage.format.png

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ULongTest : FunSpec({

   test("toULong") {
      byteArrayOf(0, 0, 0, 0).toUInt() shouldBe 0
      byteArrayOf(0, 0, 0, 127).toUInt() shouldBe 127
      byteArrayOf(0, 0, 0, -128).toUInt() shouldBe 128
      byteArrayOf(0, 0, 0, -127).toUInt() shouldBe 129
      byteArrayOf(0, 0, 0, -1).toUInt() shouldBe 255
      byteArrayOf(0, 0, 1, 1).toUInt() shouldBe 257
      byteArrayOf(0, 0, 1, -1).toUInt() shouldBe 511
      byteArrayOf(0, 1, 0, 0).toUInt() shouldBe 65536
      byteArrayOf(0, 1, 0, 1).toUInt() shouldBe 65537
      byteArrayOf(1, 0, 0, 0).toUInt() shouldBe 16777216
      byteArrayOf(-128, 0, 0, 0).toUInt() shouldBe 2147483648L
      byteArrayOf(-1, 0, 0, 0).toUInt() shouldBe 4278190080L
      byteArrayOf(-1, -1, -1, -1).toUInt() shouldBe 4294967295L
   }

})
