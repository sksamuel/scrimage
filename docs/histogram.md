Histogram
=========

`histogram` returns the distribution of channel values across all the pixels of an image.
It is available on any image.

```kotlin
val hist = image.histogram()
```

The returned `Histogram` exposes one array per channel. Each is a 256-element `long[]` where
index `i` holds the number of pixels whose value in that channel is exactly `i`:

```kotlin
val red: LongArray = hist.red()
val green: LongArray = hist.green()
val blue: LongArray = hist.blue()
val alpha: LongArray = hist.alpha()
```

There is also a `luminance()` distribution, computed per pixel from the Rec. 709 weights
(`0.2126*R + 0.7152*G + 0.0722*B`). Unlike the individual channels, its counts sum to the
total number of pixels in the image:

```kotlin
val luminance: LongArray = hist.luminance()
```

For example, to find how many pixels are fully black in the blue channel:

```kotlin
val fullyBlackBlue = image.histogram().blue()[0]
```

!!! note
    Each getter returns a fresh copy of the underlying counts, so mutating the returned array
    does not affect the histogram or subsequent calls.
