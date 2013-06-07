## Scrimage

Scrimage is a Scala library for image manipulating, processing and filtering. The aim of the this library is to provide a
quick and easy way to do the kinds of image operations that most people need, on a daily basis, such as scaling, rotating,
converting between formats and applying filters. It is not intended to provide functionality that might be required by a
more "serious" image application - such as a lightroom clone.

Scrimage has a consistent, idiomatic scala, and mostly immutable API for accessing images.
I say mostly immutable because for some operations creating a copy of the underlying image
would prove expensive (think a filter on a 8000 x 6000 image where you do not care about
keeping the original in memory). For these kinds of operations Scrimage supports a MutableImage
instance for which operations that are able to be performed in place act on the original data.

[![Build Status](https://travis-ci.org/sksamuel/scrimage.png)](https://travis-ci.org/sksamuel/scrimage)

### License

This software is licensed under the Apache 2 License.

### API

| Operation        | Description | Example |
| ------------- |-------------|-------------|
| resize | Resizes the canvas to the given dimensions. Note this does not scale the image but simply changes the dimensions of the canvas on which the image is sitting. Specifying a larger size will pad the image with a background color and specifying a smaller size will crop the image. | ```image.resize(500,800)``` for an absolute resize or ```image.resize(0.5)``` for a percentage resize
| scale | Scales the image to given dimensions. This operation will change both the canvas dimensions and the image to match. This is what most people think of when they say they want to "resize" an image. | ```image.scale(x,y)``` for an absolute scale or ```image.scale(percentage)``` for a percentage scale
| pad | Resizes the canvas by adding a number of pixels around the image in a given color. | ```image.pad(20, Color.Black)``` would add 20 pixels of black around the entire image, thus increasing the canvas width and height both by 40 pixels (20 top, 20 bottom etc).
| fit | tbc | |
| cover | tbc | |
| copy | Creates a new clone of this image with a new pixel buffer. Any operations on the copy do not write back to the original. | ```image.copy``` |
| empty | Creates a new image but without initializing the data buffer to any specific values. | ```image.empty``` on an existing instance to use the same dimensions or ```Image.empty(x,y)``` to create a new image with the given dimensions |
| filled | Creates a new image and initializes the data buffer to the given color. | ```image.filled(Color.Red)``` |
| rotate left | Pretty obvious | |
| rotate right | Pretty obvious | |
| flip x | Pretty obvious | |
| flip y | Pretty obvious | |
| filter | Applies a filter. | ```image.filter(BlurFilter)``` or ```image.filter(GaussianBlur(5)).filter(LensFlareFilter)``` |


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

Scrimage comes with a wide array (or Seq ;) of filters. Most of these filters I have not written myself,
but rather collected from other open source imaging libraries (where the license allows - see file headers for attribution),
and either re-written them in Scala, wrapped them in Scala, or fixed bugs and modified them.

| Filter Name        | Example 1 | Example 2|
| ------------- |-------------|-------------|
| Block       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_block_4.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_block_4.png) |
| Blur       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_blur.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_blur.png) |
| Bump       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_bump.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_bump.png) |
| Chrome       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_chrome.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_chrome.png) |
| Despeckle       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_despeckle.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_despeckle.png) |
| Dither       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_dither.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_dither.png) |
| Diffuse | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_diffuse_4.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_diffuse_4.png) |
| Edge | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_edge.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_edge.png) |
| Emboss | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_emboss.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_emboss.png) |
| Error Diffusion | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_errordiffusion.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_errordiffusion.png) |
| Gamma | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_gamma_2.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_gamma_2.png) |
| Gaussian Blur      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_gaussian.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_gaussian.png) |
| Invert      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_invert.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_invert.png) |
| Lens Blur      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_lensblur.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_lensblur.png) |
| Lens Flare      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_lensflare.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_lensflare.png) |
| Pointillze      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_pointillize_square.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_pointillize_square.png) |
| Quantize      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_quantize_256.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_quantize_256.png) |
| Rays      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_rays.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_rays.png) |
| Ripple      | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_ripple.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_ripple.png) |
| Rylanders       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_rylanders.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_rylanders.png) |
| Sepia       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sepia.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sepia.png) |
| Smear       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_smear_circles.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_smear_circles.png) |
| Solarize       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_solarize.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_solarize.png) |
| Sparkle       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sparkle.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sparkle.png) |
| Television       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_television.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_television.png) |
| Threshold       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_threshold_red_127.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_threshold_red_127.png) |
| Unsharpen  | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_unsharp.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_unsharp.png) |


### How to use

Scrimage is available on maven central. This means you want to use one of the following:

Maven:
```xml
<dependency>
    <groupId>com.sksamuel.scrimage</groupId>
    <artifactId>scrimage</artifactId>
    <version>0.7.0</version>
</dependency>
```

SBT:
```scala
libraryDependencies += "com.sksamuel.scrimage" % "scrimage" % "0.7.0
```
