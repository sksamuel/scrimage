Create
=====

There are a couple of ways to create a completely empty image.

The first is with the `create` static method on `ImmutableImage`. This allows us to specify the width, height
and optionally the AWT image type (eg 4 byte ARGB).

For example,

```kotlin
// defaults to TYPE_INT_ARGB
ImmutableImage.create(400, 300)

// specifying the image type
ImmutableImage.create(400, 300, BufferedImage.TYPE_4BYTE_ABGR)
```

Another way to create an image, is to call `blank` on an existing image. This will return a new image
with the same type and size and an uninitialized raster.

```kotlin
image.blank()
```

Finally, `copy` allows us to copy an existing image, with each pixel copied. This function allows us to
specify the type of the copy as well.



```kotlin
// copy with the data duplicated
image.copy()

// copy with the data duplicated, and the raster using the specified type
ImmutableImage.copy(BufferedImage.TYPE_4BYTE_ABGR)
```
