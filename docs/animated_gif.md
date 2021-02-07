Animated GIFs
===============

Scrimage supports animated GIFs using the `StreamingGifWriter` class.

First we create an instance of the writer, specifying the frame delay (delay between images) and if we should loop.
Then we open a stream, specifying the output path, and the AWT image type.

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





