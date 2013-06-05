package com.sksamuel.scrimage

/** @author Stephen Samuel */
sealed abstract class Color(val value: Int)
object Color {
    case object Black extends Color(0x000000)
    case object White extends Color(0xFFFFFF)
    case object Red extends Color(0xFF0000)
    case object Green extends Color(0x00FF00)
    case object Blue extends Color(0x0000FF)
}