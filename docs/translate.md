Translate
=========

Returns a new image with the original image translated (moved) the specified number of pixels.
We can translate on either axis and in either direction. Additionally, a background colour can be specified.



### Examples

Using this image as our input:

![source image](images/input_640_360.jpg)


```kotlin
 // translate 100 pixels right
image.translate(100, 0)
```

![image](images/translate_100_0.jpg)



```kotlin
 // translate 100 pixels right with specified bg
image.translate(100, 0, Color.BLUE)
```

![image](images/translate_100_0_blue.jpg)



```kotlin
 // translate on both axis
image.translate(120, 80)
```

![image](images/translate_120_80.jpg)



```kotlin
 // translate in negative directions with specified bg
image.translate(120, -80, Color.RED)
```

![image](images/translate_-120_-80.jpg)
