## Scrimage

Scrimage is a Scala library for image manipulating, processing and filtering. The aim of the this library is to provide a
quick and easy way to do the kinds of image operations that most people need, on a daily basis, such as scaling, rotating,
converting between formats and applying filters. It is not intended to provide functionality that might be required by a
more "serious" image application - such as a lightroom clone.

Scrimage has a consistent, idiomatic scala, and mostly immutable API for accessing images.
I say mostly immutable because for some operations creating a copy of the underlying image
would prove expensive (think a blur filter on a 3000 x 3000 image where you do not care about
keeping the original in memory). For these kinds of operations Scrimage supports a copy
operation that can be used to mimic immutability by first creating a copy and then performing
the operation on that.

[![Build Status](https://travis-ci.org/sksamuel/scrimage.png)](https://travis-ci.org/sksamuel/scrimage)

### License

This software is licensed under the Apache 2 License.

### Quick Examples

Reading an image, scaling it to 50% using the Bicubic method, and writing out as PNG
```scala
val in = ... // input stream
val out = ... // output stream
Image(in).scale(0.5, Bicubic).write(out)
```

Reading an image from a java File, applying a blur filter, then flipping it on the horizontal axis, then writing out as a Jpeg
```scala
val inFile = ... // input File
val outFile = ... // output File
Image(inFile).filter(BlurFilter).flipX.write(outFile)
```

Padding an image with a 20 pixel border around the edges in red
```scala
val in = ... // input stream
val out = ... // output stream
Image(in).pad(20, Color.Red)
```

Enlarging the canvas of an image without scaling the image (use resize method)
```scala
val in = ... // input stream
val out = ... // output stream
Image(in).resize(600,400)
```

Scaling an image to a specific size using a fast non-smoothed scale
```scala
val in = ... // input stream
val out = ... // output stream
Image(in).scale(300, 200, FastScale)
```

Printing the sizes and ratio of the image
```scala
val in = ... // input stream
val out = ... // output stream
val image = Image(in)
println(s"Width: ${image.width} Height: ${image.height} Ratio: ${image.ratio}")
```

### Filters

Scrimage comes with a wide array (or List ;)) of filters. Most of these filters I have not written myself,
but rather collected from other open source imaging libraries (where the license allows - see file headers for attribution),
and re-written them in Scala, wrapped them in a Scala wrapper, or fixed bugs and modified them.

| Filter Name        | Example 1 | Example 2|
| ------------- |-------------|-------------|
| Block       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_block_4.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_block_4.png) |
| Blur       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_blur.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_blur.png) |
| Bump       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_bump.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_bump.png) |
| Chrome       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_chrome.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_chrome.png) |
| Despeckle       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_despeckle.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_despeckle.png) |
| Diffuse | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_diffuse_4.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_diffuse_4.png) |
| Edge | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_edge.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_edge.png) |
| Gamma | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_gamma_2.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_gamma_2.png) |
| Gaussian Blur      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_gaussian.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_gaussian.png) |
| Invert      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_invert.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_invert.png) |
| Lens Blur      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_lensblur.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_lensblur.png) |
| Pointillze      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_pointillize_square.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_pointillize_square.png) |
| Quantize      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_quantize_256.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_quantize_256.png) |
| Rays      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_rays.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_rays.png) |
| Ripple      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_ripple.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_ripple.png) |
| Smear       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_smear_circles.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_smear_circles.png) |
| Solarize       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_solarize.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_solarize.png) |
| Sparkle       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sparkle.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sparkle.png) |
| Threshold       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_threshold_red_127.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_threshold_red_127.png) |
| Unsharpen  | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_unsharp.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_unsharp.png) |


### How to use

Scrimage is available on maven central. This means you want to use one of the following:

Maven:

SBT:

Gradle:

Ivy: