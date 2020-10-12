Fit
=====

The fit operation creates an image with the specified dimensions, with the original image scaled to fit
exactly inside, as large as possible, without losing any of the original image.

If the source and target dimensions do not have the same aspect ratio then the original
image will not cover the entire output image, so the excess background is set to a default color.

The difference between this and [max](max.md), is that fit will pad out the canvas to the specified dimensions,
whereas max will not. In other words, max and fit will result an equal amount of scaling, but fit will pad the canvas
with a background color so that it precisely matches the input dimensions.

The difference between this and [cover](cover.md) is that cover will also return an image with the specified dimensions, but
will size the image to ensure there is no background padding required, but potentially losing some of the source image.

To use, invoke with the target dimenions and optionally, a background color (defaults to Color.WHITE),
a scale method (defaults to ScaleMethod.Bicubic), and the position of the source image in the
target (defaults to Position.Center).

That last parameter has no effect if the aspect ratios are the same (because in that case the image would fit exactly inside anyway).


### Examples

Using this image scaled to 640 x 360 as our input:

![source image](images/input_640_360.jpg)

| Code | Output |
| ---- | ------ |
| `image.fit(400, 300, Color.DARK_GRAY)` | ![image](images/fit_400_300.jpg) |
| `image.fit(300, 300, Color.BLUE)`      | ![image](images/fit_300_300.jpg) |
| `image.fit(400, 100, Color.RED)`       | ![image](images/fit_400_100.jpg) |
