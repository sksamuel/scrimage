package com.sksamuel.scrimage

import com.sksamuel.scrimage.scaling.NearestNeighbour

object NearestNeighbourBenchmark extends App {

  val Iterations = 100
  val image = Image.fromResource("/earth-map-huge.jpg")

  bench(300, 200)
  bench(1000, 600)
  bench(2000, 1200)
  bench(3200, 1600)
  bench(6000, 3000)

  def bench(w: Int, h: Int): Unit = {
    println(s"Testing scale from ${image.width},${image.height} to $w,$h")
    val sw = new Stopwatch
    sw.start()
    for ( _ <- 0 until Iterations ) {
      val scaled = image.awt.getScaledInstance(w, h, java.awt.Image.SCALE_FAST)
      sw.lap
      require(scaled.getWidth(null) == w)
      require(scaled.getHeight(null) == h)
    }
    sw.stop()
    println("AWT:")
    println("Total time: " + sw.elapsed.toMillis)
    println("Average time: " + sw.averageLap.toMillis)
    println()

    Image.fromAwt(image.awt.getScaledInstance(w, h, java.awt.Image.SCALE_FAST)).output("awt.jpg")

    sw.reset()
    sw.start()

    for ( _ <- 0 until Iterations ) {
      val scaled = NearestNeighbour.scale(image.awt, w, h)
      sw.lap
      require(scaled.getWidth(null) == w)
      require(scaled.getHeight(null) == h)
    }
    sw.stop()
    println("Scrimage:")
    println("Total time: " + sw.elapsed.toMillis)
    println("Average time: " + sw.averageLap.toMillis)
    println("-----")

    Image.fromAwt(NearestNeighbour.scale(image.awt, w, h)).output("scrimage.jpg")

  }
}
