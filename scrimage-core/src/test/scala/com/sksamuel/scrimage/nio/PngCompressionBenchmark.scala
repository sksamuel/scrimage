package com.sksamuel.scrimage.nio

import java.io.File

import com.sksamuel.scrimage.Image

import scala.concurrent.duration._

object PngCompressionBenchmark extends App {

  val image = Image.fromResource("/com/sksamuel/scrimage/jazz.jpg")

  for (c <- 0 to 9) {
    benchmarkN(1, string => println(s"Compression level $c took " + string)) {
      image.output(File.createTempFile(s"compressiontest$c", ".png"))(new PngWriter(c))
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
  def start(): Unit = {
    _start = System.nanoTime()
  }
  def stop(): Unit = {
    _end = System.nanoTime()
  }
  def elapsed: FiniteDuration = (_end - _start).nanos
}
