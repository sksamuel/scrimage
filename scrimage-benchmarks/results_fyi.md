# Benchmarking notebook

This journey was inspired by my company noticing that writing some images with scrimage allocated a lot of memory.

I wondered why that is.

### Setup
Running
./gradlew scrimage-benchmarks:jmh -Pincludes=testJpegWritingOfTransparentImage
With:
OpenJDK 64-Bit Server VM Corretto-17.0.6.10.1 (build 17.0.6+10-LTS, mixed mode, sharing)

## Results

### Starting situation (At 04faed43439004d1105b1b6783b6edf32df5af1d)
```
Benchmark                                                                           Mode  Cnt         Score        Error   Units
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage                      avgt    3        47.722 ±     16.861   ms/op
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.alloc.rate       avgt    3      1659.080 ±    594.940  MB/sec
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.alloc.rate.norm  avgt    3  83000739.473 ± 124693.723    B/op
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.count            avgt    3        56.000               counts
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.time             avgt    3      2793.000                   ms
```

### Skip creation of Array of pixels beforehand in replaceTransparencyInPlace and use an iterator instead
This is current ImmutableImage#removeTransparencySlow.
```
Benchmark                                                                           Mode  Cnt         Score       Error   Units
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage                      avgt    3        35.520 ±     2.477   ms/op
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.alloc.rate       avgt    3      1770.621 ±   123.737  MB/sec
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.alloc.rate.norm  avgt    3  65948072.753 ± 21026.857    B/op
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.count            avgt    3       161.000              counts
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.time             avgt    3       102.000                  ms
```

### Use Graphics2D for writing, skipping calls to ColorModel#getRGB for each pixel
This is current ImmutableImage#removeTransparencyFast.
```
Benchmark                                                                           Mode  Cnt         Score        Error   Units
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage                      avgt    3        12.342 ±      1.227   ms/op
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.alloc.rate       avgt    3      1052.704 ±     96.164  MB/sec
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.alloc.rate.norm  avgt    3  13623604.076 ± 110662.316    B/op
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.count            avgt    3       165.000               counts
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.time             avgt    3       114.000                   ms
```
This does, however, alter results.

#### Observations
I think PixelTools.replaceTransparencyWithColor does the transformation from alpha-channelled into RGB-only differently.
For example, with test picture of /transparent_chip.png, pixel (125, 17) is transformed into (202, 90, 101) with
Graphics2D and into (201, 90, 101) with PixelTools.replaceTransparencyWithColor.

Original value is (165, 173, 0, 17).
