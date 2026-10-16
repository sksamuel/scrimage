package com.sksamuel.scrimage.core

import com.sksamuel.scrimage.ImmutableImage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import java.awt.Color

/**
 * Regression test: diff() created its result with ImmutableImage.create(...) and returned
 * it without associateMetadata(metadata), so it was the only derived-image operation that
 * dropped the source's metadata (EXIF etc.). invert, gamma, toSquare, toGrayscale, flipX
 * and rotateLeft all keep it.
 */
class DiffMetadataTest : FunSpec({

   test("diff keeps the metadata of the receiver, like the other derived-image ops") {
      val source = ImmutableImage.loader().fromResource("/aitor.jpg").scaleTo(10, 10)
      val tags = source.metadata.tags().size
      tags shouldBeGreaterThan 0

      source.diff(source).metadata.tags().size shouldBe tags
      // sanity: the ops diff should behave like do keep theirs
      source.invert().metadata.tags().size shouldBe tags
      source.toGrayscale().metadata.tags().size shouldBe tags
      source.flipX().metadata.tags().size shouldBe tags
   }

   test("diff output is unchanged: identical images are black, differences are per-channel") {
      val a = ImmutableImage.filled(4, 4, Color.BLACK)
      val b = ImmutableImage.filled(4, 4, Color.WHITE)
      a.diff(a).argbints().distinct() shouldBe listOf(Color.BLACK.rgb)
      a.diff(b).argbints().distinct() shouldBe listOf(Color.WHITE.rgb)
   }
})
