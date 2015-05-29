package com.sksamuel.scrimage.metadata

import java.io.InputStream

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata

object ImageMetadata {

  import scala.collection.JavaConverters._

  def fromStream(is: InputStream): ImageMetadata = {
    val metadata = ImageMetadataReader.readMetadata(is)
    fromMetadata(metadata)
  }

  def fromMetadata(metadata: Metadata): ImageMetadata = {
    val directories = metadata.getDirectories.asScala.map { directory =>
      println(directory)
      val tags = directory.getTags.asScala.map { tag =>
        Tag(tag.getTagName, tag.getTagType, tag.getDescription)
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

case class Tag(name: String, `type`: Int, value: String)