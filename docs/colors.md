Colors
======

Most functions in Scrimage that deal with colours, accept an instance of java.awt.Color, however this class is limited.
Scrimage provides its own `com.sksamuel.scrimage.color.Color` interface with implementations in the following color spaces:

* RGB
* CMYK
* Grayscale
* HSL
* HSV

Each of these implementations can be converted to the other. For example:

```
new RGBColor(255, 0, 255).toHSV();
```

or

```
new HSLColor(100f, 0.5f, 0.3f, 1f).toRGB();
```

When you want to create a colour to pass to a scrimage function, you can convert the Scrimage color type to an AWT color type via the `toAwt()` method.

You can retrieve the `average()` color value from  `color`.

### X11 Colors

There is a full list of [X11 defined colors](https://en.wikipedia.org/wiki/X11_color_names) in the `X11Colorlist` class.
These can be used and converted to an AWT `Color` when you need more than the defaults built into the JDK.

For example, to copy an image, setting all pixels to _misty rose_, we can use the following code:

`image.fill(X11Colorlist.MistyRose.awt())`




