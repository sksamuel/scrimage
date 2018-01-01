package com.sksamuel.scrimage

import com.sksamuel.scrimage.nio.PngWriter
import com.sksamuel.scrimage.scaling.{AwtNearestNeighbourScale, ScrimageNearestNeighbourScale}

object NearestNeighbourBenchmark extends App {

  implicit val writer = PngWriter.MaxCompression

  val Iterations = 100
  val image = Image.fromResource("/earth-map-huge.jpg")

  bench(300, 200)
  bench(1000, 600)
  bench(2000, 1200)
  bench(3200, 1600)
  bench(4800, 2400)

  def bench(w: Int, h: Int): Unit = {
    println(s"Testing scale from ${image.width},${image.height} to $w,$h")
    val sw = new Stopwatch
    sw.start()
    for ( _ <- 0 until Iterations ) {
      val scaled = new AwtNearestNeighbourScale().scale(image.awt, w, h)
      sw.lap
      require(scaled.getWidth == w)
      require(scaled.getHeight == h)
    }
    sw.stop()
    println("AWT:")
    println("Total time: " + sw.elapsed.toMillis)
    println("Average time: " + sw.averageLap.toMillis)
    println()

    Image.fromAwt(new AwtNearestNeighbourScale().scale(image.awt, w, h)).output("awt.jpg")

    sw.reset()
    sw.start()

    for ( _ <- 0 until Iterations ) {
      val scaled = new ScrimageNearestNeighbourScale().scale(image.awt, w, h)
      sw.lap
      require(scaled.getWidth == w)
      require(scaled.getHeight == h)
    }
    sw.stop()
    println("Scrimage:")
    println("Total time: " + sw.elapsed.toMillis)
    println("Average time: " + sw.averageLap.toMillis)
    println("-----")

    Image.fromAwt(new ScrimageNearestNeighbourScale().scale(image.awt, w, h)).output("scrimage.jpg")

  }
}
