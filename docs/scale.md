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

Using this image scaled to 640 x 360 as our input:

<img src="/images/input.jpg" width=400"/>

| Code | Output |
| ---- | ------ |
| `image.scaleToWidth(400)`                    | <img src="/images/scale_w400.jpg"/> |
| `image.scaleToHeight(200)`                   | <img src="/images/scale_h200.jpg"/> |
| `image.scaleTo(400, 400)`       | <img src="/images/scale_400_400.jpg"/> |
| `image.scaleTo(400, 400, ScaleMethod.FastScale)`  | <img src="/images/scale_400_400_fast.jpg"/> |
| `image.scale(0.5)`                                | <img src="/images/scale_0.5.jpg"/> |

