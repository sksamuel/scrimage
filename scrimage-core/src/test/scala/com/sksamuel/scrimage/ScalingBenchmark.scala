package com.sksamuel.scrimage

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import org.apache.commons.io.IOUtils

// import org.imgscalr.Scalr
// import org.imgscalr.Scalr.{ Mode, Method }
import java.io.File
import thirdparty.mortennobel.{ResampleFilters, ResampleOp}

class ScalingBenchmark { // extends FunSuite {

  val COUNT                   = 100
  val root                    = new File(".")
  val bird    : Array[Byte]   = IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg"))
  val awt     : BufferedImage = ImageIO.read(new ByteArrayInputStream(bird))
  // val in = getResourceAsStream("./bird.jpg")
  val scrimage: Image         = Image.fromAwt(awt)
  // val awt = scrimage.awt
  val width   : Int           = scrimage.width
  val height  : Int           = scrimage.height

  var start: Long = System.currentTimeMillis()
  var time : Long = System.currentTimeMillis() - start

  // println("Testing java.awt.Image.getScaledInstance() High Quality")
  // start = System.currentTimeMillis()
  // for (i <- 1 to COUNT) {
  //   val scaled = awt.getScaledInstance(width / 3, height / 3, java.awt.Image.SCALE_AREA_AVERAGING)
  //   assert(scaled.getWidth(null) == width / 3)
  //   assert(scaled.getHeight(null) == height / 3)
  // }
  // time = System.currentTimeMillis() - start
  // println(s"Time: $time ms")

  // println("Testing java.awt.Image.getScaledInstance() Fast")
  // start = System.currentTimeMillis()
  // for (i <- 1 to COUNT) {
  //   val scaled = awt.getScaledInstance(width / 3, height / 3, java.awt.Image.SCALE_FAST)
  //   assert(scaled.getWidth(null) == width / 3)
  //   assert(scaled.getHeight(null) == height / 3)
  // }
  // time = System.currentTimeMillis() - start
  // println(s"Time: $time ms")

  // println("Testing ImgScalr High Quality")
  // start = System.currentTimeMillis()
  // for (i <- 1 to COUNT) {
  //   val scaled = Scalr.resize(awt, Method.QUALITY, Mode.FIT_EXACT, width / 3, height / 3)
  //   assert(scaled.getWidth(null) == width / 3)
  //   assert(scaled.getHeight(null) == height / 3)
  // }
  // time = System.currentTimeMillis() - start
  // println(s"Time: $time ms")

  // println("Testing ImgScalr Fast")
  // start = System.currentTimeMillis()
  // for (i <- 1 to COUNT) {
  //   val scaled = Scalr.resize(awt, Method.SPEED, Mode.FIT_EXACT, width / 3, height / 3)
  //   assert(scaled.getWidth(null) == width / 3)
  //   assert(scaled.getHeight(null) == height / 3)
  // }
  // time = System.currentTimeMillis() - start
  // println(s"Time: $time ms")

  var counter = 0
  println("Testing Mortennobel Bicubic")
  start = System.currentTimeMillis()
  counter = 0
  while (counter < COUNT) {
    val op = new ResampleOp(ResampleFilters.biCubicFilter, width / 3, height / 3)
    val scaled = Image.fromAwt(op.filter(awt, null))
    assert(scaled.width == width / 3)
    assert(scaled.height == height / 3)
    counter += 1
  }
  time = System.currentTimeMillis() - start
  println(s"Time: $time ms")

  // println("Testing Scrimage Fast")
  // start = System.currentTimeMillis()
  // for (i <- 1 to COUNT) {
  //   val scaled = scrimage.scaleTo(width / 3, height / 3, FastScale)
  //   assert(scaled.width == width / 3)
  //   assert(scaled.height == height / 3)
  // }
  // time = System.currentTimeMillis() - start
  // println(s"Time: $time ms")

  val size = 10000
  val redInt = 0xffff0000
  var x = 0
  var y = 0
  println(s"Writing one by one pixels in a $size x $size java BufferedImage")
  start = System.currentTimeMillis()
  var buffered = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
  y = 0
  while (y < size) {
    x = 0
    while (x < size) {
      buffered.setRGB(x, y, redInt)
      x += 1
    }
    y += 1
  }
  time = System.currentTimeMillis() - start
  println(s"Time: $time ms")
  buffered = null

}
