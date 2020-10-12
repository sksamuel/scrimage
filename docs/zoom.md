Zoom
====

Returns a new image that is the result of scaling this image, but without
changing the canvas size.

This can be thought of as similar to zooming in on a camera. When you zoom on a camera, the size of the viewpane
does not increase, and some parts of the image are no longer visible.

The `zoom` operation accepts a scale factor to apply to the dimensions, and optionally a scale method. If
the scale method is not specified, then BICUBIC is used.

### Examples

Using this image as our input:

![source image](images/input_640_360.jpg)


```kotlin
// zoom with default scale method
image.zoom(1.3)
```

![zoom](images/zoom.jpg)


```kotlin
// zoom with a specified scale method.
image.scaleToHeight(200, Scalemethod.FastScale)
```

![zoom](images/zoom_fastscale.jpg)
