Overlay
=======

Returns a new image which is the original image plus a specified image overlaid on top.

The overlaying image is placed at an offset starting at 0,0 at the top left.

A negative offset will start the image "off canvas".

Any excess pixels from the overlay image are ignored. For example, if the overlay image is too large, or overlaps.


### Examples

Using these images as our input:

![source image](images/input_640_360.jpg)

![source image](images/picard.jpg)

```kotlin
image.overlay(picard, 25, 25)
```

![image](images/overlay_25_25.jpg)


```kotlin
image.overlay(picard, -75, 0)
```

![image](images/overlay_-75_0.jpg)
