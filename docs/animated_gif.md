Animated GIFs
===============

Scrimage supports reading and writing animated GIFs.

## Reading

We read an instance of an `AnimatedGif` by using the `AnimatedGifReader.read` method, passing in an `ImageSource`. The
image source can be constructed from files, bytes, input streams and so on.

Once we have the animated gif object, we can inspect that to retrieve an `ImmutableImage` per frame, the total number of
frames, delay per frame and so on.

=== "Java"

    ```java
    AnimatedGif gif = AnimatedGifReader.read(ImageSource.of(...));
    ImmutableImage firstFrame = gif.getFrame(0);
    ImmutableImage lastFrame = gif.getFrame(gif.getFrameCount() - 1);
    ```

=== "Kotlin"

    ```kotlin
    val gif = AnimatedGifReader.read(ImageSource.of(...))
    val firstFrame = gif.frames().first()
    val lastFrame = gif.frames().last()
    ```

=== "Scala"

    ```scala
    val gif = AnimatedGifReader.read(ImageSource.of(...))
    val firstFrame = gif.frames.head
    val lastFrame = gif.frames.last
    ```


## Writing

Using the `StreamingGifWriter` class, first we create an instance of the writer, specifying the frame delay (delay
between images) and if we should loop. Then we open a stream, specifying the output path, and the AWT image type.

=== "Java"

    ```java
    StreamingGifWriter writer = new StreamingGifWriter(Duration.ofSeconds(2), true);
    GifStream gif = writer.prepareStream("/path/to/gif.gif", BufferedImage.TYPE_INT_ARGB);
    ```

=== "Kotlin"

    ```kotlin
    val writer = StreamingGifWriter(Duration.ofSeconds(2), true)
    val gif = writer.prepareStream("/path/to/gif.gif", BufferedImage.TYPE_INT_ARGB)
    ```

=== "Scala"

    ```scala
    val writer = new StreamingGifWriter(Duration.ofSeconds(2), true)
    val gif = writer.prepareStream("/path/to/gif.gif", BufferedImage.TYPE_INT_ARGB)
    ```


Next we can add as many images as we want, each an instance of an `ImmutableImage`. Eg,

```
gif.writeFrame(image0);
gif.writeFrame(image1);
gif.writeFrame(imageN);
```

Finally, we close the stream by invoking finish, and the GIF is persisted to disk.

```
gif.finish();
```

!!! note
    Browsers have a minimum frame delay. If you try to set the frame delay lower than the minimum for that browser, the browser will use the default frame delay. *The default frame delay is not equal to the minimum*. Chrome and Firefox have a minimum frame delay of 0.2 seconds and IE and Safari 0.6 seconds.





