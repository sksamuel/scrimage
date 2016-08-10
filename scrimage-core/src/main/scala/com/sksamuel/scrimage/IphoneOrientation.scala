package com.sksamuel.scrimage

object IphoneOrientation {

  def reorient(image: Image, metadata: ImageMetadata): Image = {
    val orientationTags = metadata.tagsBy(_.`type` == 274)
    if (orientationTags.map(_.rawValue).toSet.size == 1) {
      orientationTags.head.rawValue match {
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
}
