package com.sksamuel.scrimage.filter

import thirdparty.BlendComposite
import com.sksamuel.scrimage.CompositeFilter

/** @author Stephen Samuel
  *
  *         A Filter that will put you on ice!
  *
  **/
object FreezeFilter extends CompositeFilter {
    val composite = BlendComposite.Glow
}
