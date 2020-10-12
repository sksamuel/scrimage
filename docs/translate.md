Translate
=========

Returns a new image with the original image translated (moved) the specified number of pixels.
We can translate on either axis and in either direction. Additionally, a background colour can be specified.



### Examples

Using this image as our input:

![source image](images/input_640_360.jpg)


```kotlin
image.translate(100, 0) // translate 100 pixels right
```

![image](images/translate_100_0.jpg)



```kotlin
image.translate(100, 0, Color.BLUE) // translate 100 pixels right with specified bg
```

![image](images/translate_100_0_blue.jpg)



```kotlin
image.translate(120, 80) // translate on both axis
```

![image](images/translate_120_80.jpg)



```kotlin
image.translate(120, -80, Color.RED) // translate in negative directions with specified bg
```

![image](images/translate_-120_-80.jpg)
