package com.sksamuel.scrimage.core.nio

import com.sksamuel.scrimage.metadata.ImageMetadata
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Files

class ImmutableImageLoaderStripMetadataTest : FunSpec() {
   init {

      // metadata-extractor reports a directory per metadata block; EXIF data lives in these.
      val exifDirectories = setOf("Exif IFD0", "Exif SubIFD", "Exif Thumbnail", "GPS", "Interoperability")

      fun copyResourceToTemp(resource: String, suffix: String): File {
         val tmp = Files.createTempFile("strip-meta", suffix).toFile()
         tmp.deleteOnExit()
         javaClass.getResourceAsStream(resource).use { input ->
            tmp.outputStream().use { out -> input!!.copyTo(out) }
         }
         return tmp
      }

      test("stripMetadata removes EXIF data from a jpeg, leaving the pixels intact") {
         val file = copyResourceToTemp("/vossen.jpg", ".jpg")

         // sanity check: the source actually carries EXIF metadata to strip
         ImageMetadata.fromFile(file).directories.map { it.name } shouldContain "Exif IFD0"
         val original = ImmutableImageLoader.create().fromFile(file)

         val result = ImmutableImageLoader.create().stripMetadata(file)
         result shouldBe file

         // every EXIF directory is gone
         val dirs = ImageMetadata.fromFile(file).directories.map { it.name }
         exifDirectories.forEach { dirs shouldNotContain it }

         // but the image is still readable and unchanged in size
         val stripped = ImmutableImageLoader.create().fromFile(file)
         stripped.width shouldBe original.width
         stripped.height shouldBe original.height
      }

      test("stripMetadata leaves a png readable and unchanged in size") {
         val file = copyResourceToTemp("/balloon.png", ".png")
         val original = ImmutableImageLoader.create().fromFile(file)

         ImmutableImageLoader.create().stripMetadata(file)

         val dirs = ImageMetadata.fromFile(file).directories.map { it.name }
         exifDirectories.forEach { dirs shouldNotContain it }

         val stripped = ImmutableImageLoader.create().fromFile(file)
         stripped.width shouldBe original.width
         stripped.height shouldBe original.height
      }

      test("stripMetadata throws if the file does not exist") {
         shouldThrow<FileNotFoundException> {
            ImmutableImageLoader.create().stripMetadata(File("does-not-exist.jpg"))
         }
      }
   }
}
