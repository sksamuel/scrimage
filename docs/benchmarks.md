Benchmarks
==========


Some noddy benchmarks comparing the speed of rescaling an image.
I've compared the basic getScaledInstance method in java.awt.Image with ImgScalr and Scrimage.
ImgScalr delegates to awt.Graphics2D for its rendering. Scrimage adapts the methods implemented by Morten Nobel.

The original Scala benchmark that produced the figures below no longer exists in the repository. Current benchmarks are
JMH classes in the `scrimage-benchmarks` module (for example `scrimage-benchmarks/src/jmh/java/com/sksamuel/scrimage/benchmarks/ScaleBenchmarks.java`),
run with `./gradlew :scrimage-benchmarks:jmh`.

The results are for 100 runs of a resize to a fixed width / height.

| Library | Fast | High Quality (Method) |
| --------- | --------- | --------- |
| java.awt.Image.getScaledInstance | 11006ms | 17134ms (Area Averaging) |
| ImgScalr | 57ms | 5018ms (ImgScalr.Quality) |
| Scrimage | 113ms | 2730ms (Bicubic) |

As you can see, ImgScalr is the fastest for a simple rescale, but Scrimage is much faster than the rest for a high quality scale.



