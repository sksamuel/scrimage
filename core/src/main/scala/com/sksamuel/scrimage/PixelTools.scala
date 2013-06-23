package com.sksamuel.scrimage

/** @author Stephen Samuel */
object PixelTools {

    def rgb(r: Int, g: Int, b: Int): Int = 0xFF << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF) << 0
    def alpha(pixel: Int): Int = pixel >> 24 & 0xFF
    def red(pixel: Int): Int = pixel >> 16 & 0xFF
    def green(pixel: Int): Int = pixel >> 8 & 0xFF
    def blue(pixel: Int): Int = pixel & 0xFF
}
