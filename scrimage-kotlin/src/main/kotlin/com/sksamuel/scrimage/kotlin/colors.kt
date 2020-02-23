@file:Suppress("unused")

package com.sksamuel.scrimage.kotlin

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.color.RGBColor
import java.awt.Color
import java.awt.image.BufferedImage

fun Color.toRGBColor(): RGBColor = com.sksamuel.scrimage.color.RGBColor.fromAwt(this)

fun BufferedImage.toImmutableImage() = ImmutableImage.wrapAwt(this)