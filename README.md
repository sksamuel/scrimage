## Scrimage

Scrimage is a Scala image library for manipulating and processing of images. The aim of the this library is to provide a
quick and easy way to do the kinds of image operations that most people need, such as scaling, rotating,
converting between formats and applying filters. It is not intended to provide functionality that might be required by a
more "serious" image application - such as face recognition or movement tracking.

A typical use case for this library would be creating thumbnails of images uploaded by users in a web app, or resizing a
set of images to have a consistent size, or applying a grayscale filter in a print application.

Scrimage has a consistent, idiomatic scala, and mostly immutable API that builds on the java.awt.Image methods.
I say mostly immutable because for some operations creating a copy of the underlying image
would prove expensive (think an in place filter on a 8000 x 6000 image where you do not care about
keeping the original in memory). For these kinds of operations Scrimage supports a MutableImage
instance where operations that can be performed in place mutate the original instead of returning a copy.

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
Image(in).scale(0.5, Bicubic).write(out) // Png is default
```

Reading an image from a java File, applying a blur filter, then flipping it on the horizontal axis, then writing out as a Jpeg
```scala
val inFile = ... // input File
val outFile = ... // output File
Image(inFile).filter(BlurFilter).flipX.write(outFile, Format.Jpeg) // specified Jpeg
```

Padding an image with a 20 pixel border around the edges in red
```scala
val in = ... // input stream
val out = ... // output stream
Image(in).pad(20, Color.Red)
```

Enlarging the canvas of an image without scaling the image (resize method changes canvas size, scale method scales image)
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

Writing out an optimized PNG
```scala
val in = ... // input stream
val out = ... // output stream
Image(in).writer(Format.PNG).withCompression(9).write(out)
```

Writing out a heavily compressed Jpeg thumbnail
```scala
val in = ... // input stream
val out = ... // output stream
Image(in).scale(300,200).writer(Format.JPEG).withCompression(0.5).write(out)
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

Some filters have options which can be set when creating the filters. All filters are immutable. Most filters have sensible default options as default parameters.

| Filter | Example 1 | Example 2 |
| ------------- |-------------|-------------|
| Block | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_block_4_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_block_4_small.png) |
| Blur | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_blur_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_blur_small.png) |
| Brightness | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_brightness_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_brightness_small.png) |
| Bump | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_bump_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_bump_small.png) |
| Chrome | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_chrome_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_chrome_small.png) |
| Color Halftone | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_color_halftone_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_color_halftone_small.png) |
| Contour | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_contour_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_contour_small.png) |
| Contrast | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_contrast_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_contrast_small.png) |
| Despeckle | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_despeckle_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_despeckle_small.png) |
| Dither | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_dither_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_dither_small.png) |
| Diffuse | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_diffuse_4_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_diffuse_4_small.png) |
| Edge | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_edge_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_edge_small.png) |
| Emboss | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_emboss_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_emboss_small.png) |
| Error Diffusion | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_errordiffusion_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_errordiffusion_small.png) |
| Gamma | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_gamma_2_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_gamma_2_small.png) |
| Gaussian Blur | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_gaussian_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_gaussian_small.png) |
| Glow | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_glow_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_glow_small.png) |
| Grayscale | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_grayscale_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_grayscale_small.png) |
| Invert | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_invert_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_invert_small.png) |
| Lens Blur | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_lensblur_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_lensblur_small.png) |
| Lens Flare | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_lensflare_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_lensflare_small.png) |
| Minimum | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_minimum.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_minimum_small.png) |
| Maximum | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_maximum_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_maximum_small.png) |
| Offset | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_offset_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_offset_small.png) |
| Oil | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_oil_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_oil_small.png) |
| Pointillze | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_pointillize_square_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_pointillize_square_small.png) |
| Quantize | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_quantize_256_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_quantize_256_small.png) |
| Rays | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_rays_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_rays_small.png) |
| Ripple | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_ripple_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_ripple_small.png) |
| Rylanders | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_rylanders_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_rylanders_small.png) |
| Sepia | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sepia_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sepia_small.png) |
| Smear | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_smear_circles_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_smear_circles_small.png) |
| Sobels | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sobels_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sobels_small.png) |
| Solarize | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_solarize_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_solarize_small.png) |
| Sparkle | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sparkle_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sparkle_small.png) |
| Swim | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_swim_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_swim_small.png) |
| Television | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_television_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_television_small.png) |
| Threshold | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_threshold_127_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_threshold_127_small.png) |
| Twirl | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_twirl_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_twirl_small.png) |
| Unsharpen | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_unsharp_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_unsharp_small.png) |
| Vintage | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_vintage_small.png) | ![](https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_vintage_small.png) |


### How to use

Scrimage is available on maven central.

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
