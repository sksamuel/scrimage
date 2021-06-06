Changelog
=========

#### 4.0.20

* Autocrop now works on fully transparent pixels.

#### 4.0.19

* All dependencies no longer transitively include kotlin-stdlib.
* Support webp binary in multiple o/s.

#### 4.0.18

* Core dependency no longer transitively includes kotlin-stdlib.

#### 4.0.17

* Added `withClassloader` option to ImmutableImageLoader to support discovery of `ImageReader` instances on arbitrary
  classpaths.

#### 4.0.16

* Fixed webp support on Windows

#### 4.0.15

* Fixed regression in `ScaleMethod.Bicubic` scale speed.
* Added `ScaleMethod.Progressive` scaling method.
