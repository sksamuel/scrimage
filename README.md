## Scrimage

Scrimage is a Scala library for image manipulating, processing and filtering. The aim of the this library is to provide a
quick and easy way to do the kinds of image operations that most people need, on a daily basis, such as scaling, rotating,
converting between formats and applying filters. It is not intended to provide functionality that might be required by a
more "serious" image application - such as a lightroom clone.

[![Build Status](https://travis-ci.org/sksamuel/scrimage.png)](https://travis-ci.org/sksamuel/scrimage)

### License

This software is licensed under the Apache 2 License.

### API

Scrimage has a consistent, idiomatic scala, and mostly immutable API for accessing images.
I say mostly immutable because for some operations creating a copy of the underlying image
would prove expensive (think a blur filter on a 3000 x 3000 image where you do not care about
keeping the original in memory). For these kinds of operations Scrimage supports a copy
operation that can be used to mimic immutability by first creating a copy and then performing
the operation on that.

### Filters

Scrimage comes with a wide array (or List ;)) of filters. Most of these filters I have not written myself,
but rather collected from other open source imaging libraries (where the license allows - see file headers for attribution), and either re-written them in Scala or wrapped them in a Scala wrapper. 

| Filter Name        | Example           |
| ------------- |:-------------:|
| Blur filter      |  |
| Unsharpen filter      |       |
| Lens blur filter |        |
| Chrome Filter |  |
| 


### Quick Examples

Reading an image, scaling it to 50%, and writing out as PNG
```scala
val in = ... // input stream
val out = ... // output stream
Image(in).scale(0.5).write(out)
```

Reading an image from a java File, applying a blur filter, then flipping it on the horizontal axis, then writing out as a Jpeg
```scala
val inFile = ... // input File
val outFile = ... // output File
Image(inFile).filter(BlurFilter).flipX.write(outFile)
```