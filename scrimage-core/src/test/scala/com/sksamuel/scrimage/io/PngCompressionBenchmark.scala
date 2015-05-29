package com.sksamuel.scrimage.io

import com.sksamuel.scrimage.{ Format, Image }
import scala.concurrent.duration._
import java.io.File

/** @author Stephen Samuel */
object PngCompressionBenchmark extends App {

  val image = Image(getClass.getResourceAsStream("/com/sksamuel/scrimage/jazz.jpg"))

  for (c <- 0 to 9) {
    benchmarkN(1, string => println(s"Compression level $c took " + string)) {
      image.writer(Format.PNG).withCompression(c).write(File.createTempFile(s"compressiontest$c", ".png"))
    }
  }

  def benchmarkN[A](n: Int, pr: String => Unit)(f: => A): Unit = {
    val stopwatch = new Stopwatch
    stopwatch.start()
    for (k <- 0 until n) f
    stopwatch.stop()
    pr(stopwatch.elapsed.toMillis / n + "ms")
  }
}

class Stopwatch {
  var _start = 0l
  var _end = 0l
  def start() = {
    _start = System.nanoTime()
  }
  def stop() = {
    _end = System.nanoTime()
  }
  def elapsed: FiniteDuration = (_end - _start).nanos
}
