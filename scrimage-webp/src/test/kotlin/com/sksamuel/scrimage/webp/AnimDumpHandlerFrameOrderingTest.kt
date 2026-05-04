package com.sksamuel.scrimage.webp

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.nio.file.Paths

class AnimDumpHandlerFrameOrderingTest : FunSpec({

   // Regression for AnimDumpHandler sorting dumped frames lexicographically.
   // anim_dump emits dump_0000.png, dump_0001.png, ... , dump_9999.png and
   // then dump_10000.png. Lexicographic sort puts dump_10000.png BEFORE
   // dump_2.png because string comparison stops at the first '1' < '2'
   // character — scrambling playback order for animations with 10000+ frames.
   //
   // The frameIndex helper now parses the embedded integer so the ordering
   // is purely numeric.
   test("frame indices are extracted as integers, not strings") {
      AnimDumpHandler.frameIndex(Paths.get("dump_0000.png")) shouldBe 0
      AnimDumpHandler.frameIndex(Paths.get("dump_0001.png")) shouldBe 1
      AnimDumpHandler.frameIndex(Paths.get("dump_0002.png")) shouldBe 2
      AnimDumpHandler.frameIndex(Paths.get("dump_9999.png")) shouldBe 9999
      AnimDumpHandler.frameIndex(Paths.get("dump_10000.png")) shouldBe 10000
      AnimDumpHandler.frameIndex(Paths.get("dump_99999.png")) shouldBe 99999
   }

   test("a numeric sort by frameIndex orders dump_10000 after dump_9999, not after dump_1") {
      val names = listOf(
         "dump_0001.png",
         "dump_10000.png",
         "dump_0002.png",
         "dump_9999.png",
         "dump_0010.png",
      )
      val sortedByLex = names.sorted()
      val sortedByIndex = names.sortedBy { AnimDumpHandler.frameIndex(Paths.get(it)) }

      // Lexicographic sort scrambles dump_10000 in among the small numbers.
      sortedByLex shouldBe listOf(
         "dump_0001.png", "dump_0002.png", "dump_0010.png",
         "dump_10000.png",  // <-- wrong: lands between 10 and 9999 because "1" < "9"
         "dump_9999.png",
      )

      // Numeric sort gives playback order.
      sortedByIndex shouldBe listOf(
         "dump_0001.png", "dump_0002.png", "dump_0010.png",
         "dump_9999.png", "dump_10000.png",
      )
   }
})
