Grayscale
=========

Returns a new grayscale copy of the image, where every pixel is replaced by a shade of grey
of equivalent brightness. The alpha channel is preserved.

The no-argument form uses the default `LUMA` method, which weights the channels by their
perceived brightness using the Rec. 709 coefficients (`0.2126*R + 0.7152*G + 0.0722*B`):

```kotlin
image.toGrayscale()
```

To use a different weighting, pass an explicit `GrayscaleMethod`:

```kotlin
import com.sksamuel.scrimage.color.GrayscaleMethod

image.toGrayscale(GrayscaleMethod.AVERAGE)   // (R + G + B) / 3
image.toGrayscale(GrayscaleMethod.LUMA)      // Rec. 709 perceived luminance (the default)
image.toGrayscale(GrayscaleMethod.WEIGHTED)  // Rec. 601 perceived luminance
```
