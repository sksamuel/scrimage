package com.sksamuel.scrimage

/** @author Stephen Samuel */
trait Composite {

    /**
     * Apply the given applicative image onto the given source image using this composite.
     */
    def apply(src: Image, applicative: Image)
}