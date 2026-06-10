@file:Suppress("BlockingMethodInNonBlockingContext")

package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import com.sksamuel.scrimage.nio.AnimatedGifReader
import com.sksamuel.scrimage.nio.ImageSource
import com.sksamuel.scrimage.nio.StreamingGifWriter
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.spec.style.wordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.apache.commons.io.IOUtils
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.DataBufferInt
import java.io.ByteArrayOutputStream
import java.time.Duration

class StreamingGifWriterTest : WordSpec({
   // run dispose method tests
   include(disposeMethodTests("/com/sksamuel/scrimage/nio/animated_birds.gif")) // tests 'doNotDispose'
   include(disposeMethodTests("/com/sksamuel/scrimage/nio/bananaDance.gif")) // tests 'restoreToBackgroundColor'
   include(disposeMethodTests("/com/sksamuel/scrimage/nio/canvas_prev.gif")) // tests 'restoreToPrevious'

   val bird = ImmutableImage.loader().fromResource("/bird_small.png")

   "StreamingGifWriter" should {
      "write to memory" {
         val writer = StreamingGifWriter().withFrameDelay(Duration.ofMillis(500))
         val output = ByteArrayOutputStream()
         val stream = writer.prepareStream(output, BufferedImage.TYPE_INT_ARGB)
         stream.writeFrame(bird)
         stream.writeFrame(bird.flipX())
         stream.writeFrame(bird.flipY())
         stream.writeFrame(bird.flipX())
         stream.writeFrame(bird.flipY())
         stream.close()
         val bytes = output.toByteArray()
         bytes shouldBe IOUtils.toByteArray(javaClass.getResourceAsStream("/com/sksamuel/scrimage/nio/streaming_gif_writer_output_test.gif"))
      }

      "use transparent pixels when compression is set" {
         val writer = StreamingGifWriter().withCompression(true).withFrameDelay(Duration.ofMillis(100))
         val input = AnimatedGifReader.read(ImageSource.of(javaClass.getResourceAsStream("/gif/fallingroof.gif")))
         val output = ByteArrayOutputStream()
         val stream = writer.prepareStream(output, BufferedImage.TYPE_INT_ARGB)
         input.frames.forEach { frame ->
            val scaled = frame.scaleTo(400, 280).copy(BufferedImage.TYPE_INT_ARGB)
            stream.writeFrame(scaled)
         }
         stream.close()
         val bytes = output.toByteArray()
         bytes shouldBe IOUtils.toByteArray(javaClass.getResourceAsStream("/gif/fallingroof_scaled.gif"))
      }

      "compressed-mode write of DataBufferInt frames round-trips through reader" {
         // Hits the new DataBufferInt fast path: two TYPE_INT_ARGB frames
         // differing on a single pixel. The first frame is written verbatim;
         // the second has matching pixels zeroed before being encoded with
         // the GIF transparency mechanism. Decoding back must reconstruct
         // both frames at the original dimensions.
         val w = 8; val h = 8
         val frame1 = ImmutableImage.create(w, h, BufferedImage.TYPE_INT_ARGB)
         val frame2 = ImmutableImage.create(w, h, BufferedImage.TYPE_INT_ARGB)
         for (y in 0 until h) for (x in 0 until w) {
            val color = RGBColor(x * 30, y * 30, 128, 255)
            frame1.setColor(x, y, color)
            frame2.setColor(x, y, color)
         }
         frame2.setColor(3, 3, RGBColor(255, 0, 0, 255))

         val writer = StreamingGifWriter().withCompression(true).withFrameDelay(Duration.ofMillis(100))
         val output = ByteArrayOutputStream()
         val stream = writer.prepareStream(output, BufferedImage.TYPE_INT_ARGB)
         stream.writeFrame(frame1)
         stream.writeFrame(frame2)
         stream.close()

         val decoded = AnimatedGifReader.read(ImageSource.of(output.toByteArray()))
         decoded.frameCount shouldBe 2
         decoded.getFrame(0).width shouldBe w
         decoded.getFrame(0).height shouldBe h
         decoded.getFrame(1).width shouldBe w
         decoded.getFrame(1).height shouldBe h
      }

      "compressed-mode write of DataBufferByte frames round-trips through reader" {
         // Hits the new DataBufferByte fast path: TYPE_4BYTE_ABGR images are
         // backed by a byte buffer, which previously fell through to the slow
         // generic getElem/setElem loop. Behaviour must be unchanged: the diff
         // is applied, the frames encode, and decoding reconstructs both
         // frames at the original dimensions.
         val w = 8; val h = 8
         val frame1 = ImmutableImage.create(w, h, BufferedImage.TYPE_4BYTE_ABGR)
         val frame2 = ImmutableImage.create(w, h, BufferedImage.TYPE_4BYTE_ABGR)
         for (y in 0 until h) for (x in 0 until w) {
            val color = RGBColor(x * 30, y * 30, 128, 255)
            frame1.setColor(x, y, color)
            frame2.setColor(x, y, color)
         }
         frame2.setColor(3, 3, RGBColor(255, 0, 0, 255))

         // sanity: these frames must actually be byte-backed so the fast path is taken
         frame1.awt().raster.dataBuffer.shouldBeInstanceOf<DataBufferByte>()
         frame2.awt().raster.dataBuffer.shouldBeInstanceOf<DataBufferByte>()

         val snap1: IntArray = frame1.pixels().map { it.argb }.toIntArray()
         val snap2: IntArray = frame2.pixels().map { it.argb }.toIntArray()

         val writer = StreamingGifWriter().withCompression(true).withFrameDelay(Duration.ofMillis(100))
         val output = ByteArrayOutputStream()
         val stream = writer.prepareStream(output, BufferedImage.TYPE_INT_ARGB)
         stream.writeFrame(frame1)
         stream.writeFrame(frame2)
         stream.close()

         val decoded = AnimatedGifReader.read(ImageSource.of(output.toByteArray()))
         decoded.frameCount shouldBe 2
         decoded.getFrame(0).width shouldBe w
         decoded.getFrame(0).height shouldBe h
         decoded.getFrame(1).width shouldBe w
         decoded.getFrame(1).height shouldBe h

         // the caller's byte-backed images must not be mutated by the diff step
         frame1.pixels().map { it.argb }.toIntArray().toList() shouldBe snap1.toList()
         frame2.pixels().map { it.argb }.toIntArray().toList() shouldBe snap2.toList()
      }

      "compressed mode decodes identically for byte-backed and int-backed frames" {
         // The DataBufferByte fast path must produce output equivalent to the
         // DataBufferInt fast path for pixel-identical input. The changed pixel
         // differs in every channel (including alpha) so the byte-wise and
         // int-wise diffs zero exactly the same pixels.
         val w = 8; val h = 8
         fun frames(type: Int): Pair<ImmutableImage, ImmutableImage> {
            val f1 = ImmutableImage.create(w, h, type)
            val f2 = ImmutableImage.create(w, h, type)
            for (y in 0 until h) for (x in 0 until w) {
               val color = RGBColor(x * 30, y * 30, 128, 255)
               f1.setColor(x, y, color)
               f2.setColor(x, y, color)
            }
            f2.setColor(3, 3, RGBColor(255, 0, 0, 254))
            return f1 to f2
         }

         fun write(f1: ImmutableImage, f2: ImmutableImage): ByteArray {
            val writer = StreamingGifWriter().withCompression(true).withFrameDelay(Duration.ofMillis(100))
            val output = ByteArrayOutputStream()
            val stream = writer.prepareStream(output, BufferedImage.TYPE_INT_ARGB)
            stream.writeFrame(f1)
            stream.writeFrame(f2)
            stream.close()
            return output.toByteArray()
         }

         val (byte1, byte2) = frames(BufferedImage.TYPE_4BYTE_ABGR)
         val (int1, int2) = frames(BufferedImage.TYPE_INT_ARGB)
         byte1.awt().raster.dataBuffer.shouldBeInstanceOf<DataBufferByte>()
         int1.awt().raster.dataBuffer.shouldBeInstanceOf<DataBufferInt>()

         val byteGif = AnimatedGifReader.read(ImageSource.of(write(byte1, byte2)))
         val intGif = AnimatedGifReader.read(ImageSource.of(write(int1, int2)))

         byteGif.frameCount shouldBe intGif.frameCount
         for (i in 0 until byteGif.frameCount) {
            byteGif.getFrame(i).pixels() shouldBe intGif.getFrame(i).pixels()
         }
      }

      "compressed-mode write of non-int non-byte frames uses the generic fallback" {
         // TYPE_USHORT_565_RGB frames are backed by a DataBufferUShort, so the
         // compressed diff must take the generic getElem/setElem fallback
         // (where getSize() is now hoisted out of the loop). The write must
         // still succeed and decode with the original frame count/dimensions.
         val w = 8; val h = 8
         val frame1 = ImmutableImage.create(w, h, BufferedImage.TYPE_USHORT_565_RGB)
         val frame2 = ImmutableImage.create(w, h, BufferedImage.TYPE_USHORT_565_RGB)
         for (y in 0 until h) for (x in 0 until w) {
            val color = RGBColor(x * 30, y * 30, 128, 255)
            frame1.setColor(x, y, color)
            frame2.setColor(x, y, color)
         }
         frame2.setColor(3, 3, RGBColor(255, 0, 0, 255))

         // sanity: neither fast path applies to these frames
         val buffer = frame1.awt().raster.dataBuffer
         (buffer is DataBufferInt) shouldBe false
         (buffer is DataBufferByte) shouldBe false

         val writer = StreamingGifWriter().withCompression(true).withFrameDelay(Duration.ofMillis(100))
         val output = ByteArrayOutputStream()
         val stream = writer.prepareStream(output, BufferedImage.TYPE_INT_ARGB)
         stream.writeFrame(frame1)
         stream.writeFrame(frame2)
         stream.close()

         val decoded = AnimatedGifReader.read(ImageSource.of(output.toByteArray()))
         decoded.frameCount shouldBe 2
         decoded.getFrame(0).width shouldBe w
         decoded.getFrame(0).height shouldBe h
         decoded.getFrame(1).width shouldBe w
         decoded.getFrame(1).height shouldBe h
      }

      "not mutate caller's ImmutableImage in compressed mode" {
         // Two 8x8 images where most pixels match, so the compressed diff path
         // will attempt to zero-fill matching cells.
         val image1 = ImmutableImage.create(8, 8, BufferedImage.TYPE_INT_ARGB)
         val image2 = ImmutableImage.create(8, 8, BufferedImage.TYPE_INT_ARGB)
         for (y in 0 until 8) {
            for (x in 0 until 8) {
               val color = RGBColor(x * 30, y * 30, 128, 255)
               image1.setColor(x, y, color)
               image2.setColor(x, y, color)
            }
         }
         // Differ on exactly one pixel so image2 is distinguishable
         image2.setColor(3, 3, RGBColor(255, 0, 0, 255))

         val snap1: IntArray = image1.pixels().map { it.argb }.toIntArray()
         val snap2: IntArray = image2.pixels().map { it.argb }.toIntArray()

         val writer = StreamingGifWriter().withCompression(true).withFrameDelay(Duration.ofMillis(100))
         val output = ByteArrayOutputStream()
         val stream = writer.prepareStream(output, BufferedImage.TYPE_INT_ARGB)
         stream.writeFrame(image1)
         stream.writeFrame(image2)
         stream.close()

         image1.pixels().map { it.argb }.toIntArray().toList() shouldBe snap1.toList()
         image2.pixels().map { it.argb }.toIntArray().toList() shouldBe snap2.toList()
      }
   }

})

fun disposeMethodTests(resourcePath: String) = wordSpec {
   val originalGif = AnimatedGifReader.read(ImageSource.of(javaClass.getResourceAsStream(resourcePath)))

   "StreamingGifWriter" should {
      val writer = StreamingGifWriter()
      val output = ByteArrayOutputStream()
      val stream = writer.prepareStream(output, BufferedImage.TYPE_INT_ARGB)

      for ((i, frame) in originalGif.frames.withIndex()) {
         "write frame with index \"$i\" to stream with \"${originalGif.getDisposeMethod(i)}\" dispose method" {
            stream.writeFrame(frame, originalGif.getDelay(i), originalGif.getDisposeMethod(i))
         }
      }

      "close" {
         stream.close()
      }

      "contents from $resourcePath and output should be equal" {
         val outputGif = AnimatedGifReader.read(ImageSource.of(output.toByteArray()))
         for ((i, frame) in outputGif.frames.withIndex()) {
            outputGif.getDisposeMethod(i) shouldBe originalGif.getDisposeMethod(i)
            outputGif.getDelay(i) shouldBe originalGif.getDelay(i)
            frame.pixels() shouldBe originalGif.getFrame(i).pixels()
         }
      }
   }
}
