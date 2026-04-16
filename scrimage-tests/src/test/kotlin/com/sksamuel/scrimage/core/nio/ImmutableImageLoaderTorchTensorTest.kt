package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ImmutableImageLoaderTorchTensorTest : FunSpec() {
   init {

      test("fromTorchTensor int[] reconstructs RGB pixel values from CHW layout") {
         // 2x2 image, 3 channels (RGB), CHW layout:
         // R channel: [[10, 20], [30, 40]]
         // G channel: [[50, 60], [70, 80]]
         // B channel: [[90, 100], [110, 120]]
         val data = intArrayOf(
            10, 20, 30, 40,   // R plane, row-major
            50, 60, 70, 80,   // G plane
            90, 100, 110, 120 // B plane
         )
         val image = ImmutableImageLoader.create().fromTorchTensor(data, 2, 2)
         image.width shouldBe 2
         image.height shouldBe 2
         image.pixel(0, 0).red() shouldBe 10
         image.pixel(0, 0).green() shouldBe 50
         image.pixel(0, 0).blue() shouldBe 90
         image.pixel(1, 0).red() shouldBe 20
         image.pixel(1, 0).green() shouldBe 60
         image.pixel(1, 0).blue() shouldBe 100
         image.pixel(0, 1).red() shouldBe 30
         image.pixel(0, 1).green() shouldBe 70
         image.pixel(0, 1).blue() shouldBe 110
         image.pixel(1, 1).red() shouldBe 40
         image.pixel(1, 1).green() shouldBe 80
         image.pixel(1, 1).blue() shouldBe 120
      }

      test("fromTorchTensor int[] handles grayscale (1-channel) tensor") {
         // 2x2, 1 channel — value replicated to R, G, B
         val data = intArrayOf(10, 20, 30, 40)
         val image = ImmutableImageLoader.create().fromTorchTensor(data, 2, 2)
         image.pixel(0, 0).red() shouldBe 10
         image.pixel(0, 0).green() shouldBe 10
         image.pixel(0, 0).blue() shouldBe 10
         image.pixel(1, 1).red() shouldBe 40
      }

      test("fromTorchTensor int[] handles RGBA (4-channel) tensor") {
         // 1x1, 4 channels: R=100, G=150, B=200, A=128
         val data = intArrayOf(100, 150, 200, 128)
         val image = ImmutableImageLoader.create().fromTorchTensor(data, 1, 1)
         image.pixel(0, 0).red() shouldBe 100
         image.pixel(0, 0).green() shouldBe 150
         image.pixel(0, 0).blue() shouldBe 200
         image.pixel(0, 0).alpha() shouldBe 128
      }

      test("fromTorchTensor int[] clamps out-of-range values") {
         // 2x2, 3 channels — first pixel red=-10 (clamp to 0), second pixel red=300 (clamp to 255)
         val data = intArrayOf(
            -10, 300, 0, 0, // R
            0, 0, 0, 0,     // G
            0, 0, 0, 0      // B
         )
         val image = ImmutableImageLoader.create().fromTorchTensor(data, 2, 2)
         image.pixel(0, 0).red() shouldBe 0
         image.pixel(1, 0).red() shouldBe 255
      }

      test("fromTorchTensor float[] converts 0.0-1.0 values to 0-255") {
         // 1x2 image, 3 channels: left pixel black, right pixel white
         val data = floatArrayOf(
            0f, 1f, // R
            0f, 1f, // G
            0f, 1f  // B
         )
         val image = ImmutableImageLoader.create().fromTorchTensor(data, 2, 1)
         image.pixel(0, 0).red() shouldBe 0
         image.pixel(1, 0).red() shouldBe 255
      }

      test("fromTorchTensor throws on unsupported channel count") {
         // 8 values for a 2x2 image = 2 channels, which is not supported
         val data = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8)
         shouldThrow<IllegalArgumentException> {
            ImmutableImageLoader.create().fromTorchTensor(data, 2, 2)
         }
      }

      test("fromTorchTensor throws when data length does not match dimensions") {
         // 3 values is not divisible by 2×2=4
         val data = intArrayOf(1, 2, 3)
         shouldThrow<IllegalArgumentException> {
            ImmutableImageLoader.create().fromTorchTensor(data, 2, 2)
         }
      }
   }
}
