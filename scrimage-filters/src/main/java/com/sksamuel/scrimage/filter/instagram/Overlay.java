package com.sksamuel.scrimage.filter.instagram;

/**
 * A colour overlay layer composited over the image with a {@link BlendMode},
 * matching the {@code ::before} pseudo-element of the Instagram CSS filters.
 *
 * <p>Supports a solid colour, a vertical (top-to-bottom) linear gradient, and a
 * centred radial gradient sized to the closest corner. Gradients are defined by
 * two stops, each with an offset in [0, 1] and a straight (non-premultiplied)
 * RGBA colour.
 */
public final class Overlay {

   private enum Kind {SOLID, LINEAR, RADIAL}

   private final Kind kind;
   private final BlendMode blend;
   private final float o0, r0, g0, b0, a0;
   private final float o1, r1, g1, b1, a1;

   private Overlay(Kind kind, BlendMode blend,
                   float o0, float r0, float g0, float b0, float a0,
                   float o1, float r1, float g1, float b1, float a1) {
      this.kind = kind;
      this.blend = blend;
      this.o0 = o0; this.r0 = r0; this.g0 = g0; this.b0 = b0; this.a0 = a0;
      this.o1 = o1; this.r1 = r1; this.g1 = g1; this.b1 = b1; this.a1 = a1;
   }

   /**
    * A flat colour overlay (r, g, b in [0, 255], alpha in [0, 1]).
    *
    * @return a solid colour Overlay.
    */
   public static Overlay solid(int r, int g, int b, float alpha, BlendMode blend) {
      float rf = r / 255f, gf = g / 255f, bf = b / 255f;
      return new Overlay(Kind.SOLID, blend, 0f, rf, gf, bf, alpha, 1f, rf, gf, bf, alpha);
   }

   /**
    * A top-to-bottom linear gradient between two RGBA stops.
    *
    * @return a linear gradient Overlay.
    */
   public static Overlay linear(BlendMode blend,
                                float o0, int r0, int g0, int b0, float a0,
                                float o1, int r1, int g1, int b1, float a1) {
      return new Overlay(Kind.LINEAR, blend,
         o0, r0 / 255f, g0 / 255f, b0 / 255f, a0,
         o1, r1 / 255f, g1 / 255f, b1 / 255f, a1);
   }

   /**
    * A centred radial gradient (sized to the closest corner) between two RGBA stops.
    *
    * @return a radial gradient Overlay.
    */
   public static Overlay radial(BlendMode blend,
                                float o0, int r0, int g0, int b0, float a0,
                                float o1, int r1, int g1, int b1, float a1) {
      return new Overlay(Kind.RADIAL, blend,
         o0, r0 / 255f, g0 / 255f, b0 / 255f, a0,
         o1, r1 / 255f, g1 / 255f, b1 / 255f, a1);
   }

   private static int to8(float v) {
      int i = Math.round(v * 255f);
      return i < 0 ? 0 : (i > 255 ? 255 : i);
   }

   /** Composite this overlay over the given ARGB pixel buffer in place. */
   void apply(int[] px, int w, int h) {
      double cx = w / 2.0, cy = h / 2.0;
      double maxR = Math.sqrt(cx * cx + cy * cy); // closest corner of a centred circle
      float span = o1 - o0;
      for (int y = 0; y < h; y++) {
         for (int x = 0; x < w; x++) {
            float t;
            switch (kind) {
               case LINEAR:
                  t = h > 1 ? (float) y / (h - 1) : 0f;
                  break;
               case RADIAL:
                  double dx = x - cx, dy = y - cy;
                  t = maxR > 0 ? (float) (Math.sqrt(dx * dx + dy * dy) / maxR) : 0f;
                  break;
               default:
                  t = 0f;
            }
            float tt = kind == Kind.SOLID || span <= 0f ? (kind == Kind.SOLID ? 0f : 1f) : (t - o0) / span;
            if (tt < 0f) tt = 0f;
            else if (tt > 1f) tt = 1f;

            float sr = r0 + (r1 - r0) * tt;
            float sg = g0 + (g1 - g0) * tt;
            float sb = b0 + (b1 - b0) * tt;
            float sa = a0 + (a1 - a0) * tt;

            int i = y * w + x;
            int argb = px[i];
            int al = argb >>> 24;
            float br = ((argb >> 16) & 0xff) / 255f;
            float bg = ((argb >> 8) & 0xff) / 255f;
            float bb = (argb & 0xff) / 255f;

            float rr = br * (1f - sa) + blend.blend(br, sr) * sa;
            float rg = bg * (1f - sa) + blend.blend(bg, sg) * sa;
            float rb = bb * (1f - sa) + blend.blend(bb, sb) * sa;

            px[i] = (al << 24) | (to8(rr) << 16) | (to8(rg) << 8) | to8(rb);
         }
      }
   }
}
