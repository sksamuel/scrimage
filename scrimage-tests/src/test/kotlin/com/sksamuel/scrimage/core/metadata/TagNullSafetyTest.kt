package com.sksamuel.scrimage.core.metadata

import com.sksamuel.scrimage.metadata.Tag
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Regression for Tag.equals / Tag.hashCode previously calling .equals
 * and .hashCode unconditionally on rawValue and value. Both fields can
 * be null in the wild — they come from drewmetadata's
 * `Directory.getString(int)` and `Tag.getDescription()`, which return
 * null for some real EXIF tags — and the unprotected calls would NPE.
 */
class TagNullSafetyTest : FunSpec({

   test("Tag.hashCode tolerates a null rawValue") {
      // Did not throw: the assertion is implicit in this call returning.
      Tag("Make", 271, null, "Canon").hashCode()
   }

   test("Tag.hashCode tolerates a null value") {
      Tag("Make", 271, "Canon", null).hashCode()
   }

   test("Tag.hashCode tolerates both null") {
      Tag("Make", 271, null, null).hashCode()
   }

   test("Tag.equals tolerates a null rawValue") {
      val a = Tag("Make", 271, null, "Canon")
      val b = Tag("Make", 271, null, "Canon")
      a.equals(b) shouldBe true
   }

   test("Tag.equals tolerates a null value") {
      val a = Tag("Make", 271, "Canon", null)
      val b = Tag("Make", 271, "Canon", null)
      a.equals(b) shouldBe true
   }

   test("Tag.equals distinguishes one null rawValue from a non-null one") {
      val a = Tag("Make", 271, null, "Canon")
      val b = Tag("Make", 271, "raw", "Canon")
      a.equals(b) shouldBe false
      b.equals(a) shouldBe false
   }

   test("Tag.equals contract: equal Tags have equal hash codes (with nulls)") {
      val a = Tag("Make", 271, null, null)
      val b = Tag("Make", 271, null, null)
      a shouldBe b
      a.hashCode() shouldBe b.hashCode()
   }

   test("Tag.equals: a Tag with null rawValue is not equal to one with a different name") {
      val a = Tag("Make", 271, null, "Canon")
      val b = Tag("Model", 272, null, "Canon")
      a shouldNotBe b
   }
})
