Input / Output
=============

Scrimage supports loading and saving of images in the common web formats (currently png, jpeg, gif, tiff).
In addition it extends javas image.io support by giving you an easy way to compress / optimize / interlace the images when saving.

Note: Some the tiff support is via javax.imageio which has issues with some tiff images. If this happens, try using the `scrimage-formats-extra` module which provides extra image formats via the [TwelveMonkeys library](https://github.com/haraldk/TwelveMonkeys).

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


