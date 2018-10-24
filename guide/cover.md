## Cover

The cover operation creates an image with the specified dimensions, with the original image scaled to cover the entire new canvas size, such that there is no "background". If the source and target dimensions do not have the same aspect ratio then some of the image will be lost as it will have to be "over scaled" to completely cover the target image.

Compare this operation with [fit](https://github.com/sksamuel/scrimage/blob/master/guide/fit.md).

To use, invoke with the target dimensions and optionally, a scale method (defaults to ScaleMethod.Bicubic), and the position of the source image in the target (defaults to Position.Center). That last parameter has no effect if the aspect ratios are the same, since the image will cover the target without any loss.

Eg, to create a new cover of 600 x 400 using defaults:
```
image.cover(600, 400)
```
or to create a cover of 240 x 160 using fast scale:
```
image.cover(240, 160, ScaleMethod.FastScale)
```

As always a picture is worth a thousand statements. 

In this example, the original bird image was invoked with
`bird.cover(300,150)`. 

As you can see, the output image is of course 300 x 150, but because the original image had a different aspect ratio, there is no way to exactly scale the image. So some of the top and bottom are lost in order to avoid having background showing. This operation is great when you want images of a uniform size, but you don't any white background. Think facebook cover pictures (the long flat images at the top of profile pages) where the cover image loses a bit of the original but fits in the space perfectly.

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
    <img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/bird_covered.png"/>
</th>
</tr>
</table>
