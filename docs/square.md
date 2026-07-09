Square
======

Returns a square version of the image by padding the shorter side so that the width and height
become equal. The original image is centred within the new canvas, and an image that is already
square is returned unchanged in size.

This is a convenience wrapper around [pad](pad.md) that computes the target size for you
(`max(width, height)` on each side).

The no-argument form uses a transparent background for the added area:

```kotlin
image.toSquare()
```

Pass a colour to fill the padded area instead:

```kotlin
import java.awt.Color

image.toSquare(Color.BLACK)
```

!!! note
    `toSquare` only ever *adds* pixels. If you instead want to crop the longer side down to a
    square, use [cover](cover.md) with equal width and height, or a [subimage](take.md).
