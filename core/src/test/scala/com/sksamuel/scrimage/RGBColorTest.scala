package com.sksamuel.scrimage

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
}
