## Fit

The fit operation will create an image that is exactly the specified dimensions, and the given image is scaled to fit inside, such that none of the original image is lost. If the target dimensions do not have the same aspect ratio as the source image then the excess "space" will be set to a default background color.

Compare this operation with cover.

To use, invoke with the target dimenions and optionally, a background color (defaults to Color.WHITE), a scale method (defaults to ScaleMethod.Bicubic), and the position of the source image in the target (defaults to Position.Center). That last parameter has no effect if the aspect ratios are the same (because in that case the image would fit exactly inside).

Eg, to create an image of 600 x 400 using defaults:
```
image.fit(600,400)
```
or to create 240x160 with black background.
```
image.cover(240,160, Color.BLACK)
```

As always a picture is worth a thousand lines of code. In this example, the original bird image was invoked with `bird.fit(300,150, Color.WHITE)`. As you can see, the output image is of course 300x150, but because the original image had a different aspect ratio, there is no way to exactly scale the image. So the original image is scaled to fit inside and the excess space is set to the given background color (in this case white. Perhaps a different color would have been a more effective demo).

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
    <img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/bird_small.png"/>
</th>
<th>
    <img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/bird_fitted.png"/>
</th>
</tr>
</table>