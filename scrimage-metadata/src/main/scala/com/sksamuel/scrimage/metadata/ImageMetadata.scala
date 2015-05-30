package com.sksamuel.scrimage.metadata

import java.io.{ ByteArrayInputStream, InputStream }

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.PngWriter

import scala.language.implicitConversions

object ImageMetadata {

  implicit class RichImage(image: Image) {
    def metadata: ImageMetadata = ImageMetadata.fromImage(image)
  }

  import scala.collection.JavaConverters._

  def fromImage(image: Image): ImageMetadata = {
    val metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(image.bytes(PngWriter.MinCompression)))
    fromMetadata(metadata)
  }

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
}

case class ImageMetadata(directories: List[Directory]) {
  def tags: List[Tag] = directories.flatMap(_.tags)
}

case class Directory(name: String, tags: List[Tag])

case class Tag(name: String, `type`: Int, rawValue: String, value: String)