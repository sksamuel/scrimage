Drop
====

Returns a new image with pixels removed from one edge, shrinking the canvas by that amount.
`dropLeft(n)` removes the first `n` columns from the left, so the width becomes `n` pixels smaller.
`dropRight`, `dropTop` and `dropBottom` do the same for the right, top and bottom edges respectively.



### Examples

Using this image as our input:

![source image](images/input_640_360.jpg)


```kotlin
 // remove the leftmost 100 pixels
image.dropLeft(100)
```

![image](images/drop_left_100.jpg)



```kotlin
 // remove the rightmost 100 pixels
image.dropRight(100)
```

![image](images/drop_right_100.jpg)



```kotlin
 // remove the top 80 pixels
image.dropTop(80)
```

![image](images/drop_top_80.jpg)



```kotlin
 // remove the bottom 80 pixels
image.dropBottom(80)
```

![image](images/drop_bottom_80.jpg)
