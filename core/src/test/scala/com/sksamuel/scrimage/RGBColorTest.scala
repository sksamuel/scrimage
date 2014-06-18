package com.sksamuel.scrimage

import com.sksamuel.scrimage.Color._
import org.scalatest.{FlatSpec, OneInstancePerTest}

/** @author Stephen Samuel */
class RGBColorTest extends FlatSpec with OneInstancePerTest {

  "an RGB color" should "convert to hex" in {
    assert("FF00FF" === Color(255, 0, 255).toHex)
    assert("FFFFFF" === Color(255, 255, 255).toHex)
    assert("EFEFEF" === Color(239, 239, 239).toHex)
    assert("00000F" === Color(0, 0, 15).toHex)
    assert("000000" === Color(0, 0, 0).toHex)
  }

  it should "convert to an integer using correct bit shifting" in {
    assert(0xFFEFDFCF === Color(239, 223, 207).argb)
  }

  it should "convert to awt.color" in {
    assert(new java.awt.Color(255, 0, 255) === Color(255, 0, 255).toAWT)
    assert(new java.awt.Color(255, 250, 255) === Color(255, 250, 255).toAWT)
    assert(new java.awt.Color(14, 250, 255) === Color(14, 250, 255).toAWT)
    assert(new java.awt.Color(255, 0, 0) === Color(255, 0, 0).toAWT)
  }

  it should "convert from awt.color" in {
    import Color._
    assert(awt2color(new java.awt.Color(255, 0, 255)) === Color(255, 0, 255))
    assert(awt2color(new java.awt.Color(255, 250, 255)) === Color(255, 250, 255))
    assert(awt2color(new java.awt.Color(14, 250, 255)) === Color(14, 250, 255))
    assert(awt2color(new java.awt.Color(255, 0, 0)) === Color(255, 0, 0))
  }

  it should "convert from int to color using correct bit shifting" in {
    assert(Color(1088511) === Color(16, 155, 255, 0))
  }
}
