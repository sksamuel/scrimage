package com.sksamuel.scrimage

import java.io.{File, ByteArrayInputStream, InputStream}
import java.nio.file.Files

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.sksamuel.scrimage.nio.PngWriter

import scala.collection.JavaConverters._

object ImageMetadata {

  def fromImage(image: AbstractImage): ImageMetadata = {
    val metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(image.bytes(PngWriter.NoCompression)))
    fromMetadata(metadata)
  }

  def fromResource(resource: String): ImageMetadata = fromStream(getClass.getResourceAsStream(resource))

  def fromStream(is: InputStream): ImageMetadata = {
    val metadata = ImageMetadataReader.readMetadata(is)
    fromMetadata(metadata)
  }

  def fromMetadata(metadata: Metadata): ImageMetadata = {

    val directories = metadata.getDirectories.asScala.map { directory =>
      val tags = directory.getTags.asScala.map { tag =>
        Tag(tag.getTagName, tag.getTagType, directory.getString(tag.getTagType), tag.getDescription)
      }
      Directory(directory.getName, tags.toList)
    }
    ImageMetadata(directories.toList)
  }

  def fromFile(file: File): ImageMetadata = fromStream(Files.newInputStream(file.toPath))

  lazy val empty = ImageMetadata(Nil)
}

case class ImageMetadata(directories: List[Directory]) {
  def tags: List[Tag] = directories.flatMap(_.tags)
}

case class Directory(name: String, tags: List[Tag])

case class Tag(name: String, `type`: Int, rawValue: String, value: String)