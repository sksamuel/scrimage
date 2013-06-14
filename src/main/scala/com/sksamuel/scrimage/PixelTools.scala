package com.sksamuel.scrimage

/** @author Stephen Samuel */
object PixelTools {

    def alpha(pixel: Int): Int = pixel >> 24 & 0xFF
    def red(pixel: Int): Int = pixel >> 16 & 0xFF
    def green(pixel: Int): Int = pixel >> 8 & 0xFF
    def blue(pixel: Int): Int = pixel & 0xFF
}
