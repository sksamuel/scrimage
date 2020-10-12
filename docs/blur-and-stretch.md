## Blur and stretch

See [this issue](https://github.com/sksamuel/scrimage/issues/133)

Sometimes when you change canvas size of your image you want to use something more fancy than plain solid color as a background.
One of possible solutions is to use blurred version of your image as a background.
Basically this guide shows one possible way to achieve it using basic `Image` operations and blur filter (`LensBlurFilter` was chosen as an example).

You just need to:
1. scale image to desired dimensions to make a foreground, e.g. using `scaleToRatio`.
In code below this step is omitted for simplicity: `val fgImage = image`
2. scale image using [cover](https://github.com/sksamuel/scrimage/blob/master/guide/cover.md) and apply some blur via `filter` to make a background
3. complete process by putting foreground on top of background using `overlay`

```scala
import java.nio.file.Paths

import com.sksamuel.scrimage.{Image, ScaleMethod}
import com.sksamuel.scrimage.filter.LensBlurFilter

val image = Image.fromPath(Paths.get("examples", "images", "lanzarote_small.jpeg"))
val filter = new LensBlurFilter(12f, 2f, 255f, 10)
// for simplicity of example source image is not scaled
val fgImage = image
val targetWidth = fgImage.width * 2
val targetHeight = fgImage.height
image
  .cover(targetWidth, targetHeight, ScaleMethod.FastScale)
  .filter(filter)
  .overlay(fgImage, x = (targetWidth - fgImage.width) / 2, y = (targetHeight - fgImage.height) / 2)
```

Compare original and result:

<table>
<tr>
<th>
    Before
</th>
<th>
    After
</th>
</tr>
<tr>
<th>
    <img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/lanzarote_small.jpeg"/>
</th>
<th>
    <img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/lanzarote_blur_and_stretch.jpeg"/>
</th>
</tr>
</table>
