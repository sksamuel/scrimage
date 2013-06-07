## Scrimage

Scrimage is a Scala library for image manipulating and filtering. The aim of the this library is to provide a
quick and easy way to do the kinds of image operations that most people need, on a daily basis, such as scaling, rotating,
converting between formats and applying filters. It is not intended to provide functionality that might be required by a
more "serious" image application - such as face recognition or movement detection.

A typical use case for this library would be creating thumbnails of images uploaded by users in a web app, or resizing a
set of images to have a consistent size, or applying an unblur filter to all images in a print application.

Scrimage has a consistent, idiomatic scala, and mostly immutable API for accessing images.
I say mostly immutable because for some operations creating a copy of the underlying image
would prove expensive (think a filter on a 8000 x 6000 image where you do not care about
keeping the original in memory). For these kinds of operations Scrimage supports a MutableImage
instance where operations that can be performed in place mutate the original.

[![Build Status](https://travis-ci.org/sksamuel/scrimage.png)](https://travis-ci.org/sksamuel/scrimage)

### License

This software is licensed under the Apache 2 License.

### API

| Operation        | Description | Example |
| ------------- |-------------|-------------|
| resize | Resizes the canvas to the given dimensions. Note this does not scale the image but simply changes the dimensions of the canvas on which the image is sitting. Specifying a larger size will pad the image with a background color and specifying a smaller size will crop the image. | ```image.resize(500,800)``` for an absolute resize or ```image.resize(0.5)``` for a percentage resize
| scale | Scales the image to given dimensions. This operation will change both the canvas dimensions and the image to match. This is what most people think of when they say they want to "resize" an image. | ```image.scale(x,y)``` for an absolute scale or ```image.scale(percentage)``` for a percentage scale
| pad | Resizes the canvas by adding a number of pixels around the image in a given color. | ```image.pad(20, Color.Black)``` would add 20 pixels of black around the entire image, thus increasing the canvas width and height both by 40 pixels (20 top, 20 bottom etc).
| fit | Resizes the canvas to the given dimensions and scales the original image so that it is the maximum possible size inside the canvas while maintaining aspect ratio.<br/><br/>This operation is useful if you want a group of images to all have the same canvas dimensions while maintaining the original aspect ratios. Think thumbnails on a site like amazon. | Given a 64x64 image then ```image.fit(128,96,Color.White)``` would result in a new image of 128x96 where the original image is now 96,96 as that is the largest it can be scaled to without overflowing the canvas bounds. The additional 'spare' width of 32 pixels would be set to the given background color, white in this case.
| cover | Resizes the canvas to the given dimensions and scales the original image so that it is the minimum size needed to cover the new dimensions without leaving any background visible.<br/><br/>This operation is useful if you want to generate an avatar/thumbnail style image from a larger image where having no background is more important than cropping part of the image. Think a facebook style profile thumbnail. | Given a 64x64 image then ```image.cover(128,96)``` would result in a new image of 128x96 where the original image is now 128,128 as that is the smallest it can be scaled to without leaving any visible background. 32 pixels of the height is lost as that is "off canvas" |
| copy | Creates a new clone of this image with a new pixel buffer. Any operations on the copy do not write back to the original.| ```image.copy``` |
| empty | Creates a new image but without initializing the data buffer to any specific values.| ```image.empty``` on an existing instance to use the same dimensions or ```Image.empty(x,y)``` to create a new image with the given dimensions |
| filled | Creates a new image and initializes the data buffer to the given color. | ```image.filled(Color.Red)``` |
| rotate left | Rotates the image anti clockwise. Results in width and height being flipped.| ```image.rotateLeft``` |
| rotate right | Rotates the image clockwise. Results in width and height being flipped. | ```image.rotateRight``` |
| flip x | Flips the image horizontally. Left becomes right. | ```image.flipX``` |
| flip y | Flips the image vertically. Top becomes bottom. | ```image.flipY``` |
| filter | Returns a new image with the given filter applied. See the filters section for examples of the filters available. Filters can be chained and are applied in sequence. | ```image.filter(BlurFilter)``` or ```image.filter(GaussianBlur(5)).filter(SepiaFilter)``` Most filters can be created from companion objects with sensible default values. |


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
but rather collected from other open source imaging libraries (for compliance with licenses and / or attribution - see file headers),
and either re-written them in Scala, wrapped them in Scala, or fixed bugs and modified them.

| Filter | Example 1 | Example 2 |
| ------------- |-------------|-------------|
| Block       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_block_4.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_block_4.png) |
| Blur       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_blur.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_blur.png) |
| Brightness | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_brightness.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_brightness.png) |
| Bump       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_bump.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_bump.png) |
| Chrome       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_chrome.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_chrome.png) |
| Contrast | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_contrast.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_contrast.png) |
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
| Pointillze | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_pointillize_square.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_pointillize_square.png) |
| Quantize | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_quantize_256.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_quantize_256.png) |
| Rays | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_rays.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_rays.png) |
| Ripple | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_ripple.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_ripple.png) |
| Rylanders       | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_rylanders.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_rylanders.png) |
| Sepia | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sepia.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sepia.png) |
| Smear | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_smear_circles.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_smear_circles.png) |
| Solarize | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_solarize.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_solarize.png) |
| Sparkle | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sparkle.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sparkle.png) |
| Television | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_television.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_television.png) |
| Threshold | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_threshold_red_127.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_threshold_red_127.png) |
| Unsharpen | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_unsharp.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_unsharp.png) |


### How to use

Scrimage is available on maven central. This means you want to use one of the following:

Maven:
```xml
<dependency>
    <groupId>com.sksamuel.scrimage</groupId>
    <artifactId>scrimage</artifactId>
    <version>0.9.0</version>
</dependency>
```

SBT:
```scala
libraryDependencies += "com.sksamuel.scrimage" % "scrimage" % "0.9.0"
```
