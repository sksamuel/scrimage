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

The following example was the result of `bird.trim(20)`

| Before | After |
|--------|-------|
|<img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/bird_small.png"/>| <img src="https://raw.github.com/sksamuel/scrimage/master/examples/images/bird_trimmed.png"/>|

