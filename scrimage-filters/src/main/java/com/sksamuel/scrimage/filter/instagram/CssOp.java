package com.sksamuel.scrimage.filter.instagram;

/**
 * A single CSS filter-function operation that transforms a normalised RGB triple
 * in place. The array holds three values (r, g, b), each in the range [0, 1].
 */
@FunctionalInterface
public interface CssOp {
   void apply(float[] rgb);
}
