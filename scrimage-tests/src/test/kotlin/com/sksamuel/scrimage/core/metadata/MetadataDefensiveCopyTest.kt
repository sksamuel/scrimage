package com.sksamuel.scrimage.core.metadata

import com.sksamuel.scrimage.metadata.Directory
import com.sksamuel.scrimage.metadata.ImageMetadata
import com.sksamuel.scrimage.metadata.Tag
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Regression: ImageMetadata.getDirectories() and Directory.getTags()
 * returned the backing array directly. A caller mutating that array
 * (`meta.getDirectories()[0] = null`) corrupted internal state — the
 * subsequent equals/hashCode used the mutated array and `tags()`
 * NPE'd inside the flatMap stream.
 */
class MetadataDefensiveCopyTest : FunSpec({

   test("ImageMetadata.getDirectories() returns a defensive copy") {
      val meta = ImageMetadata(arrayOf(Directory("Exif IFD0", arrayOf())))

      val firstView = meta.directories
      firstView[0] = Directory("Mutated", arrayOf())

      // Internal state unchanged
      meta.directories[0].name shouldBe "Exif IFD0"
      // First view (now mutated locally) does not equal a fresh view
      firstView[0].name shouldNotBe meta.directories[0].name
   }

   test("Directory.getTags() returns a defensive copy") {
      val tag = Tag("Make", 271, "Canon", "Canon")
      val dir = Directory("Exif IFD0", arrayOf(tag))

      val firstView = dir.tags
      firstView[0] = Tag("Mutated", 1, "x", "y")

      // Internal state unchanged
      dir.tags[0].name shouldBe "Make"
      firstView[0].name shouldNotBe dir.tags[0].name
   }
})
