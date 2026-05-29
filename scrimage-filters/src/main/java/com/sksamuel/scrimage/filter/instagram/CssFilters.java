package com.sksamuel.scrimage.filter.instagram;

/**
 * Clean-room implementations of the CSS filter functions (W3C Filter Effects
 * Module Level 1) used by the Instagram filters. Each factory returns a
 * {@link CssOp} that transforms a normalised RGB triple in [0, 1].
 *
 * <p>{@code brightness} and {@code contrast} are per-channel affine operations;
 * {@code saturate}, {@code grayscale}, {@code sepia} and {@code hueRotate} are
 * the linear colour matrices defined by the specification. Values are clamped to
 * [0, 1] after each operation, matching browser behaviour.
 */
public final class CssFilters {

   private CssFilters() {
   }

   private static float clamp(float v) {
      return v < 0f ? 0f : (v > 1f ? 1f : v);
   }

   /**
    * {@code brightness(amount)} multiplies each channel by amount.
    *
    * @return a CssOp applying the brightness adjustment
    */
   public static CssOp brightness(float amount) {
      return rgb -> {
         rgb[0] = clamp(rgb[0] * amount);
         rgb[1] = clamp(rgb[1] * amount);
         rgb[2] = clamp(rgb[2] * amount);
      };
   }

   /**
    * {@code contrast(amount)} scales each channel around the 0.5 midpoint.
    *
    * @return a CssOp applying the contrast adjustment
    */
   public static CssOp contrast(float amount) {
      return rgb -> {
         rgb[0] = clamp((rgb[0] - 0.5f) * amount + 0.5f);
         rgb[1] = clamp((rgb[1] - 0.5f) * amount + 0.5f);
         rgb[2] = clamp((rgb[2] - 0.5f) * amount + 0.5f);
      };
   }

   /**
    * {@code saturate(amount)}; 0 is greyscale, 1 is identity, &gt;1 over-saturates.
    *
    * @return a CssOp applying the saturation adjustment
    */
   public static CssOp saturate(float amount) {
      return matrix(saturateMatrix(amount));
   }

   /**
    * {@code grayscale(amount)}; 1 is fully grey. Equivalent to saturate(1 - amount).
    *
    * @return a CssOp applying the grayscale adjustment
    */
   public static CssOp grayscale(float amount) {
      return matrix(saturateMatrix(1f - amount));
   }

   /**
    * {@code sepia(amount)}; 0 is identity, 1 is full sepia.
    *
    * @return a CssOp applying the sepia adjustment
    */
   public static CssOp sepia(float amount) {
      return matrix(sepiaMatrix(amount));
   }

   /**
    * {@code hue-rotate(degrees)} rotates the hue around the colour wheel.
    *
    * @return a CssOp applying the hue rotation
    */
   public static CssOp hueRotate(float degrees) {
      return matrix(hueRotateMatrix(degrees));
   }

   private static CssOp matrix(float[] m) {
      return rgb -> {
         float r = rgb[0], g = rgb[1], b = rgb[2];
         rgb[0] = clamp(m[0] * r + m[1] * g + m[2] * b);
         rgb[1] = clamp(m[3] * r + m[4] * g + m[5] * b);
         rgb[2] = clamp(m[6] * r + m[7] * g + m[8] * b);
      };
   }

   private static float[] saturateMatrix(float s) {
      return new float[]{
         0.213f + 0.787f * s, 0.715f - 0.715f * s, 0.072f - 0.072f * s,
         0.213f - 0.213f * s, 0.715f + 0.285f * s, 0.072f - 0.072f * s,
         0.213f - 0.213f * s, 0.715f - 0.715f * s, 0.072f + 0.928f * s
      };
   }

   private static float[] sepiaMatrix(float amount) {
      float n = 1f - amount;
      return new float[]{
         0.393f + 0.607f * n, 0.769f - 0.769f * n, 0.189f - 0.189f * n,
         0.349f - 0.349f * n, 0.686f + 0.314f * n, 0.168f - 0.168f * n,
         0.272f - 0.272f * n, 0.534f - 0.534f * n, 0.131f + 0.869f * n
      };
   }

   private static float[] hueRotateMatrix(float degrees) {
      double rad = Math.toRadians(degrees);
      float c = (float) Math.cos(rad);
      float s = (float) Math.sin(rad);
      return new float[]{
         0.213f + c * 0.787f - s * 0.213f, 0.715f - c * 0.715f - s * 0.715f, 0.072f - c * 0.072f + s * 0.928f,
         0.213f - c * 0.213f + s * 0.143f, 0.715f + c * 0.285f + s * 0.140f, 0.072f - c * 0.072f - s * 0.283f,
         0.213f - c * 0.213f - s * 0.787f, 0.715f - c * 0.715f + s * 0.715f, 0.072f + c * 0.928f + s * 0.072f
      };
   }
}
