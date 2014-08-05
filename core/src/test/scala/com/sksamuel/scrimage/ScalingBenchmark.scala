package com.sksamuel.scrimage

import com.sksamuel.scrimage.scaling.ResampleOpScala
import org.apache.commons.io.IOUtils
import javax.imageio.ImageIO
import java.io.ByteArrayInputStream
// import org.imgscalr.Scalr
// import org.imgscalr.Scalr.{ Mode, Method }
import com.sksamuel.scrimage.ScaleMethod.{ FastScale, Bicubic }
import thirdparty.mortennobel.{ResampleFilters, ResampleOp}
import java.io.File
import org.scalatest.FunSuite

/** @author Stephen Samuel */
class ScalingBenchmark extends FunSuite {

  val COUNT = 2
  val root = new File(".")
  val bird = IOUtils.toByteArray(getClass.getResourceAsStream("/com/sksamuel/scrimage/bird.jpg"))
  val awt = ImageIO.read(new ByteArrayInputStream(bird))
  // val in = getResourceAsStream("./bird.jpg")
  val scrimage = Image(awt)
  // val awt = scrimage.awt
  val width = scrimage.width
  val height = scrimage.height

  var start = System.currentTimeMillis()
  var time = System.currentTimeMillis() - start


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

  println("Testing Mortennobel Bicubic")
  start = System.currentTimeMillis()
  for (i <- 1 to COUNT) {
    val op = new ResampleOp(Image.SCALE_THREADS, ResampleFilters.biCubicFilter, width / 3, height / 3)
    val scaled = Image(op.filter(scrimage.awt, null))
    assert(scaled.width == width / 3)
    assert(scaled.height == height / 3)
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

  println("Testing Mortennobel Bicubic in scala")
  start = System.currentTimeMillis()
  for (i <- 1 to COUNT) {
    val scaled = ResampleOpScala.scaleTo(ResampleOpScala.bicubicFilter)(scrimage)(width / 3, height / 3, Image.SCALE_THREADS)
    assert(scaled.width == width / 3)
    assert(scaled.height == height / 3)
  }
  time = System.currentTimeMillis() - start
  println(s"Time: $time ms")
}
