Fill
====


Creates a new image with the same dimensions as a source image, setting each pixel to the specified colour.




### Examples

Using this image as our input:

![source image](images/input_640_360.jpg)


```kotlin
// a new image with all pixels blue, and the dimensions
// taken from the source image
image.fill(Color.BLUE)
```

![image](images/fill_blue.jpg)
