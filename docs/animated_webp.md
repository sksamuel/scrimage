Animated WebP
===============

Scrimage supports both reading and writing animated WebPs, using the libwebp
toolchain under the hood:

| Operation | Tool wrapped | Scrimage class |
|---|---|---|
| Decompose into frames + metadata | `anim_dump` + `webpmux` | `AnimatedWebpReader` → `AnimatedWebp` |
| Compose from a sequence of frames | `img2webp` | `StreamingWebpWriter` |

All required binaries ship with the `scrimage-webp` module for Linux (x86-64,
aarch64), macOS (x86-64, arm64) and Windows (x64); see the
[Webp page](webp.md) for how to override the bundled binaries.

## Reading

We read an instance of an `AnimatedWebp` by using `AnimatedWebpReader.read`,
passing in an `ImageSource`. The image source can be constructed from files,
bytes, input streams and so on.

Once we have the `AnimatedWebp`, we can inspect it to retrieve an
`ImmutableImage` per frame, the total number of frames, the per-frame display
delay, the canvas dimensions, and the loop count (`0` means loop forever).

=== "Java"

    ```java
    AnimatedWebp webp = AnimatedWebpReader.read(ImageSource.of(new File("animated.webp")));
    int frameCount        = webp.getFrameCount();
    ImmutableImage first  = webp.getFrame(0);
    ImmutableImage last   = webp.getFrame(frameCount - 1);
    Duration firstDelay   = webp.getDelay(0);
    Dimension canvas      = webp.getDimensions();
    int loopCount         = webp.getLoopCount();   // 0 = forever
    ```

=== "Kotlin"

    ```kotlin
    val webp = AnimatedWebpReader.read(ImageSource.of(File("animated.webp")))
    val firstFrame = webp.frames.first()
    val lastFrame  = webp.frames.last()
    val firstDelay = webp.getDelay(0)
    ```

=== "Scala"

    ```scala
    val webp = AnimatedWebpReader.read(ImageSource.of(new File("animated.webp")))
    val firstFrame = webp.getFrames.get(0)
    val lastFrame  = webp.getFrames.get(webp.getFrameCount - 1)
    ```

!!! note
    Each frame returned by `getFrame(n)` is the **fully composed** image for
    that point in the animation — `anim_dump` applies inter-frame disposal and
    blending for you. You don't need to manually composite earlier frames to
    reproduce the on-screen result.

The reader also preserves the original WebP bytes, accessible via
`getBytes()`, in case you need to forward the file unchanged after inspecting
its metadata.

## Writing

Using the `StreamingWebpWriter` class, first we create an instance of the writer,
configuring the frame delay (delay between images) and whether the animation should
loop forever. Then we open a stream, specifying either an output path or an output
stream — the AWT image type isn't required because `img2webp` accepts any image
scrimage can render.

=== "Java"

    ```java
    StreamingWebpWriter writer = new StreamingWebpWriter()
       .withFrameDelay(Duration.ofSeconds(2))
       .withInfiniteLoop(true);
    StreamingWebpWriter.WebpStream webp = writer.prepareStream("/path/to/animated.webp");
    ```

=== "Kotlin"

    ```kotlin
    val writer = StreamingWebpWriter()
       .withFrameDelay(Duration.ofSeconds(2))
       .withInfiniteLoop(true)
    val webp = writer.prepareStream("/path/to/animated.webp")
    ```

=== "Scala"

    ```scala
    val writer = new StreamingWebpWriter()
       .withFrameDelay(Duration.ofSeconds(2))
       .withInfiniteLoop(true)
    val webp = writer.prepareStream("/path/to/animated.webp")
    ```


Next we can add as many images as we want, each an instance of an `ImmutableImage`.
The default frame delay applies, or you can pass a `Duration` to override the delay
for an individual frame.

```
webp.writeFrame(image0);
webp.writeFrame(image1, Duration.ofMillis(500));
webp.writeFrame(imageN);
```

Finally, we close the stream — at which point the buffered frames are encoded
into the destination. `WebpStream` extends `AutoCloseable`, so the recommended
pattern is try-with-resources:

=== "Java"

    ```java
    try (StreamingWebpWriter.WebpStream webp = writer.prepareStream("/path/to/animated.webp")) {
       webp.writeFrame(image0);
       webp.writeFrame(image1);
       webp.writeFrame(imageN);
    }
    ```

=== "Kotlin"

    ```kotlin
    writer.prepareStream("/path/to/animated.webp").use { webp ->
       webp.writeFrame(image0)
       webp.writeFrame(image1)
       webp.writeFrame(imageN)
    }
    ```

=== "Scala"

    ```scala
    val webp = writer.prepareStream("/path/to/animated.webp")
    try {
       webp.writeFrame(image0)
       webp.writeFrame(image1)
       webp.writeFrame(imageN)
    } finally {
       webp.close()
    }
    ```

If you'd rather close it explicitly:

```
webp.close();
```

### Encoding options

In addition to `withFrameDelay` and `withInfiniteLoop`, the writer exposes the
WebP-native compression knobs:

| Method | Effect |
|---|---|
| `withLossless(boolean)` | `true` (the default) emits `-lossless`, otherwise `-lossy`. |
| `withQ(int)` | RGB quality factor in `[0, 100]`. |
| `withM(int)` | Encoding method in `[0, 6]` — higher is slower but produces smaller output. |

For example:

```java
StreamingWebpWriter writer = new StreamingWebpWriter()
   .withFrameDelay(Duration.ofMillis(100))
   .withInfiniteLoop(true)
   .withLossless(false)
   .withQ(75)
   .withM(4);
```

!!! note
    Browsers have a minimum frame delay. If you try to set the frame delay lower than the minimum for that browser, the browser will use the default frame delay. *The default frame delay is not equal to the minimum*. Chrome and Firefox have a minimum frame delay of 0.2 seconds and IE and Safari 0.6 seconds.
