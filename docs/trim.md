## Trim

The trim operation shrinks the canvas size on a specified or all sides.
You can think of it as removing a border.

To remove the same amount of pixels on all side, invoke with a single parameter.


```scala
image.trim(15)
```
or to specify each side seperately

```scala
image.trim(5, 40, 5, 19)
```


### Examples

Using this image as our starting input:

![source image](images/input_640_360.jpg)


```scala
image.trim(50) // will remove 50 pixels on all sides
```

![source image](images/trim_50.jpg)


```scala
 image.trimLeft(100) // remove 100 pixels from the left side
```

![source image](images/trim_l100.jpg)


```scala
image.trimBottom(200) // will remove 200 pixels from the bottom
```

![source image](images/trim_b200.jpg)
