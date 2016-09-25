package com.sksamuel.scrimage

import com.drew.metadata.exif.ExifIFD0Directory

object Orientation {

  def requiresReorientation(metadata: ImageMetadata): Boolean = {
    val imageOrientations = imageOrientationsOf(metadata)
    if (imageOrientations.size == 1) {
      imageOrientations.head match {
        case "2" | "3" | "4" | "5" | "6" | "7" | "8" => true
        case _ => false
      }
    } else {
      false
    }
  }

  def reorient(image: Image, metadata: ImageMetadata): Image = {
    val imageOrientations = imageOrientationsOf(metadata)

    if (imageOrientations.size == 1) {
      imageOrientations.head match {
        // Normal
        case "1" => image

        // Flip horizontally
        case "2" => image.flipX

        // Rotate 180 degrees
        case "3" => image.rotateLeft.rotateLeft

        // Rotate 180 degrees and flip horizontally
        case "4" => image.rotateLeft.rotateLeft.flipX

        // Rotate 90 degrees clockwise and flip horizontally
        case "5" => image.rotateRight.flipX

        // Rotate 90 degrees clockwise
        case "6" => image.rotateRight

        // Rotate 90 degrees anti-clockwise and flip horizontally
        case "7" => image.rotateLeft.flipX

        // Rotate 90 degrees anti-clockwise
        case "8" => image.rotateLeft

        // Unknown, keep the orginal image
        case _ => image
      }
    } else {
      image
    }
  }

  private def imageOrientationsOf(metadata: ImageMetadata): Set[String] = {
    val exifIFD0DirName = new ExifIFD0Directory().getName

    metadata.directories
      .find(_.name == exifIFD0DirName)
      .map(_.tags.filter(_.`type` == 274).map(_.rawValue).toSet)
      .getOrElse(Set.empty)
  }
}
