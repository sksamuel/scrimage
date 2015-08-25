package com.sksamuel.scrimage

object IphoneOrientation {

  def reorient(image: Image, metadata: ImageMetadata): Image = {
    val orientationTags = metadata.tagsBy(_.`type` == 274)
    if (orientationTags.size == 1) {
      orientationTags.head.rawValue match {
        case "3" => image.flipY // Bottom, right side (Rotate 180)
        case "6" => image.rotateRight // 6,Right side, top (Rotate 90 CW)))
        case "8" => image.rotateLeft // 8,Left side, bottom (Rotate 270 CW)
        case _ => image // no info or 1,Top, left side (Horizontal / normal)))
      }
    } else {
      image
    }
  }
}
