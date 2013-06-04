package com.sksamuel.scrimage

/** @author Stephen Samuel */
sealed trait Format
object Format {
    case object PNG extends Format
    case object JPEG extends Format
    case object GIF extends Format
}