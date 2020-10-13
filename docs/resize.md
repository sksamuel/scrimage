Resize
=======

Resizes the canvas to the given dimensions.

This does not [scale](scale.md) the image but simply changes the dimensions of the canvas on which the image is sitting.

Specifying a larger size will pad the image with a background color and specifying a smaller size will crop the image.
This is the operation most people want when they think of crop.

We can specify a position which will be used to anchor the source image in the new canvas.

When resizing, we can specify a factor to multiply the current dimensions by, such as `image.resize(0.5)`,
or we can specify target sizes specifically, such as `image.resizeTo(400,300)`, or we can resize the width or
height independently.


### Examples

Using this image as our input:

![source image](images/input_640_360.jpg)


```kotlin
image.resize(0.75)
```

![image](images/resize_0.75.jpg)

```kotlin
// with added colour to show the padded canvas
image.resizeTo(400, 400, Color.MAGENTA)
```

![image](images/resize_400_400.jpg)

```kotlin
// anchoring the source to the bottom right corner
image.resizeTo(400, 300, Position.BottomRight)
```

![image](images/resize_400_300_br.jpg)


```kotlin
// resizing only one dimension
image.resizeToWidth(400)
```

![image](images/resize_400.jpg)
