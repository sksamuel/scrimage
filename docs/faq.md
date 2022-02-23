### FAQ

* I know the format of the files I want to load, can I optimize the parsing process?

By constraining the ImageReaders to a specific format(s), the image parsers don't need to work out what format your file is in.
You can use something like the following as an example for a JPEG only loader.

```kotlin
val reader = ImageIO.getImageReadersByFormatName("JPEG").asSequence().first()
val jpegLoader = ImmutableImageLoader.create().withImageReaders(listOf(ImageIOReader(listOf(reader))))
```
