Changelog
=========

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
