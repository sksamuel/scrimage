Invert
======

Returns a new image with the colours inverted, producing a photographic negative.

Each of the red, green and blue channels is replaced by `255` minus its value. The alpha
channel is left unchanged, so transparent areas stay transparent.

```kotlin
val negative = image.invert()
```

Inverting twice returns the original colours:

```kotlin
image.invert().invert() // equal colours to image
```
