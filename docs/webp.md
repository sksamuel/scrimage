## Webp Support

[Webp](https://developers.google.com/speed/webp/faq) is a method of lossy and lossless compression created by Google.

Scrimage provides support for webp through the `scrimage-webp` module. To use webp, add this module to your build.

This module uses the `dwebp`, `cwebp` and `gif2webp` binaries, created by Google. The `scrimage-webp` module comes with the
linux_x64, window_x64, mac-10.15 binaries already included (see required [copyright notice](https://github.com/sksamuel/scrimage/blob/master/scrimage-webp/src/main/resources/dist_webp_binaries/LICENSE)).

If you don't wish to use the embedded binaries, then you can [download other versions](https://developers.google.com/speed/webp)
and use the system property "com.sksamuel.scrimage.webp.binary.dir" to configure the directory you placed the binaries:
```shell
java -Dcom.sksamuel.scrimage.webp.binary.dir="/tmp/webp" -jar myprogram.jar
```
Or you can place them on your classpath at `/webp_binaries/{osName}/dwebp` or `/webp_binaries/{osName}/cwebp` or
`/webp_binaries/{osName}/gif2webp`.
The directory set by the system property is always searched first.

`{osName}` must be one of `window`, `linux`, `mac`. ie `/webp_binaries/window/cwebp`.

or just place your binaries into `/webp_binaries/dwebp` or `/webp_binaries/cwebp` or `/webp_binaries/gif2webp`.
then scrimage will use `/webp_binaries/{binary}` regardless of the binaries which is in os specific directory.

Then you should be able to read webp files by using the `ImageLoader` as normal:

```java
ImmutableImage.loader().fromFile(new File("someimage.webp"))
```

And write out images using the WebpWriter image writer, eg.

```java
myimage.output(WebpWriter.MAX_LOSSLESS_COMPRESSION,"output.webp");
```

### Animated Gif to Webp
The writer is configuration with options for compression quality, compression method, and lossless compression factor.

If want to change Animated Gif to webp, you should be able to read animated file by using by `AnimatedGifReader` as normal :

```java
AnimatedGifReader.read(ImageSource.of(File("animated.gif"));
```

And write out AnimatedGif using the Gif2WebpWriter, eg.

```java
animatedGif.bytes(Gif2WebpWriter.DEFAULT);

animatedGif.output(Gif2WebpWriter.DEFAULT, "output.webp");
```

The gif2webp writer is configuration with options for compression quality, compression method, and lossly compression factor.

> animated webp to animated gif is not currently supported in this module.
