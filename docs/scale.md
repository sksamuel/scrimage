Scale
=====

Scales an image up or down. This operation will change both the canvas and the image.
This is what most people want when they think of "resizing" an image.

If instead, we wish to change the image "canvas" without scaling up or down, then [resize](resize.md) is the right operation.

We can perform a scale operation with a specified width and height. Or we can specify only a width or height
and Scrimage will adjust the other dimension to keep the aspect ratio constant.

Alternatively, we can specify a scale factor, which will multiply the width and height by that factor.



### Examples

Using this image as our input:

![source image](images/input_640_360.jpg)


```kotlin
image.scaleToWidth(400) // keeps aspect ratio
```

![scale](images/scale_h200.jpg)

```kotlin
image.scaleToHeight(200) // keeps aspect ratio
```

![scale](images/scale_w400.jpg)

```kotlin
image.scaleTo(400, 400)
```

![scale](images/scale_400_400.jpg)

```kotlin
// using the fast scale method
image.scaleTo(400, 400, ScaleMethod.FastScale)
```

![scale](images/scale_400_400_fast.jpg)

```kotlin
image.scale(0.5) // keeps aspect ratio, applies factor to width and height
```

![scale](images/scale_0.5.jpg)


### Scale Algorithms

Each scale operation has a `ScaleMethod` parameter that allows us to specify the algorithm to be used when
performing the scale. Options are `FastScale`, `Lanczos3`, `BSpline`, `Bilinear`, `Bicubic`. Bicucbic is the default.

Here is the same image scaled using each of the algorithms. Click on the image to see the full size.

*Fast Scale*

[![fast scale](images/scale_fast_scale.jpg)](images/scale_fast_scale.jpg)

*BSpline*

[![fast scale](images/scale_bspline.jpg)](images/scale_bspline.jpg)

*Lanczos3*

[![fast scale](images/scale_lanczos3.jpg)](images/scale_lanczos3.jpg)

*Bilinear*

[![fast scale](images/scale_bilinear.jpg)](images/scale_bilinear.jpg)

*Bicubic*

[![fast scale](images/scale_bicubic.jpg)](images/scale_bicubic.jpg)

