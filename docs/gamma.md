Gamma
=====

Returns a new image with [gamma correction](https://en.wikipedia.org/wiki/Gamma_correction)
applied to the red, green and blue channels. The alpha channel is left unchanged.

A gamma of `1.0` leaves the image unchanged. Values greater than `1.0` brighten the midtones,
and values below `1.0` darken them. Each channel value `v` in the range `0..255` is mapped
to `255 * (v / 255) ^ (1 / gamma)`.

```kotlin
image.gamma(2.0) // brighten the midtones
image.gamma(0.5) // darken the midtones
```

!!! note
    For gamma correction as part of a filter pipeline in the `scrimage-filters` module,
    see `GammaFilter`, which produces the same result.
