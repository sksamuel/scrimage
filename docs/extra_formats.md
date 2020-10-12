Extra formats
==============

Scrimage provides for support outside of the standard Java ImageIO classes via two optional modules.

## Formats-Extra

The `scrimage-formats-extra` module brings in additional formats such as PCX, BMP, TGA and more. It also includes
a better TIFF reader than the one available in the standard java library and a customizable TIFF writer.

To read from these formats, just add the module to your classpath, nothing more.

To write using these formats, pass an instance of the applicable `ImageWriter` interface when saving out the format.

For example,

```java
image.output(new BmpWriter(), new File("output.bmp"))
```



## Webp Input Support

Webp loading is available is an additional module `scrimage-webp` and requires that the dwebp decompression
binary is located on the classpath at `/webp_binaries/dwebp`.

The easiest way to achieve this is to [download the binary](https://developers.google.com/speed/webp) and place it
into a `src/java/resources/web_binaries/` folder (replace java with kotlin/scala etc).

Then you should be able to read webp files by using the `ImageLoader` as normal:

```java
ImmutableImage.loader().fromFile(new File("someimage.webp"))
````
