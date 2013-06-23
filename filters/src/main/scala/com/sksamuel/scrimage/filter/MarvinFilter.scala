package com.sksamuel.scrimage.filter

import com.sksamuel.scrimage.{Image, Filter}
import thirdparty.marvin.image.{MarvinImageMask, MarvinAttributes, MarvinImage, MarvinAbstractImagePlugin}

/**
 * Implementation of Filter that provides filtering through delegation
 * to a plugin from the Marvin framework. The plugins are modified
 * so that the dependancy on the marvin gui is removed.
 *
 * @author Stephen Samuel
 **/
abstract class MarvinFilter extends Filter {
    val plugin: MarvinAbstractImagePlugin
    def apply(image: Image) {

        val input = new MarvinImage(image.awt)
        val output = input.clone()

        plugin.process(input, output, new MarvinAttributes(), MarvinImageMask.NULL_MASK, false)

        input.setIntColorArray(output.getIntColorArray)
        input.update()
    }
}