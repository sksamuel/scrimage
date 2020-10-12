Scale
=====

Scales the image. This operation will change both the canvas and the image.
This is what most people want when they think of "resizing" an image.

We can perform a scale operation with a specified width and height. Or we can specify only a width or height
and Scrimage will adjust the other dimension to keep the aspect ratio constant.

Alternatively, we can specify a scale factor, which will multiply the width and height by that factor.

Each scale operation has a `ScaleMethod` parameter that allows us to specify the algorithm to be used when
performing the scale. Options are `FastScale`, `Lanczos3`, `BSpline`, `Bilinear`, `Bicubic`. Bicucbic is the default.



### Examples

Using this image as our input:

![source image](images/input_640_360.jpg)


```kotlin
image.scaleToWidth(400)
```

![scale](images/scale_h200.jpg)

```kotlin
image.scaleToHeight(200)
```

![scale](images/scale_w400.jpg)

```kotlin
image.scaleTo(400, 400)
```

![scale](images/scale_400_400.jpg)

```kotlin
image.scaleTo(400, 400, ScaleMethod.FastScale)
```

![scale](images/scale_400_400_fast.jpg)

```kotlin
image.scale(0.5)
```

![scale](images/scale_0.5.jpg)
