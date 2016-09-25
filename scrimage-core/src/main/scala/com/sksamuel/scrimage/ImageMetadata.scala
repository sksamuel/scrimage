package com.sksamuel.scrimage

import java.io.{ByteArrayInputStream, File, InputStream}
import java.nio.file.{Files, Path}

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.sksamuel.scrimage.nio.PngWriter

import scala.collection.JavaConverters._

object ImageMetadata extends Using {

  import ImageMetadataReader._

  def fromImage(image: Image): ImageMetadata = {
    val metadata = using(new ByteArrayInputStream(image.bytes(PngWriter.NoCompression))){ stream =>
    ImageMetadataReader.readMetadata(stream)
    }
    fromMetadata(metadata)
  }

  def fromPath(path: Path) : ImageMetadata = fromFile(path.toFile)

  def fromFile(file: File): ImageMetadata = using(Files.newInputStream(file.toPath))(fromStream)

  def fromResource(resource: String): ImageMetadata = using(getClass.getResourceAsStream(resource))(fromStream)

  def fromStream(is: InputStream): ImageMetadata = fromMetadata(readMetadata(is))

  def fromBytes(bytes: Array[Byte]): ImageMetadata = using(new ByteArrayInputStream(bytes)) { stream =>
    fromMetadata(readMetadata(stream))
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

  lazy val empty = ImageMetadata(Nil)
}

case class ImageMetadata(directories: List[Directory]) {
  def tagsBy(f: Tag => Boolean): Seq[Tag] = tags.filter(f)
  def tags: List[Tag] = directories.flatMap(_.tags)
}

case class Directory(name: String, tags: List[Tag])

case class Tag(name: String, `type`: Int, rawValue: String, value: String)