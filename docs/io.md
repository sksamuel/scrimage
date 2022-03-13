Reading / Writing
=================

Scrimage supports loading and saving of images in the common web formats (png, jpeg, gif, tiff, webp).
In addition, it extends javas image.io support by giving you an easy way to set options on the output format when saving.


!!! warning
    The TIFF support via javax.imageio has issues with some tiff images.
    If this happens, try using the `scrimage-formats-extra` module which provides extra TIFF support via the [TwelveMonkeys library](https://github.com/haraldk/TwelveMonkeys).


## Reading

To load an image we use the `ImmutableImageLoader` interface.
An instance can be created via `ImmutableImage.loader()`.

This allows us to customize loading behavior and specify the input source.
Supported sources are input streams, byte arrays, files, paths, classpath resources or anything that implements
the `ImageSource` interface.


For example, to load an image from the filesystem:


=== "Java"

    ```java
    ImmutableImage image = ImmutableImage.loader().fromFile(file);
    ```

=== "Kotlin"

    ```kotlin
    val image = ImmutableImage.loader().fromFile(file)
    ```

=== "Scala"

    ```scala
    val image = ImmutableImage.loader().fromFile(file)
    ```

or to load from a byte array:


=== "Java"

    ```java
    ImmutableImage image = ImmutableImage.loader().fromBytes(bytes);
    ```

=== "Kotlin"

    ```kotlin
    val image = ImmutableImage.loader().fromBytes(bytes)
    ```

=== "Scala"

    ```scala
    val image = ImmutableImage.loader().fromBytes(bytes)
    ```


We can load from byte arrays, streams, files, paths, resources and so on.

### Image loader options

The `ImmutableImageLoader` has several options to customize loading.


| Option            | Description                                                                                                                                                   |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| detectOrientation | If set to true (the default) then if the image has metadata that indeeds its orientation, then scrimage will rotate the image back to landscape               |
| detectMetadata    | If set to true (the default) then scrimage will attempt to parse the metadata tags (if any) present in the file                                               |
| type              | Sets the BufferedImage type that the loaded image should use. If unspecified then the default of the reader implementation is used                            |
| sourceRegion      | Sets an area to load from the image. If you are loading an image to immediately crop, then this operation can result in less bytes being read from the source |


## Writing

To save a method, Scrimage requires an `ImageWriter` for the format you wish to persist to.
Scrimage does not use the file extension as a way to infer the format.

Then you can use `output` or `bytes` to either write to a file or a byte array respectively.

For example, to save an image as a PNG to a file:



=== "Java"

    ```java
    ImmutableImage image = ... // some image
    // write out to a file
    image.output(PngWriter.Default, new File("/home/sam/spaghetti.png"));
    ```

=== "Kotlin"

    ```kotlin
    val image = ... // some image
    // write out to a file
    image.output(PngWriter.Default, File("/home/sam/spaghetti.png"))
    ```

=== "Scala"

    ```scala
    val image = ... // some image
    // write out to a file
    image.output(PngWriter.Default, new File("/home/sam/spaghetti.png"))
    ```



If you want to override the configuration for a writer then you can do this when you create the writer.
For example to save a JPEG with 50% compression:



=== "Java"

    ```java
    JpegWriter writer = new JpegWriter().withCompression(50).withProgressive(true);
    image.output(writer, new File("/home/sam/spaghetti.png"));
    ```

=== "Kotlin"

    ```kotlin
    val writer = JpegWriter().withCompression(50).withProgressive(true)
    image.output(writer, File("/home/sam/spaghetti.png"))
    ```

=== "Scala"

    ```scala
    val writer = new JpegWriter().withCompression(50).withProgressive(true)
    image.output(writer, new File("/home/sam/spaghetti.png"))
    ```



If you want to override the configuration for a writer then you can do this when you create the writer.
For example to save a JPEG with 50% compression as a byte array:


=== "Java"

    ```java
    JpegWriter writer = new JpegWriter().withCompression(50).withProgressive(true);
    image.bytes(writer);
    ```

=== "Kotlin"

    ```kotlin
    val writer = JpegWriter().withCompression(50).withProgressive(true)
    image.bytes(writer)
    ```

=== "Scala"

    ```scala
    val writer = new JpegWriter().withCompression(50).withProgressive(true)
    image.bytes(writer)
    ```


## Supported Writers

The available writers along with supported options are:

| Writer       | Option      | Description                                                                                       |
|--------------|-------------|---------------------------------------------------------------------------------------------------|
| `JpegWriter` | compression | Set a value between 0 (full lossy compression) and 100 (full quality / no compression)            |
|              | progressive | If true then data is compressed in multiple passes of progressively higher detail                 |
| `PngWriter`  | compression | Set to a value between 0 (no compression) and 9 (max compression). Compression in PNG is lossless |
| `GifWriter`  | progressive | If true then data is compressed in multiple passes of progressively higher detail                 |
