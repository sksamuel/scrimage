package com.sksamuel.scrimage

/** @author Stephen Samuel */
sealed abstract class Color(val value: Int)
object Color {
    case object Black extends Color(0x000)
    case object White extends Color(0xFFF)
    case object Red extends Color(0xF00)
    case object Green extends Color(0x0F0)
    case object Blue extends Color(0x00F)
}