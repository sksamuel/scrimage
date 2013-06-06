package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.CompositeFilter
import thirdparty.romainguy.BlendComposite

/** @author Stephen Samuel
  *
  *         A Filter that will put you on ice!
  *
  **/
object FreezeFilter extends CompositeFilter {
    val composite = BlendComposite.Glow
}
