package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class QuantizeTest : FunSpec() {
   init {

      val image = ImmutableImage.loader().fromResource("/com/sksamuel/scrimage/bird.jpg")

      test("quantize 16") {

         image.quantize(16).map { it.toHex() } shouldBe listOf(
            "E3DEDE",
            "64411B",
            "33290B",
            "C2906B",
            "DEA39C",
            "9C3A17",
            "EF4D22",
            "CD7364",
            "D4461F",
            "9B6E39",
            "A11604",
            "AD5342",
            "C7B765",
            "4C6434",
            "ACB48E",
            "4C4454"
         )
      }

      test("quantize 4") {
         image.quantize(4).map { it.toHex() } shouldBe listOf(
            "AD502C", "33290B", "E3DEDD", "DEA39C"
         )
      }

      test("quantize 2") {
         image.quantize(2).map { it.toHex() } shouldBe listOf("B86E51", "33290B")
      }
   }
}
