Fit
=====

The fit operation creates an image with the specified dimensions, with the original image scaled to fit
exactly inside, as large as possible, without losing any of the original image.

If the source and target dimensions do not have the same aspect ratio then the original
image will not cover the entire output image, so the excess background is set to a default color.

The difference between this and [max](max.md), is that fit will pad out the canvas to the specified dimensions,
whereas max will not. In other words, max and fit will result an equal amount of scaling, but fit will pad the canvas
with a background color so that it precisely matches the input dimensions.

Compare this operation with [cover](cover.md).

To use, invoke with the target dimenions and optionally, a background color (defaults to Color.WHITE),
a scale method (defaults to ScaleMethod.Bicubic), and the position of the source image in the
target (defaults to Position.Center).

That last parameter has no effect if the aspect ratios are the same (because in that case the image would fit exactly inside anyway).

Eg, to fit an image into 600 x 400 using defaults:
```scala
image.fit(600,400)
```
or to fit into 240 x 160 with black background.
```scala
image.cover(240,160, Color.BLACK)
```

In this example, the original bird image was invoked with `bird.fit(300,150, Color.RED)`.
The output image is 300 x 150 as specified, but because the original image had a different aspect ratio,
there is no way to exactly scale the image to cover the entire canvas.

So the original image is scaled to fit inside without any loss.
Then the excess background is set to the given background color (in this case red).
The red background is an ugly choice, but it serves to demonstrate how the fit operation works.

| Before | After |
|--------|-------|
|<img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/bird_small.png"/>| <img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/bird_fitted.png"/>|
