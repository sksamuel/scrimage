### FAQ

* I know the format of the files I want to load, can I optimize the parsing process?

By constraining the ImageReaders to a specific format(s), the image parsers don't need to work out what format your file
is in.
You can use something like the following as an example for a JPEG only loader.

```kotlin
val reader = ImageIO.getImageReadersByFormatName("JPEG").asSequence().first()
val jpegLoader = ImmutableImageLoader.create().withImageReaders(listOf(ImageIOReader(listOf(reader))))
```

* I have added `scrimage-webp` but I still can't load WebP images when I create a far jar.

Scrimage, like many other libraries, uses the `ServiceLoader` mechanism to load image readers and writers. This
mechanism is based on the `META-INF/services` directory in the JAR file. When you create a fat jar, you must ensure that
all `META-INF/services` directories are merged, otherwise the `ServiceLoader` mechanism will not work as expected.

For instance, if you are using the gradle shadow plugin, you can read
instructions [here](https://gradleup.com/shadow/configuration/merging/#merging-service-descriptor-files/). If you are using SBT
assembly, instructions are [here](https://github.com/sbt/sbt-assembly?tab=readme-ov-file#merge-strategy).
Other fat jar plugins should have similar instructions.
