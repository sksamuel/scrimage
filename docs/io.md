Input / Output
=============

Scrimage supports loading and saving of images in the common web formats (png, jpeg, gif, tiff, webp).
In addition, it extends javas image.io support by giving you an easy way to set options on the output format when saving.


!!! warning
    The TIFF support via javax.imageio has issues with some tiff images.
    If this happens, try using the `scrimage-formats-extra` module which provides extra TIFF support via the [TwelveMonkeys library](https://github.com/haraldk/TwelveMonkeys).


To load an image simply use the Image companion methods on an input stream, file, filepath (String) or a byte array.
The format does not matter as the underlying reader will determine that. Eg,
```scala
val in = ... // a handle to an input stream
val image = ImmutableImage.loader().fromStream(in)
```

To save a method, Scrimage requires an ImageWriter. You can use this implicitly or explicitly. A PngWriter is in scope
by default.

```scala
val image = ... // some image
image.output(new File("/home/sam/spaghetti.png")) // use implicit writer
image.output(new File("/home/sam/spaghetti.png"))(writer) // use explicit writer
```

To set your own implicit writer, just define it in scope and it will override the default:

```scala
implicit val writer = PngWriter.NoCompression
val image = ... // some image
image.output(new File("/home/sam/spaghetti.png")) // use custom implicit writer instead of default
```

If you want to override the configuration for a writer then you can do this when you create the writer. Eg:

```scala
implicit val writer = JpegWriter().withCompression(50).withProgressive(true)
val image = ... // some image
image.output(new File("/home/sam/compressed_spahgetti.png"))
```



## Extra formats

The `scrimage-formats-extra` module brings in additional formats such as PCX, BMP, TGA and more. It also includes
a better TIFF reader than the one available in the standard java library.

To read from these formats, just add the module to your classpath, nothing more.

To write using these formats, pass an instance of the applicable `ImageWriter` interface when saving out the format.

For example,

```java
image.output(new BmpWriter(), new File("output.bmp"))
```




## Webp Input Support

Webp loading is available is an additional module `scrimage-webp` and requires that the dwebp decompression
binary is located on the classpath at `/webp_binaries/dwebp`.

The easiest way to achieve this is to [download the binary](https://developers.google.com/speed/webp) and place it
into a `src/java/resources/web_binaries/` folder (replace java with kotlin/scala etc).

Then you should be able to read webp files by using the `ImageLoader` as normal:

```java
ImmutableImage.loader().fromFile(new File("someimage.webp"))
````




## Format Detection

If you are interested in detecting the format of an image (which you don't need to do when simply loading an image,
 as Scrimage will figure it out for you) then you can use the `FormatDetector`.

The detector recognises PNG, JPEG and GIF.

This method does not need to load all bytes, only the initial few bytes to determine what the format is.

The return value is an Optional<Format> with the detected format, or a None if unable to detect.

```kotlin
// detect from a byte array
FormatDetector.detect(bytes)

// detect from an input stream
FormatDetector.detect(inputStream)
```
