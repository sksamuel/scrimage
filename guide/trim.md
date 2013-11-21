## Trim

The trim operation shrinks the canvas size on each side. You can think of it as removing a border on each side. 

To remove the same amount of pixels on all side, invoke with a single parameter:


```scala
image.trim(15)
```
or to specify each side seperately
```scala
image.trim(5, 40, 5, 19)
```

As always a picture is worth a thousand parameters. This was the result of `bird.trim(20)`

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
    <img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/bird_trimmed.png"/>
</th>
</tr>
</table>