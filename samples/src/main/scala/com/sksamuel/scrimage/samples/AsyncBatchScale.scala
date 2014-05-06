package com.sksamuel.scrimage.samples

import com.sksamuel.scrimage.{AsyncImage, Format}
import java.io.File

/**
 * This sample app will resize all images inside a folder.
 *
 * @author Stephen Samuel */
object AsyncBatchScale extends App {

  val dir = new File(args(0)) // use the first argument as the location of the files
  for ( file <- dir.listFiles;
        image <- AsyncImage(file);
        scaled <- image.scale(0.5) ) {
    scaled.writer(Format.PNG).write(file)
  }
}
