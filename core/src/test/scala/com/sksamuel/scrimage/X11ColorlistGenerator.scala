package com.sksamuel.scrimage

import org.apache.commons.io.IOUtils
import java.io.FileInputStream
import scala.collection.JavaConverters._
import org.apache.commons.lang3.text.WordUtils

/** @author Stephen Samuel */
object X11ColorlistGenerator extends App {
  val file = "/etc/X11/rgb.txt"
  val lines = IOUtils.readLines(new FileInputStream(file))
  for (line <- lines.asScala) {
    val tokens = line.replaceAll("\\s{2,}", " ").trim.split(' ')
    val red = tokens(0)
    val green = tokens(1)
    val blue = tokens(2)
    val name = WordUtils.capitalize(tokens.drop(3).mkString(" "))
    if (!name.contains(" "))
      println(s"val $name = Color($red, $green, $blue)")
  }
}
