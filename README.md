## Scrimage

[![Join the chat at https://gitter.im/sksamuel/scrimage](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sksamuel/scrimage?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Scrimage is a Scala image library for manipulating and processing of images. The aim of the this library is to provide a
quick and easy way to do the kinds of image operations that most people need, such as scaling, rotating,
converting between formats and applying filters. It is not intended to provide functionality that might be required by a
more "serious" image application - such as face recognition or movement tracking.

A typical use case for this library would be creating thumbnails of images uploaded by users in a web app, or resizing a
set of images to have a consistent size, or optimizing PNG uploads by users to apply maximum compression, or applying a grayscale filter in a print application.

Scrimage has a consistent, idiomatic scala, and mostly immutable API that builds on the java.awt.Image methods.
I say mostly immutable because for some operations creating a copy of the underlying image
would prove expensive (think an in place filter on a 8000 x 6000 image where you do not care about
keeping the original in memory). For these kinds of operations Scrimage supports a MutableImage
instance where operations that can be performed in place mutate the original instead of returning a copy.

[![Build Status](https://travis-ci.org/sksamuel/scrimage.png)](https://travis-ci.org/sksamuel/scrimage) 

### Image Operations

These operations all operate on an existing image, returning a copy of that image. The more complicated operations have a link to more detailed documentation.

| Operation        | Description |
| ------------- |-------------|
| [autocrop](https://github.com/sksamuel/scrimage/blob/master/guide/autocrop.md) | Removes any "excess" background, returning just the image proper |
| bound | Ensures that the image is no larger than specified dimensions. If the original is bigger, it will be scaled down, otherwise the original is returned. <br/><br/>This is useful when you want to ensure images do need exceed a certain size but you don't want to scale up if smaller. |
| copy | Creates a new clone of this image with a new pixel buffer. Any operations on the copy do not write back to the original.|
| [cover](https://github.com/sksamuel/scrimage/blob/master/guide/cover.md) | Resizes the canvas to the given dimensions and scales the original image so that it is the minimum size needed to cover the new dimensions without leaving any background visible.<br/><br/>This operation is useful if you want to generate an avatar/thumbnail style image from a larger image where having no background is more important than cropping part of the image. Think a facebook style profile thumbnail. |
| empty | Creates a new image but without initializing the data buffer to any specific values.|
| filled | Creates a new image and initializes the data buffer to the given color. |
| [fit](https://github.com/sksamuel/scrimage/blob/master/guide/fit.md) | Resizes the canvas to the given dimensions and scales the original image so that it is the maximum possible size inside the canvas while maintaining aspect ratio.<br/><br/>This operation is useful if you want a group of images to all have the same canvas dimensions while maintaining the original aspect ratios. Think thumbnails on a site like amazon where they are padded with white background. |
| flip | Flips the image either horizontally or vertically. |
| filter | Returns a new image with the given filter applied. See the filters section for examples of the filters available. Filters can be chained and are applied in sequence. |
| pad | Resizes the canvas by adding a number of pixels around the image in a given color. |
| resize | Resizes the canvas to the given dimensions. This does not scale the image but simply changes the dimensions of the canvas on which the image is sitting. Specifying a larger size will pad the image with a background color and specifying a smaller size will crop the image. This is the operation most people want when they think of crop. |
| rotate | Rotates the image clockwise or anti-clockwise. |
| scale | Scales the image to given dimensions. This operation will change both the canvas and the image. This is what most people think of when they want a "resize" operation. |
| [trim](https://github.com/sksamuel/scrimage/blob/master/guide/trim.md) | Removes a specified amount of pixels from each edge, essentially sugar to a crop operation. |

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
Image(in).fit(180,120).writer(Format.JPEG).withCompression(50).write(out)
```

Printing the sizes and ratio of the image
```scala
val in = ... // input stream
val out = ... // output stream
val image = Image(in)
println(s"Width: ${image.width} Height: ${image.height} Ratio: ${image.ratio}")
```

Converting a byte array in JPEG to a byte array in PNG
```
val in : Array[Byte] = ... // array of bytes in JPEG say
val out = Image(in).write // default is PNG
val out2 = Image(in).write(Format.PNG) // to be explicit about the output format
```

Coverting an input stream to a maximum compressed PNG
```
val in : InputStream = ... // some input stream
val out : OutputStream = ... // some output stream
val compressed = Image(in).writer(Format.PNG).withMaxCompression.write(out)
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

### Benchmarks

Some noddy benchmarks comparing the speed of rescaling an image. I've compared the basic getScaledInstance method in java.awt.Image with ImgScalr and Scrimage. ImgScalr delegates to awt.Graphics2D for its rendering. Scrimage adapts the methods implemented by Morten Nobel.

The code is inside src/test/scala/com/sksamuel/scrimage/ScalingBenchmark.scala.

The results are for 100 runs of a resize to a fixed width / height.

| Library | Fast | High Quality (Method) |
| --------- | --------- | --------- |
| java.awt.Image.getScaledInstance | 11006ms | 17134ms (Area Averaging) |
| ImgScalr | 57ms | 5018ms (ImgScalr.Quality) |
| Scrimage | 113ms | 2730ms (Bicubic) |

As you can see, ImgScalr is the fastest for a simple rescale, but Scrimage is much faster than the rest for a high quality scale.


### Including Scrimage in your project

Scrimage is available on maven central. There are several dependencies. 
One is the core library which is required. The others are filters and canvas.
They are split because the image filters is a large jar, 
and most people just want the basic resize/scale/load/save functionality. 

Include the canvas dependency if you want to be able to do AWT style graphics manipulating (drawRect, etc).
Include the filters dependency if you need the image filters.

Scrimage is cross compiled for scala 2.11 and 2.10.

If using SBT then you want:
```scala
libraryDependencies += "com.sksamuel.scrimage" %% "scrimage-core" % "1.4.2"

libraryDependencies += "com.sksamuel.scrimage" %% "scrimage-canvas" % "1.4.2"

libraryDependencies += "com.sksamuel.scrimage" %% "scrimage-filters" % "1.4.2"
```

Maven:
```xml
<dependency>
    <groupId>com.sksamuel.scrimage</groupId>
    <artifactId>scrimage-core_2.11</artifactId>
    <version>1.4.2</version>
</dependency>
<dependency>
    <groupId>com.sksamuel.scrimage</groupId>
    <artifactId>scrimage-canvas_2.11</artifactId>
    <version>1.4.2</version>
</dependency>
<dependency>
    <groupId>com.sksamuel.scrimage</groupId>
    <artifactId>scrimage-filters_2.11</artifactId>
    <version>1.4.2</version>
</dependency>
```

If you're using maven you'll have to adjust the artifact id with the correct scala version. SBT will do this automatically when you use %% like in the example above.


### Filters

Scrimage comes with a wide array (or Iterable ;) of filters. Most of these filters I have not written myself,
but rather collected from other open source imaging libraries (for compliance with licenses and / or attribution - see file headers), and either re-written them in Scala, wrapped them in Scala, fixed bugs or improved them.

Some filters have options which can be set when creating the filters. All filters are immutable. Most filters have sensible default options as default parameters.

Click on the small images to see an enlarged example.

| Filter | Example 1 | Example 2 | Example 3 |
| ------ | --------- | --------- | --------- |
| blur | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_blur_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_blur_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_blur_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_blur_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_blur_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_blur_small.png'><a/> |
| border | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_border_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_border_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_border_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_border_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_border_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_border_small.png'><a/> |
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
| hsb | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_hsb_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_hsb_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_hsb_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_hsb_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_hsb_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_hsb_small.png'><a/> |
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
| snow | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_snow_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_snow_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_snow_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_snow_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_snow_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_snow_small.png'><a/> |
| sobels | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sobels_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sobels_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_sobels_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_sobels_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sobels_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sobels_small.png'><a/> |
| solarize | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_solarize_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_solarize_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_solarize_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_solarize_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_solarize_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_solarize_small.png'><a/> |
| sparkle | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sparkle_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_sparkle_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_sparkle_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_sparkle_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sparkle_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_sparkle_small.png'><a/> |
| summer | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_summer_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_summer_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_summer_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_summer_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_summer_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_summer_small.png'><a/> |
| swim | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_swim_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_swim_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_swim_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_swim_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_swim_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_swim_small.png'><a/> |
| television | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_television_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_television_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_television_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_television_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_television_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_television_small.png'><a/> |
| threshold | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_threshold_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_threshold_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_threshold_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_threshold_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_threshold_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_threshold_small.png'><a/> |
| tritone | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_tritone_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_tritone_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_tritone_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_tritone_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_tritone_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_tritone_small.png'><a/> |
| twirl | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_twirl_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_twirl_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_twirl_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_twirl_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_twirl_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_twirl_small.png'><a/> |
| unsharp | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_unsharp_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_unsharp_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_unsharp_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_unsharp_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_unsharp_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_unsharp_small.png'><a/> |
| vignette | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_vignette_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_vignette_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_vignette_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_vignette_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_vignette_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_vignette_small.png'><a/> |
| vintage | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_vintage_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/bird_vintage_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_vintage_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/colosseum_vintage_small.png'><a/> | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_vintage_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/filters/lanzarote_vintage_small.png'><a/> |

### Composites

Scrimage comes with the usual composites built in. This grid shows the effect of compositing palm trees over a US
mailbox. The first column is the composite with a value of 0.5f, and the second column with 1f. Note, if you reverse
the order of the images then the effects would be reversed.

The code required to perform a composite is simply.

`val composed = image1.composite(new XYZComposite(alpha), image2)`

Click on an example to see it full screen.

| Composite | Alpha 0.5f | Alpha 1f |
| ------ | --------- | --------- |
| average | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/average_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/average_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/average_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/average_1.0_small.png'><a/> |
| blue | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/blue_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/blue_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/blue_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/blue_1.0_small.png'><a/> |
| color | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/color_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/color_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/color_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/color_1.0_small.png'><a/> |
| colorburn | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colorburn_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colorburn_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colorburn_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colorburn_1.0_small.png'><a/> |
| colordodge | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colordodge_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colordodge_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colordodge_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/colordodge_1.0_small.png'><a/> |
| diff | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/diff_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/diff_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/diff_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/diff_1.0_small.png'><a/> |
| green | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/green_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/green_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/green_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/green_1.0_small.png'><a/> |
| grow | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/grow_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/grow_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/grow_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/grow_1.0_small.png'><a/> |
| hue | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hue_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hue_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hue_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hue_1.0_small.png'><a/> |
| hard | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hard_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hard_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hard_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/hard_1.0_small.png'><a/> |
| heat | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/heat_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/heat_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/heat_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/heat_1.0_small.png'><a/> |
| lighten | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/lighten_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/lighten_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/lighten_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/lighten_1.0_small.png'><a/> |
| negation | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_1.0_small.png'><a/> |
| luminosity | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/luminosity_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/luminosity_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/luminosity_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/luminosity_1.0_small.png'><a/> |
| multiply | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/multiply_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/multiply_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/multiply_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/multiply_1.0_small.png'><a/> |
| negation | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/negation_1.0_small.png'><a/> |
| normal | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/normal_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/normal_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/normal_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/normal_1.0_small.png'><a/> |
| overlay | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/overlay_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/overlay_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/overlay_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/overlay_1.0_small.png'><a/> |
| red | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/red_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/red_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/red_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/red_1.0_small.png'><a/> |
| reflect | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/reflect_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/reflect_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/reflect_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/reflect_1.0_small.png'><a/> |
| saturation | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/saturation_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/saturation_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/saturation_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/saturation_1.0_small.png'><a/> |
| screen | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/screen_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/screen_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/screen_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/screen_1.0_small.png'><a/> |
| subtract | <a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/subtract_0.5_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/subtract_0.5_small.png'><a/> |<a href='https://raw.github.com/sksamuel/scrimage/master/examples/composite/subtract_1.0_large.jpeg'><img src='https://raw.github.com/sksamuel/scrimage/master/examples/composite/subtract_1.0_small.png'><a/> |

## License
```
This software is licensed under the Apache 2 license, quoted below.

Copyright 2013 Stephen Samuel

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
```
