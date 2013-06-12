package com.sksamuel.scrimage

import org.apache.commons.io.IOUtils
import javax.imageio.ImageIO
import java.io.ByteArrayInputStream
import org.imgscalr.Scalr
import org.imgscalr.Scalr.{Mode, Method}
import com.sksamuel.scrimage.ScaleMethod.{FastScale, Bicubic}

/** @author Stephen Samuel */
object ScalingBenchmark extends App {

    val COUNT = 20

    val bird = IOUtils.toByteArray(getClass.getResourceAsStream("/bird.jpg"))
    val awt = ImageIO.read(new ByteArrayInputStream(bird))
    val scrimage = Image(bird)
    val width = scrimage.width
    val height = scrimage.height

    println("Testing java.awt.Image.getScaledInstance() High Quality")
    var start = System.currentTimeMillis()
    for ( i <- 1 to COUNT ) {
        val scaled = awt.getScaledInstance(width / 3, height / 3, java.awt.Image.SCALE_AREA_AVERAGING)
        assert(scaled.getWidth(null) == width / 3)
        assert(scaled.getHeight(null) == height / 3)
    }
    var time = System.currentTimeMillis() - start
    println(s"Time: $time ms")

    println("Testing java.awt.Image.getScaledInstance() Fast")
    start = System.currentTimeMillis()
    for ( i <- 1 to COUNT ) {
        val scaled = awt.getScaledInstance(width / 3, height / 3, java.awt.Image.SCALE_FAST)
        assert(scaled.getWidth(null) == width / 3)
        assert(scaled.getHeight(null) == height / 3)
    }
    time = System.currentTimeMillis() - start
    println(s"Time: $time ms")

    println("Testing ImgScalr High Quality")
    start = System.currentTimeMillis()
    for ( i <- 1 to COUNT ) {
        val scaled = Scalr.resize(awt, Method.QUALITY, Mode.FIT_EXACT, width / 3, height / 3)
        assert(scaled.getWidth(null) == width / 3)
        assert(scaled.getHeight(null) == height / 3)
    }
    time = System.currentTimeMillis() - start
    println(s"Time: $time ms")

    println("Testing ImgScalr Fast")
    start = System.currentTimeMillis()
    for ( i <- 1 to COUNT ) {
        val scaled = Scalr.resize(awt, Method.SPEED, Mode.FIT_EXACT, width / 3, height / 3)
        assert(scaled.getWidth(null) == width / 3)
        assert(scaled.getHeight(null) == height / 3)
    }
    time = System.currentTimeMillis() - start
    println(s"Time: $time ms")

    println("Testing Scrimage Bicubic")
    start = System.currentTimeMillis()
    for ( i <- 1 to COUNT ) {
        val scaled = scrimage.scaleTo(width / 3, height / 3, Bicubic)
        assert(scaled.width == width / 3)
        assert(scaled.height == height / 3)
    }
    time = System.currentTimeMillis() - start
    println(s"Time: $time ms")

    println("Testing Scrimage Fast")
    start = System.currentTimeMillis()
    for ( i <- 1 to COUNT ) {
        val scaled = scrimage.scaleTo(width / 3, height / 3, FastScale)
        assert(scaled.width == width / 3)
        assert(scaled.height == height / 3)
    }
    time = System.currentTimeMillis() - start
    println(s"Time: $time ms")
}
