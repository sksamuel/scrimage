# Benchmarking notebook

### Setup
Running
./gradlew scrimage-benchmarks:jmh -Pincludes=testJpegWritingOfTransparentImage
With:
OpenJDK 64-Bit Server VM Corretto-17.0.6.10.1 (build 17.0.6+10-LTS, mixed mode, sharing)

## Results

### Starting situation (After 0ac6e717eb1209931553641533a64c268bde87cd)
```
Benchmark                                                                           Mode  Cnt         Score        Error   Units
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage                      avgt    3        47.722 ±     16.861   ms/op
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.alloc.rate       avgt    3      1659.080 ±    594.940  MB/sec
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.alloc.rate.norm  avgt    3  83000739.473 ± 124693.723    B/op
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.count            avgt    3        56.000               counts
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.time             avgt    3      2793.000                   ms
```

### Skip creation of Array of pixels before-hand in replaceTransparencyInPlace and use iterator instead
```
Benchmark                                                                           Mode  Cnt         Score       Error   Units
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage                      avgt    3        35.520 ±     2.477   ms/op
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.alloc.rate       avgt    3      1770.621 ±   123.737  MB/sec
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.alloc.rate.norm  avgt    3  65948072.753 ± 21026.857    B/op
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.count            avgt    3       161.000              counts
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.time             avgt    3       102.000                  ms
```
