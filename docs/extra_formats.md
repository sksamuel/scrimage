Formats-Extra
==============

Scrimage provides for support for many common formats outside the standard Java ImageIO classes via an optional module.

The `scrimage-formats-extra` module brings in additional formats such as PCX, BMP, TGA and more. It also includes
a better TIFF reader than the one available in the standard java library and a customizable TIFF writer.

To read from these formats, just add the module to your classpath, nothing more.

To write using these formats, pass an instance of the applicable `ImageWriter` interface when saving out the format.

For example,

```java
image.output(new BmpWriter(), new File("output.bmp"))
```

or

```java
image.output(new PcxWriter(), new File("output.pcx"))
```
