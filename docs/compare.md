Comparing Images
================

Scrimage provides two ways to measure how alike two images are. Both require the images to have
the same dimensions and throw `IllegalArgumentException` otherwise.

### diff

`diff` returns a new image representing the absolute per-channel difference between two images.
Pixels that are identical in both images become black; the greater the difference in a channel,
the brighter that channel appears in the result. The output is fully opaque — differences in the
alpha channel are not visualised.

```kotlin
val difference = a.diff(b)
```

This is useful for visualising *where* two images differ, for example when debugging a filter or
a rendering change.

### rmse

`rmse` returns the [root mean square error](https://en.wikipedia.org/wiki/Root-mean-square_deviation)
between two images, computed over the red, green and blue channels and normalised to the range
`0.0` to `1.0`:

* `0.0` means the images are pixel-identical.
* `1.0` means every channel of every pixel differs by the maximum of `255`.

```kotlin
val error = a.rmse(b)
if (error < 0.01) {
   // the images are visually near-identical
}
```

Because it collapses the whole comparison to a single number, `rmse` is well suited to
regression tests, where `equals` (which is an exact, all-or-nothing match) is too strict to
tolerate minor encoding differences.
