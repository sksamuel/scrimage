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
Writing out a heavily compressed Jpeg thumbnail
```scala
val in = ... // input stream
val out = ... // output stream
Image(in).fit(180,120).writer(Format.JPEG).withCompression(0.5).write(out)
```

Printing the sizes and ratio of the image
```scala
val in = ... // input stream
val out = ... // output stream
val image = Image(in)
println(s"Width: ${image.width} Height: ${image.height} Ratio: ${image.ratio}")
```


### Input / Output

Scrimage supports loading and saving of images in the common web formats (currently png, jpeg, gif, tiff). In addition it extends jav'sa image.io support by giving you an easy way to compress / optimize / interlace the images when saving.

To load an image simply use the Image apply methods on an input stream, file, filepath (String) or a byte array. The format does not matter as the underlying reader will determine that. Eg, 
```scala
val in = ... // a handle to an input stream
val image = Image(in)
```

To save a method, Scrimage provides an ImageWriter for each format it supports. An ImageWriter supports saving to a File, filepath (String), byte array, or OutputStream. The quickest way to use an ImageWriter is to call write() on an image, which will get a handle to an ImageWriter with the default configuration and use it for you. Eg,

```scala
val image = ... // some image
image.write(new File("/home/sam/spaghetti.png"))
```

If you want to override the configuration for a writer then you will need to get a handle to the writer itself using the writer() method which returns an ImageWriter instance. From here you can then configure it before writing. A common example would be optimising a PNG to use compression (uses a modified version of PngTastic behind the scenes). Eg,

```scala
val image = ... // some image
image.writer(Format.PNG).withCompression(9).write(new File("/home/sam/compressed_spahgetti.png"))
```
Note the writers are immutable and are created per image.


### Async

In version 1.1.0 support for asynchronous operations was added. This is achieved using the AsyncImage class. First, get an instance of AsyncImage from an Image or other source:

```scala
val in = ... // input stream
val a = AsyncImage(in)
```

Then any operations that act on that image return a Future[Image] instead of a standard Image. They will operate on the scala.concurrent implicit execution context.

```scala
... given an async image
val filtered = a.filter(VintageFilter) // filtered has type Future[Image]
```

A more complicated example would be to load all images instead a directory, apply a grayscale filter, and then re-save them out as optimized PNGs.

```scala
val dir = new File("/home/sam/images")
dir.listFiles().foreach(file => AsyncImage(file).filter(GrayscaleFilter).onSuccess {
case image => image.writer(Format.PNG).withMaxCompression.write(file)
})
```


### Filters

Scrimage comes with a wide array (or Iterable ;) of filters. Most of these filters I have not written myself,
but rather collected from other open source imaging libraries (for compliance with licenses and / or attribution - see file headers), and either re-written them in Scala, wrapped them in Scala, fixed bugs or improved them.

Some filters have options which can be set when creating the filters. All filters are immutable. Most filters have sensible default options as default parameters.

Click on the small images to see an enlarged example.

| Filter | Example 1 | Example 2 | Example 3 |
| ------ | --------- | --------- | --------- |
| blur | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_blur_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_blur_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_blur_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_blur_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_blur_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_blur_small.png'><a/> |
| brightness | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_brightness_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_brightness_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_brightness_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_brightness_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_brightness_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_brightness_small.png'><a/> |
| bump | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_bump_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_bump_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_bump_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_bump_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_bump_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_bump_small.png'><a/> |
| chrome | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_chrome_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_chrome_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_chrome_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_chrome_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_chrome_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_chrome_small.png'><a/> |
| color_halftone | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_color_halftone_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_color_halftone_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_color_halftone_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_color_halftone_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_color_halftone_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_color_halftone_small.png'><a/> |
| contour | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_contour_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_contour_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_contour_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_contour_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_contour_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_contour_small.png'><a/> |
| contrast | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_contrast_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_contrast_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_contrast_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_contrast_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_contrast_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_contrast_small.png'><a/> |
| despeckle | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_despeckle_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_despeckle_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_despeckle_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_despeckle_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_despeckle_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_despeckle_small.png'><a/> |
| diffuse | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_diffuse_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_diffuse_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_diffuse_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_diffuse_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_diffuse_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_diffuse_small.png'><a/> |
| dither | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_dither_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_dither_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_dither_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_dither_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_dither_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_dither_small.png'><a/> |
| edge | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_edge_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_edge_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_edge_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_edge_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_edge_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_edge_small.png'><a/> |
| emboss | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_emboss_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_emboss_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_emboss_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_emboss_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_emboss_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_emboss_small.png'><a/> |
| errordiffusion | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_errordiffusion_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_errordiffusion_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_errordiffusion_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_errordiffusion_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_errordiffusion_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_errordiffusion_small.png'><a/> |
| gamma | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_gamma_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_gamma_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_gamma_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_gamma_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_gamma_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_gamma_small.png'><a/> |
| gaussian | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_gaussian_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_gaussian_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_gaussian_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_gaussian_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_gaussian_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_gaussian_small.png'><a/> |
| glow | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_glow_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_glow_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_glow_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_glow_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_glow_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_glow_small.png'><a/> |
| grayscale | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_grayscale_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_grayscale_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_grayscale_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_grayscale_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_grayscale_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_grayscale_small.png'><a/> |
| invert | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_invert_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_invert_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_invert_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_invert_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_invert_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_invert_small.png'><a/> |
| lensblur | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_lensblur_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_lensblur_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_lensblur_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_lensblur_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_lensblur_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_lensblur_small.png'><a/> |
| lensflare | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_lensflare_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_lensflare_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_lensflare_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_lensflare_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_lensflare_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_lensflare_small.png'><a/> |
| minimum | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_minimum_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_minimum_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_minimum_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_minimum_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_minimum_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_minimum_small.png'><a/> |
| maximum | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_maximum_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_maximum_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_maximum_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_maximum_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_maximum_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_maximum_small.png'><a/> |
| motionblur | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_motionblur_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_motionblur_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_motionblur_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_motionblur_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_motionblur_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_motionblur_small.png'><a/> |
| noise | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_noise_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_noise_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_noise_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_noise_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_noise_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_noise_small.png'><a/> |
| offset | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_offset_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_offset_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_offset_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_offset_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_offset_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_offset_small.png'><a/> |
| oil | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_oil_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_oil_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_oil_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_oil_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_oil_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_oil_small.png'><a/> |
| pixelate | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_pixelate_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_pixelate_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_pixelate_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_pixelate_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_pixelate_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_pixelate_small.png'><a/> |
| pointillize_square | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_pointillize_square_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_pointillize_square_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_pointillize_square_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_pointillize_square_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_pointillize_square_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_pointillize_square_small.png'><a/> |
| posterize | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_posterize_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_posterize_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_posterize_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_posterize_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_posterize_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_posterize_small.png'><a/> |
| prewitt | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_prewitt_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_prewitt_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_prewitt_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_prewitt_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_prewitt_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_prewitt_small.png'><a/> |
| quantize | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_quantize_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_quantize_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_quantize_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_quantize_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_quantize_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_quantize_small.png'><a/> |
| rays | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_rays_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_rays_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_rays_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_rays_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_rays_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_rays_small.png'><a/> |
| ripple | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_ripple_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_ripple_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_ripple_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_ripple_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_ripple_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_ripple_small.png'><a/> |
| roberts | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_roberts_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_roberts_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_roberts_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_roberts_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_roberts_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_roberts_small.png'><a/> |
| rylanders | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_rylanders_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_rylanders_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_rylanders_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_rylanders_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_rylanders_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_rylanders_small.png'><a/> |
| sepia | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sepia_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sepia_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_sepia_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_sepia_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sepia_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sepia_small.png'><a/> |
| smear_circles | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_smear_circles_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_smear_circles_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_smear_circles_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_smear_circles_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_smear_circles_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_smear_circles_small.png'><a/> |
| sobels | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sobels_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sobels_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_sobels_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_sobels_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sobels_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sobels_small.png'><a/> |
| solarize | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_solarize_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_solarize_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_solarize_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_solarize_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_solarize_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_solarize_small.png'><a/> |
| sparkle | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sparkle_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sparkle_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_sparkle_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_sparkle_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sparkle_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sparkle_small.png'><a/> |
| swim | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_swim_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_swim_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_swim_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_swim_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_swim_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_swim_small.png'><a/> |
| television | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_television_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_television_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_television_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_television_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_television_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_television_small.png'><a/> |
| threshold | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_threshold_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_threshold_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_threshold_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_threshold_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_threshold_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_threshold_small.png'><a/> |
| twirl | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_twirl_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_twirl_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_twirl_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_twirl_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_twirl_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_twirl_small.png'><a/> |
| unsharp | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_unsharp_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_unsharp_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_unsharp_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_unsharp_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_unsharp_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_unsharp_small.png'><a/> |
| vignette | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_vignette_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_vignette_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_vignette_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_vignette_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_vignette_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_vignette_small.png'><a/> |
| vintage | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_vintage_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_vintage_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_vintage_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_vintage_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_vintage_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_vintage_small.png'><a/> |




### Benchmarks

Some noddy benchmarks comparing the speed of rescaling an image. I've compared the basic getScaledInstance method in java.awt.Image with ImgScalr and Scrimage. ImgScalr delegates to awt.Graphics2D for its rendering. Scrimage adapts the methods implemented by Morten Nobel.

The code is inside src/test/scala/com/sksamuel/scrimage/ScalingBenchmark.scala.

The results are for 100 runs of a resize to a fixed width / height.

| Library | Fast | High Quality (Method) |
| --------- | --------- | --------- |
| java.awt.Image.getScaledInstance | 11006ms | 17134ms (Area Averaging) |
| ImgScalr | 57ms | 5018ms (ImgScalr.Quality) |
| Scrimage | 113ms | 2730ms (Bicubic) |

As you can see, ImgScalr is the fastest for simple rescaling, but Scrimage is much faster than the rest a high quality scale.



### How to use

Scrimage is available on maven central.

Maven:
```xml
<dependency>
    <groupId>com.sksamuel.scrimage</groupId>
    <artifactId>scrimage</artifactId>
    <version>1.2.1</version>
</dependency>
```

SBT:
```scala
libraryDependencies += "com.sksamuel.scrimage" % "scrimage" % "1.2.1"
```
