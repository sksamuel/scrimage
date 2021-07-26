package com.sksamuel.scrimage.format.png

import java.io.ByteArrayOutputStream
import java.util.zip.Inflater

fun deflate(input: ByteArray): ByteArray {
   if (input.isEmpty()) return byteArrayOf()
   val output = ByteArrayOutputStream()
   val buffer = ByteArray(1024)
   val decompresser = Inflater()
   decompresser.setInput(input)
   while (!decompresser.finished()) {
      val count = decompresser.inflate(buffer)
      if (count == 0)
         error("deflate was zero")
      output.write(buffer, 0, count)
   }
   output.close()
   decompresser.end()
   return output.toByteArray()
}
