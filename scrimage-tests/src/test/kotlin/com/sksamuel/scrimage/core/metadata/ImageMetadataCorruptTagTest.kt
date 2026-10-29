package com.sksamuel.scrimage.core.metadata

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.metadata.ImageMetadata
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe

/**
 * Regression: ImageMetadata.fromMetadata called metadata-extractor's Tag.getDescription() for every
 * tag, and descriptions are decoded lazily. A corrupt ICC tag offset makes
 * IccDescriptor.getTagDataString throw StringIndexOutOfBoundsException, which aborted the whole
 * metadata load. Because ImmutableImageLoader loads metadata by default, an otherwise decodable
 * JPEG then failed to load at all.
 */
class ImageMetadataCorruptTagTest : FunSpec({

   val resource = "/com/sksamuel/scrimage/iphone/portrait_6.jpg"

   fun original(): ByteArray = javaClass.getResourceAsStream(resource)!!.readBytes()

   // three bytes inside the ICC profile changed; found by mutation fuzzing
   fun corrupted(): ByteArray {
      val bytes = original()
      bytes[763] = 174.toByte()
      bytes[884] = 79
      bytes[3525] = 70
      return bytes
   }

   test("the corrupted file still decodes without metadata") {
      ImmutableImage.loader().detectMetadata(false).detectOrientation(false).fromBytes(corrupted()).width shouldBe 600
   }

   test("the default loader loads an image whose ICC tag is corrupt") {
      val expected = ImmutableImage.loader().fromBytes(original())
      val image = ImmutableImage.loader().fromBytes(corrupted())
      // the EXIF orientation is still read and applied
      image.width shouldBe expected.width
      image.height shouldBe expected.height
   }

   test("metadata keeps the readable tags when one tag description cannot be decoded") {
      val metadata = ImageMetadata.fromBytes(corrupted())
      metadata.tags().toList().shouldNotBeEmpty()
      metadata.tags().any { it.getName() == "Orientation" } shouldBe true
   }
})
