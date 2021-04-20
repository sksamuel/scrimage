package com.sksamuel.scrimage.core

import java.nio.file.Files
import java.nio.file.Paths

fun main() {
   val file = "/etc/X11/rgb.txt"
   Files.newInputStream(Paths.get(file)).bufferedReader().readLines().map { line ->
      val tokens = line.replace("\\s{2,}".toRegex(), " ").trim().split(' ')
      val red = tokens[0]
      val green = tokens[1]
      val blue = tokens[2]
      val name = tokens.drop(3).joinToString(" ").trim()
      if (!name.contains(" "))
         println("val $name = Color($red, $green, $blue)")
   }
}
