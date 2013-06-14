package com.sksamuel.scrimage

/** @author Stephen Samuel */
object PixelTools {

    def alpha(pixel: Int): Byte = (pixel >> 24 & 0xFF).toByte
    def red(pixel: Int): Byte = (pixel >> 16 & 0xFF).toByte
    def green(pixel: Int): Byte = (pixel >> 8 & 0xFF).toByte
    def blue(pixel: Int): Byte = (pixel & 0xFF).toByte
}
