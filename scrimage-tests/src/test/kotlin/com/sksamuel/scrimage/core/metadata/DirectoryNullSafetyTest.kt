package com.sksamuel.scrimage.core.metadata

import com.sksamuel.scrimage.metadata.Directory
import com.sksamuel.scrimage.metadata.Tag
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Regression for Directory.equals / Directory.hashCode previously calling
 * .equals and .hashCode unconditionally on name. The name field can be null
 * — Directory's constructor doesn't enforce non-null, and parallel surfaces
 * (Tag.rawValue / value) carry the same null risk for the same reason
 * (drewmetadata returns null for some real records). The Tag class was
 * fixed earlier with Objects.equals / Objects.hashCode; Directory had the
 * same shape and the same bug.
 */
class DirectoryNullSafetyTest : FunSpec({

   test("Directory.hashCode tolerates a null name") {
      Directory(null, arrayOf()).hashCode()
   }

   test("Directory.equals tolerates a null name") {
      val a = Directory(null, arrayOf())
      val b = Directory(null, arrayOf())
      a.equals(b) shouldBe true
   }

   test("Directory.equals distinguishes a null name from a non-null one") {
      val a = Directory(null, arrayOf())
      val b = Directory("Exif IFD0", arrayOf())
      a.equals(b) shouldBe false
      b.equals(a) shouldBe false
   }

   test("Directory.equals contract: equal Directories with null name have equal hash codes") {
      val a = Directory(null, arrayOf(Tag("Make", 271, null, "Canon")))
      val b = Directory(null, arrayOf(Tag("Make", 271, null, "Canon")))
      a shouldBe b
      a.hashCode() shouldBe b.hashCode()
   }

   test("Directory.equals: tags array still discriminates when names match") {
      val a = Directory("Exif", arrayOf(Tag("Make", 271, null, "Canon")))
      val b = Directory("Exif", arrayOf(Tag("Make", 271, null, "Nikon")))
      a shouldNotBe b
   }
})
