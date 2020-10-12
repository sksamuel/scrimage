## Autocrop

Autocrop is a way of removing excess background on an image.

Let's say you had an image that was surrounded by a lot of white background,
and you wanted to remove that background so that all that remained was the main image,
then autocrop is what you want.

It works by checking the pixels of each row and column at the sides, top, and bottom, and working inwards.
It removes those rows and columns if they consist entirely of the specified colour.

To use, invoke `autocrop` on an image and pass in the colour to be used as the background detection color.

For example to remove excess white background:

```kotlin
image.autocrop(Color.WHITE)
```

or to remove a background of a custom color you could do:
```kotlin
image.autocrop(new RGBColor(255, 235, 10).awt())
```


### Examples

Using this image as our input:

![source image](images/autocrop_before.png)

```kotlin
image.autocrop(new RGBColor(255, 107, 0).awt())
```

![source image](images/autocrop_after.png)
