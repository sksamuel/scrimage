![logo](logo.png)
=======

![build_test](https://github.com/sksamuel/scrimage/workflows/build_test/badge.svg)
[<img src="https://img.shields.io/maven-central/v/com.sksamuel.scrimage/scrimage-core.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22scrimage-core)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/com.sksamuel.scrimage/scrimage-core.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/com/sksamuel/scrimage/)

Scrimage is an immutable, functional, and performant JVM library for manipulation of images.

The aim of this library is to provide a simple and concise way to do common image operations, such as resizing to fit
 a required width and height, converting between formats, applying filters and so on.
 It is not intended to provide functionality that might be required by a more "serious" image processing application - such as face recognition or movement tracking.

A typical use case for this library would be creating thumbnails of images uploaded by users in a web app, or bounding a
set of product images so that they all have the same dimensions, or optimizing PNG uploads by users to apply maximum compression,
or applying a grayscale filter in a print application.

Scrimage mostly builds on the functionality provided by java.awt.* along with selected other third party libraries,
such as [drewnoakes/metadata-extractor](https://github.com/drewnoakes/metadata-extractor) and
[haraldk/TwelveMonkeys](https://github.com/haraldk/TwelveMonkeys).



### Creation methods

Creation methods create a new image by specifying the dimensions. They can start with a specified colour using [fill](fill.md) or undefined using [blank](blank.md).
An image can be created by [copying](copy.md) an existing image.

### Input / Output

The heart of any image library is [reading and writing](io.md) in the various formats.
Scrimage supports all the formats provided by javax.imageio along with extra formats in the
modules `scrimage-formats-extra` and `scrimage-webp`.

### Image Operations

These operations operate on an image, returning a copy of that image.
For instance, [fit](fit.md) will resize an image to fit into a specified set of bounds, and [scale](scale.md) will change the size of an image.

For the full list of operations, see the menu on the left.


### Filters

Scrimage comes with a wide array of [filters](filters.md) in the module `scrimage-filters`.


### Composites



### Metadata

Scrimage builds on the [metadata-extractor](https://github.com/drewnoakes/metadata-extractor) project to provide
the ability to read metadata from an image.

Read more details [here](metadata.md).


### Quick Examples

Reading an image, scaling it to 50% using the Bicubic method, and writing out as PNG
```kotlin
val in = ... // input stream
val out = ... // output stream
ImmutableImage.loader().fromStream(in).scale(0.5, Bicubic).output(out) // an implicit PNG writer is in scope by default
```

Reading an image from a java File, applying a blur filter, then flipping it on the horizontal axis, then writing out as a Jpeg
```kotlin
val inFile = ... // input File
val outFile = ... // output File
ImmutableImage.loader().fromFile(inFile).filter(BlurFilter).flipX.output(outFile)(JpegWriter()) // specified Jpeg
```

Padding an image with a 20 pixel border around the edges in red
```kotlin
val in = ... // input stream
val out = ... // output stream
ImmutableImage.loader().fromStream(in).pad(20, Color.Red)
```

Enlarging the canvas of an image without scaling the image. Note: the resize methods change the canvas size,
and the scale methods are used to scale/resize the actual image. This terminology is consistent with Photoshop.
```kotlin
val in = ... // input stream
val out = ... // output stream
ImmutableImage.loader().fromStream(in).resize(600,400)
```

Scaling an image to a specific size using a fast non-smoothed scale
```kotlin
val in = ... // input stream
val out = ... // output stream
ImmutableImage.loader().fromStream(in).scaleTo(300, 200, FastScale)
```

Writing out a heavily compressed Jpeg thumbnail
```kotlin
implicit val writer = JpegWriter().withCompression(50)
val in = ... // input stream
val out = ... // output stream
ImmutableImage.loader().fromStream(in).fit(180,120).output(new File("image.jpeg"))
```

Printing the sizes and ratio of the image
```kotlin
val in = ... // input stream
val out = ... // output stream
val image = ImmutableImage.loader().fromStream(in)
println("Width: ${image.width} Height: ${image.height} Ratio: ${image.ratio}")
```

Converting a byte array in JPEG to a byte array in PNG
```kotlin
val in : Array[Byte] = ... // array of bytes in JPEG say
val out = Image(in).write // default is PNG
val out2 = ImmutableImage.loader().fromBytes(in).bytes) // an implicit PNG writer is in scope by default with max compression
```

Coverting an input stream to a PNG with no compression
```kotlin
implicit val writer = PngWriter.NoCompression
val in : InputStream = ... // some input stream
val out = ImmutableImage.loader().fromStream(in).stream
```
