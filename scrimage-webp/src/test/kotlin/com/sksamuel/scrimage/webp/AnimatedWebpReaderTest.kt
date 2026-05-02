package com.sksamuel.scrimage.webp

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.ImageSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.Duration

class AnimatedWebpReaderTest : FunSpec({

   test("reads the bundled animated.webp fixture and exposes canvas size, frame count and per-frame delays") {
      // webpmux -info on this fixture reports a 648x648 canvas, 34 frames
      // each at 30 ms, looping forever.
      val animated = AnimatedWebpReader.read(
         ImageSource.of(File("src/test/resources/animated.webp"))
      )

      animated.frameCount shouldBe 34
      animated.dimensions.width shouldBe 648
      animated.dimensions.height shouldBe 648
      animated.loopCount shouldBe 0  // 0 = loop forever
      animated.frames shouldHaveSize 34

      for (i in 0 until animated.frameCount) {
         animated.getDelay(i) shouldBe Duration.ofMillis(30)
      }
   }

   test("each rendered frame matches the canvas dimensions") {
      val animated = AnimatedWebpReader.read(
         ImageSource.of(File("src/test/resources/animated.webp"))
      )
      animated.frames.forEach { frame ->
         frame.width shouldBe animated.dimensions.width
         frame.height shouldBe animated.dimensions.height
      }
   }

   test("preserves the original webp bytes via getBytes()") {
      val source = File("src/test/resources/animated.webp").readBytes()
      val animated = AnimatedWebpReader.read(ImageSource.of(source))
      animated.bytes.size shouldBe source.size
      animated.bytes.contentEquals(source) shouldBe true
   }

   test("round trips through StreamingWebpWriter — write three distinct frames, read them back") {
      // Building the test webp via StreamingWebpWriter (rather than a
      // checked-in fixture) gives us tight control over the expected frame
      // count and delays without depending on any specific encoder version.
      val red = ImmutableImage.filled(40, 24, Color.RED)
      val blue = ImmutableImage.filled(40, 24, Color.BLUE)
      val green = ImmutableImage.filled(40, 24, Color.GREEN)

      val output = ByteArrayOutputStream()
      StreamingWebpWriter()
         .withFrameDelay(Duration.ofMillis(120))
         .withInfiniteLoop(false)
         .prepareStream(output)
         .use { stream ->
            stream.writeFrame(red)
            stream.writeFrame(blue, Duration.ofMillis(250))
            stream.writeFrame(green)
         }

      val animated = AnimatedWebpReader.read(ImageSource.of(output.toByteArray()))

      animated.frameCount shouldBe 3
      animated.dimensions.width shouldBe 40
      animated.dimensions.height shouldBe 24

      animated.getDelay(0) shouldBe Duration.ofMillis(120)
      animated.getDelay(1) shouldBe Duration.ofMillis(250)
      animated.getDelay(2) shouldBe Duration.ofMillis(120)

      // Frames are full-canvas after disposal/blending, so each rendered
      // frame should be dominated by the colour we wrote.
      animated.getFrame(0).pixel(0, 0).red() shouldBeGreaterThan 200
      animated.getFrame(1).pixel(0, 0).blue() shouldBeGreaterThan 200
      animated.getFrame(2).pixel(0, 0).green() shouldBeGreaterThan 200
   }

   test("WebpMuxHandler.parseInfo correctly parses a representative info dump") {
      // Pin the parser shape against a sample of real webpmux -info output
      // so that future changes to the parser don't silently break delay /
      // canvas / loop extraction.
      val sample = listOf(
         "Canvas size: 648 x 648",
         "Features present: animation transparency",
         "Background color : 0xFF18183D  Loop Count : 5",
         "Number of frames: 3",
         "No.: width height alpha x_offset y_offset duration   dispose blend image_size  compression",
         "  1:   648   648    no        0        0       30       none    no      20042    lossless",
         "  2:   399   548   yes       78      100       40       none   yes       1262    lossless",
         "  3:   444   418   yes       94       96       50       none   yes       5266    lossless",
      )
      val info = WebpMuxHandler.parseInfo(sample)
      info.canvasWidth shouldBe 648
      info.canvasHeight shouldBe 648
      info.loopCount shouldBe 5
      info.frames shouldHaveSize 3
      info.frames[0].durationMs shouldBe 30
      info.frames[1].durationMs shouldBe 40
      info.frames[2].durationMs shouldBe 50
      info.frames[1].xOffset shouldBe 78
      info.frames[1].yOffset shouldBe 100
      info.frames[2].width shouldBe 444
      info.frames[2].height shouldBe 418
   }
})
