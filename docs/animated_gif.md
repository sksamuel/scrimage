Animated GIFs
===============

Scrimage supports animated GIFs using the `StreamingGifWriter` class.

First we create an instance of the writer, specifying the delay between images and if we should loop.
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

```kotlin
gif.finish();
```
