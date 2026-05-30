package com.sksamuel.scrimage.core.color

import com.sksamuel.scrimage.color.HSLColor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.StringSpec

class HSLColorTest : StringSpec({

   // The constructor previously validated arguments with `assert`, which is
   // disabled by default in production JVMs, allowing out-of-range components
   // to be stored and silently produce wrong colours in toRGB(). It now fails
   // fast with an IllegalArgumentException.

   "accepts in-range components including boundaries" {
      shouldNotThrowAny {
         HSLColor(0f, 0f, 0f, 0f)
         HSLColor(360f, 1f, 1f, 1f)
         HSLColor(180f, 0.5f, 0.5f, 0.5f)
      }
   }

   "rejects out-of-range hue" {
      shouldThrow<IllegalArgumentException> { HSLColor(-1f, 0f, 0f, 0f) }
      shouldThrow<IllegalArgumentException> { HSLColor(360.1f, 0f, 0f, 0f) }
   }

   "rejects out-of-range saturation" {
      shouldThrow<IllegalArgumentException> { HSLColor(0f, -0.1f, 0f, 0f) }
      shouldThrow<IllegalArgumentException> { HSLColor(0f, 1.1f, 0f, 0f) }
   }

   "rejects out-of-range lightness" {
      shouldThrow<IllegalArgumentException> { HSLColor(0f, 0f, -0.1f, 0f) }
      shouldThrow<IllegalArgumentException> { HSLColor(0f, 0f, 1.1f, 0f) }
   }

   "rejects out-of-range alpha" {
      shouldThrow<IllegalArgumentException> { HSLColor(0f, 0f, 0f, -0.1f) }
      shouldThrow<IllegalArgumentException> { HSLColor(0f, 0f, 0f, 1.1f) }
   }
})
