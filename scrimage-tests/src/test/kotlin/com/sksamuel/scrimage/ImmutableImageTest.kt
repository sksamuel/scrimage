package com.sksamuel.scrimage

import com.sksamuel.scrimage.nio.PngWriter
import io.kotest.core.spec.style.FunSpec
import java.awt.Color
import java.io.File
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ImmutableImageTest : FunSpec({

   test("!slow and fast give same results") {
      val sourceImg = ImmutableImage.loader().fromResource("/transparent_chip.png")

      val color = Color.WHITE
      val fastRemoval = sourceImg.removeTransparency(color, true)
      val slowRemoval = sourceImg.removeTransparency(color, false)

      var hasDifferences = false
      fastRemoval.forEach { fastPixel ->
         val slowPixel = slowRemoval.pixel(fastPixel.x, fastPixel.y)
         if (slowPixel != fastPixel) {
            hasDifferences = true
         }
      }
      if (hasDifferences) {
         val format = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.ROOT)
         val dir = Files.createTempDirectory("comparison_result_${LocalDateTime.now().format(format)}_")
         val fastFile = File(dir.toFile().absolutePath, "fast.png")
         val slowFile = File(dir.toFile().absolutePath, "slow.png")
         fastFile.outputStream().use {
            it.write(fastRemoval.forWriter(PngWriter.NoCompression).bytes())
         }
         slowFile.outputStream().use {
            it.write(slowRemoval.forWriter(PngWriter.NoCompression).bytes())
         }
         println("Wrote differences to ${dir.toAbsolutePath()}")
         println("To see differences, use")
         println("imgdiff -t 0.001 \"${fastFile.absolutePath}\" \"${slowFile.absolutePath}\" diff.png")
      }
   }

})
