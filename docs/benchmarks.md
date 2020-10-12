Benchmarks
==========


### Benchmarks

Some noddy benchmarks comparing the speed of rescaling an image. I've compared the basic getScaledInstance method in java.awt.Image with ImgScalr and Scrimage. ImgScalr delegates to awt.Graphics2D for its rendering. Scrimage adapts the methods implemented by Morten Nobel.

The code is inside src/test/scala/com/sksamuel/scrimage/ScalingBenchmark.scala.

The results are for 100 runs of a resize to a fixed width / height.

| Library | Fast | High Quality (Method) |
| --------- | --------- | --------- |
| java.awt.Image.getScaledInstance | 11006ms | 17134ms (Area Averaging) |
| ImgScalr | 57ms | 5018ms (ImgScalr.Quality) |
| Scrimage | 113ms | 2730ms (Bicubic) |

As you can see, ImgScalr is the fastest for a simple rescale, but Scrimage is much faster than the rest for a high quality scale.



