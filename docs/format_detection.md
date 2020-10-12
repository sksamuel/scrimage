Format Detection
==========

If you are interested in detecting the format of an image (which you don't need to do when simply loading an image,
 as Scrimage will figure it out for you) then you can use the `FormatDetector`.

The detector recognises PNG, JPEG and GIF.

This method does not need to load all bytes, only the initial few bytes to determine what the format is.

The return value is an Optional<Format> with the detected format, or a None if unable to detect.

```kotlin
// detect from a byte array
FormatDetector.detect(bytes)

// detect from an input stream
FormatDetector.detect(inputStream)
```
