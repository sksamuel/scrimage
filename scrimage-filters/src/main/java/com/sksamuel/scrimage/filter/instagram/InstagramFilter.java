package com.sksamuel.scrimage.filter.instagram;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.filter.Filter;

import java.util.List;

/**
 * Base class for the clean-room Instagram filters. Each filter is defined by an
 * ordered list of {@link CssOp} colour adjustments (the CSS {@code filter:}
 * functions) and an optional {@link Overlay} composited afterwards (the CSS
 * {@code ::before} pseudo-element).
 *
 * <p>The colour adjustments are applied per pixel in order; the alpha channel is
 * preserved. The overlay, if any, is then blended over the whole image.
 */
public abstract class InstagramFilter implements Filter {

   private final List<CssOp> ops;
   private final Overlay overlay;

   protected InstagramFilter(List<CssOp> ops, Overlay overlay) {
      this.ops = ops;
      this.overlay = overlay;
   }

   protected InstagramFilter(List<CssOp> ops) {
      this(ops, null);
   }

   private static int to8(float v) {
      int i = Math.round(v * 255f);
      return i < 0 ? 0 : (i > 255 ? 255 : i);
   }

   @Override
   public void apply(ImmutableImage image) {
      int w = image.width;
      int h = image.height;
      int[] px = image.awt().getRGB(0, 0, w, h, null, 0, w);
      float[] rgb = new float[3];
      for (int i = 0; i < px.length; i++) {
         int argb = px[i];
         int al = argb >>> 24;
         rgb[0] = ((argb >> 16) & 0xff) / 255f;
         rgb[1] = ((argb >> 8) & 0xff) / 255f;
         rgb[2] = (argb & 0xff) / 255f;
         for (CssOp op : ops) {
            op.apply(rgb);
         }
         px[i] = (al << 24) | (to8(rgb[0]) << 16) | (to8(rgb[1]) << 8) | to8(rgb[2]);
      }
      if (overlay != null) {
         overlay.apply(px, w, h);
      }
      image.awt().setRGB(0, 0, w, h, px, 0, w);
   }
}
