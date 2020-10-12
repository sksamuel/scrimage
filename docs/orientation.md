Orientation
===========

Apple iPhone's have this annoying "feature" where an image taken when the phone is rotated is not saved as a rotated file. Instead the image is always
saved as landscape with a flag set to whether it was portrait or not. Scrimage will detect this flag, if it is present on the file, and correct the
orientation for you automatically. Most image readers do this, such as web browsers, but you might have noticed some things do not, such as intellij.

Note: This can be disabled by setting `detectOrientation(false)` on the `ImmutableImage.loader()` instance.

