Changelog
=========

#### 4.0.38

* Added the system property "com.sksamuel.scrimage.webp.binary.dir" to configure the directory of the webp binaries.

#### 4.0.37

* Better error message when image loading fails, to indicate the file type.

#### 4.0.36

* Fixed webp support for windows that was broken in 4.0.34

#### 4.0.35

* Fixed bug stopping PNG compression from being applied to some types of images.

#### 4.0.34

* Updated webp to 1.3.0
* Removed deprecated use of finalizer

#### 4.0.33

* Add withCompression option to StreamingGifWriter #256
* Fixed JpegWriter.NoCompression preset is misleading #255

#### 4.0.32

* Add convert animated gif to animated webp (#253)

#### 4.0.31

* Add dispose method support when writing frames with StreamingGifWriter #242 zalmb8
* Handle temp files when compressing webp (#246)
* Bumped twelve monkeys library to 3.8.2
* Added methods to copy a pixel by changing one component.
* Added method to clone an AWT image with another image type.

#### 4.0.30

* Updated error message on the ImageIOReader.
* Removed noisy println

#### 4.0.29

* Added extra constructor for ImageIOReader that accepts javax readers

#### 4.0.28

* Added `withImageReaders` to ImmutableImageLoader to allow specifying the `ImageReader` implements to use.

#### 4.0.27

* Added process destroy to explicitly remove resources in the webp module.

#### 4.0.26

* Clean up /tmp files when decompressing using the webp module. #240 Jeff Bilicki

#### 4.0.25

* Updated `FormatDetector` to detect webp (#238) Ilia

#### 4.0.24

* Updated `StreamingGifWriter` to support writing to any `OutputStream` in addition to the existing file/path options.

#### 4.0.23

* Fixed issue with autocrop when no changes are required.

#### 4.0.22

* Bumped 12monkeys and commons-io versions.

#### 4.0.21

* Added `AnimatedGifReader.read(inputstream)`, to allow reading each frame of an animated GIF as an `ImmutableImage`.

#### 4.0.20

* Autocrop now works on fully transparent pixels.

#### 4.0.19

* All dependencies no longer transitively include kotlin-stdlib.
* Support webp binary in multiple o/s.

#### 4.0.18

* Core dependency no longer transitively includes kotlin-stdlib.

#### 4.0.17

* Added `withClassloader` option to `ImmutableImageLoader` to support discovery of `ImageReader` instances on arbitrary
  classpaths.

#### 4.0.16

* Fixed webp support on Windows

#### 4.0.15

* Fixed regression in `ScaleMethod.Bicubic` scale speed.
* Added `ScaleMethod.Progressive` scaling method.
