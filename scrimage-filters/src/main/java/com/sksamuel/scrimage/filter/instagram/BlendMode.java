package com.sksamuel.scrimage.filter.instagram;

/**
 * Clean-room implementations of the CSS {@code mix-blend-mode} functions used by
 * the Instagram filter overlays. Each blends a single normalised channel value
 * of the source (overlay) over the backdrop (image), both in the range [0, 1].
 *
 * <p>Formulas follow the W3C Compositing and Blending Level 1 specification.
 */
public enum BlendMode {

   NORMAL {
      @Override
      public float blend(float backdrop, float source) {
         return source;
      }
   },
   MULTIPLY {
      @Override
      public float blend(float backdrop, float source) {
         return backdrop * source;
      }
   },
   SCREEN {
      @Override
      public float blend(float backdrop, float source) {
         return 1f - (1f - backdrop) * (1f - source);
      }
   },
   OVERLAY {
      @Override
      public float blend(float backdrop, float source) {
         // overlay(b, s) == hard-light(s, b)
         return backdrop <= 0.5f ? 2f * backdrop * source : 1f - 2f * (1f - backdrop) * (1f - source);
      }
   },
   DARKEN {
      @Override
      public float blend(float backdrop, float source) {
         return Math.min(backdrop, source);
      }
   },
   LIGHTEN {
      @Override
      public float blend(float backdrop, float source) {
         return Math.max(backdrop, source);
      }
   },
   SOFT_LIGHT {
      @Override
      public float blend(float backdrop, float source) {
         if (source <= 0.5f) {
            return backdrop - (1f - 2f * source) * backdrop * (1f - backdrop);
         }
         float d = backdrop <= 0.25f
            ? ((16f * backdrop - 12f) * backdrop + 4f) * backdrop
            : (float) Math.sqrt(backdrop);
         return backdrop + (2f * source - 1f) * (d - backdrop);
      }
   };

   /**
    * Blend one normalised channel value of the source over the backdrop.
    *
    * @param backdrop the existing image channel value in [0, 1]
    * @param source   the overlay channel value in [0, 1]
    * @return the blended channel value in [0, 1]
    */
   public abstract float blend(float backdrop, float source);
}
