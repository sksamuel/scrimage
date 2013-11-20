## Autocrop

Autocrop is a way of removing "excess" background on an image. Let's say you had an image that was surrounded by a lot of white background, and you wanted to remove that white so that all that remained was the image proper, then autocrop is what you want.

It works by checking the pixels of each row and column at the sides, top, and bottom, and working inwards. It removes those rows and columns if they consist entirely of the color that we wish to remove.

Very simple to use, simply invoke on an image and pass in a color that should be used as the background detection color. 

For example to remove excess white background:

```
image.autocrop(Color.WHITE)
```
or to remove a background of a custom color you could do:
```
image.autocrop(new Color(255,235,10))
```

As always a picture is worth a thousand bytecodes. It's hard to see the white background example (obviously) so I've placed it inside gray table cells so the result is more obvious.

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
    <img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/dyson.png"/>
</th>
<th>
    <img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/dyson_autocropped.png"/>
</th>
</tr>
</table>