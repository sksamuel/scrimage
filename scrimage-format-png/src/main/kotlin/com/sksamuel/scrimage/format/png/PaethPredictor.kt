package com.sksamuel.scrimage.format.png

import kotlin.math.abs

object PaethPredictor {
   fun predict(left: Int, up: Int, upleft: Int): Int {
      val p = left + up - upleft   // initial estimate
      val pa = abs(p - left) // distances to a, b, c
      val pb = abs(p - up)
      val pc = abs(p - upleft)
      // return nearest of a, b, c
      // breaking ties in order a, b, c
      return when {
         pa <= pb && pa <= pc -> left
         pb <= pc -> up
         else -> upleft
      }
   }
}
