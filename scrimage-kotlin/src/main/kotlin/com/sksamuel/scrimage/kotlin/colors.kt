package com.sksamuel.scrimage.kotlin

import com.sksamuel.scrimage.color.RGBColor
import java.awt.Color

fun Color.toRGBColor(): RGBColor = com.sksamuel.scrimage.color.RGBColor.fromAwt(this)