package com.sksamuel.scrimage;

public enum ScaleMethod {
   FastScale,
   Lanczos3,
   BSpline,
   Bilinear,
   Bicubic,
   /**
    * Uses a progressive bilinear on downscale and a bicubic on upscale
    */
   Progressive,
}
