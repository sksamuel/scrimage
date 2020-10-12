Colors
======

Scrimage provides utilities for dealing with colours in multiple color spaces, such as RBB, HSL, HSV and so on.


### X11 Colors

There is a full list of [X11 defined colors](https://en.wikipedia.org/wiki/X11_color_names) in the `X11Colorlist` class. These can be used and converted to AWT `Color` when you need more than the defaults built into the JDK.

For example,

`val awt = X11Colorlist.MistyRose.awt()`



