![logo](SCRIMAGE.png)
=======

![build_test](https://github.com/sksamuel/scrimage/workflows/build_test/badge.svg)
[<img src="https://img.shields.io/maven-central/v/com.sksamuel.scrimage/scrimage-core.svg?label=latest%20release"/>](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22scrimage-core)
[<img src="https://img.shields.io/nexus/s/https/oss.sonatype.org/com.sksamuel.scrimage/scrimage-core.svg?label=latest%20snapshot"/>](https://oss.sonatype.org/content/repositories/snapshots/com/sksamuel/scrimage/)

Scrimage is immutable, functional, and performant JVM library for manipulation of images.

The aim of this library is to provide a simple and concise way to do common image operations, such as resizing to fit
 a required width and height, converting between formats, applying filters and so on.
 It is not intended to provide functionality that might be required by a more "serious" image processing application - such as face recognition or movement tracking.

A typical use case for this library would be creating thumbnails of images uploaded by users in a web app, or bounding a
set of product images so that they all have the same dimensions, or optimizing PNG uploads by users to apply maximum compression,
or applying a grayscale filter in a print application.

To begin, head over to the [microsite](https://sksamuel.github.io/scrimage).
