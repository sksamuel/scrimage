# Benchmarking notebook

### Setup
Running
./gradlew scrimage-benchmarks:jmh -Pincludes=testJpegWritingOfTransparentImage
With:
OpenJDK 64-Bit Server VM Corretto-17.0.6.10.1 (build 17.0.6+10-LTS, mixed mode, sharing)

## Results

### Starting situation (After 0ac6e717eb1209931553641533a64c268bde87cd):
```
Benchmark                                                                           Mode  Cnt         Score        Error   Units
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage                      avgt    3        47.722 ±     16.861   ms/op
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.alloc.rate       avgt    3      1659.080 ±    594.940  MB/sec
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.alloc.rate.norm  avgt    3  83000739.473 ± 124693.723    B/op
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.count            avgt    3        56.000               counts
JpegWriterBenchmarks.Writing.testJpegWritingOfTransparentImage:·gc.time             avgt    3      2793.000                   ms
```
