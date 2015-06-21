package com.sksamuel.scrimage

object IphoneOrientation {

  def reorient(image: Image, metadata: ImageMetadata): Image = {
    metadata.tag(274).fold(image)(tag => tag.rawValue match {
      case "6" => image.rotateLeft
      case _ => image
    })
  }
}
