package com.sksamuel.scrimage

object NearestNeighbourBenchmark extends App {

  val image = Image.fromResource("/earth-map-huge.jpg")

  val sw = new Stopwatch
  sw.start()
  for ( _ <- 1 until 200 ) {
    val scaled = image.awt.getScaledInstance(100, 100, java.awt.Image.SCALE_FAST)
    sw.lap
    require(scaled.getWidth(null) == 100)
  }
  sw.stop()
  println("Total time: " + sw.elapsed.toMillis)
  println("Average time: " + sw.averageLap.toMillis)
}
