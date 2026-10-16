package com.sksamuel.scrimage.core.metadata

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.metadata.Directory
import com.sksamuel.scrimage.metadata.ImageMetadata
import com.sksamuel.scrimage.metadata.Orientation
import com.sksamuel.scrimage.metadata.OrientationTools
import com.sksamuel.scrimage.metadata.Tag
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.util.Optional

/**
 * Regression for OrientationTools throwing NullPointerException on a Directory or Tag with a
 * null name. Directory and Tag were deliberately made null-safe (their constructors accept null
 * names, equals/hashCode use Objects.equals) but OrientationTools.imageOrientationsOf called
 * `dir.getName().equals(...)` and `t.getName().toLowerCase()` unconditionally.
 */
class OrientationToolsNullNameTest : FunSpec({

   val orientation6 = Tag("Orientation", 274, "6", "Right side, top")

   test("getOrientation tolerates a directory with a null name") {
      val md = ImageMetadata(arrayOf(Directory(null, arrayOf(orientation6))))
      md.orientation shouldBe Optional.empty<Orientation>()
      OrientationTools.requiresReorientation(md) shouldBe false
   }

   test("a null-named directory does not hide the Exif IFD0 directory that follows it") {
      val md = ImageMetadata(
         arrayOf(
            Directory(null, arrayOf(Tag("Make", 271, null, "Canon"))),
            Directory("Exif IFD0", arrayOf(orientation6)),
         )
      )
      md.orientation shouldBe Optional.of(Orientation.NinetyMirrored)
      OrientationTools.requiresReorientation(md) shouldBe true
   }

   test("getOrientation tolerates a null-named tag among several type 274 tags") {
      // two tags with type 274 force the name check; one of them has no name
      val md = ImageMetadata(
         arrayOf(
            Directory(
               "Exif IFD0",
               arrayOf(Tag(null, 274, "1", "Unknown"), orientation6)
            )
         )
      )
      md.orientation shouldBe Optional.of(Orientation.NinetyMirrored)
   }

   test("reorient tolerates null directory and tag names") {
      val image = ImmutableImage.filled(4, 2, Color.RED)
      val md = ImageMetadata(
         arrayOf(
            Directory(null, arrayOf(orientation6)),
            Directory("Exif IFD0", arrayOf(Tag(null, 274, "6", "Right side, top"), orientation6)),
         )
      )
      val reoriented = OrientationTools.reorient(image, md)
      reoriented.width shouldBe 2
      reoriented.height shouldBe 4
   }
})
