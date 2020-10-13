Pixel Functions
=========

There are several functions available on `ImmutableImage` that operate at the pixel level.

To return all pixels in an image, we can use `image.argb()` or `image.pixels()`. The former returns pixels as ARGB
integer encodings, and the latter returns them as `Pixel` objects.

`image.pixel(x,y)` returns the pixel at the given coordinates and `count()` returns the total number of pixels in the image.

If we want to count the number of pixels that satisfy a predicate, we can use the version of count that accepts a function.

```java
// count number of pixels with some blue
image.count(pixel -> pixel.blue() > 0)
```

If you wish to run a side-effecting function for every pixel, then use `forEach`.

```java
image.forEach(pixel -> {
  if (pixel.blue() == 255) {
    System.out.println("Is totally blue man!");
  }
});
```

If you want to return a new image, mapping each pixel to another colour, then use `map`.

```java
// remove all blue
image.map(pixel -> new RGBColor(pixel.red(), pixel.green(), 0).awt());
```

If you wish to check an image to see if at least one pixel satisfies a predicate, then use `exists`.

```java
// returns true if any pixel has some blue
image.exists(pixel -> pixel.blue() > 0);
```

If you wish to run a predicate against every pixel, and return true if all pixels satisfy that predicate,
then use `forAll`.


```java
// returns true if all pixels have some blue
image.forAll(pixel -> pixel.blue() > 0);
```
