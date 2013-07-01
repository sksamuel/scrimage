package com.sksamuel.scrimage

/** @author Stephen Samuel */
trait Composite {

    /**
     * Applies this composite to the given src and dest images.
     *
     */
    def apply(src: Image, dest: Image)
}