package com.sksamuel.scrimage.nio

import java.io.File

import com.sksamuel.scrimage.Image
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

/** @author Stephen Samuel */
class JpegWriterTest extends FunSuite with BeforeAndAfter with Matchers {

  val original = Image.fromResource("/com/sksamuel/scrimage/bird.jpg").scaleTo(600, 400)

  test("jpeg compression happy path") {
    for ( i <- 0 to 100 by 10 ) {
      original.bytes(JpegWriter().withCompression(i)) // make sure no exceptions for each format level
    }
  }

  test("issue 84 - jpeg writing with alpha") {
    val img = Image.fromResource("/com/sksamuel/scrimage/nio/issue84.jpg")
    val w = JpegWriter()
    img.bytes(w) // was throwing with bug
  }

  test("preserve metadata") {
    val image = Image.fromResource("/com/sksamuel/scrimage/metadata/gibson.jpg")
    println(image.metadata.tags)
    val file = image.output(File.createTempFile("metadata", "jpg"))(JpegWriter.Default)
    val image2 = Image.fromFile(file)
    println(image2.metadata.tags)
    for ( tag <- image.metadata.tags ) {
      image2.metadata.tags.find(_.name == tag.name) shouldBe Some(tag)
    }
  }
}
