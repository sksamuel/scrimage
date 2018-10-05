package com.sksamuel.scrimage

import java.awt.image.BufferedImage

import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

class ImageTest extends FunSuite with BeforeAndAfter with Matchers {

  private val image = Image.fromResource("/com/sksamuel/scrimage/bird.jpg")
  private val small = Image.fromResource("/bird_small.png")
  private val chip = Image.fromResource("/transparent_chip.png")
  private val turing = Image.fromResource("/com/sksamuel/scrimage/turing.jpg")

  test("ratio happy path") {
    val awt1 = new BufferedImage(200, 400, BufferedImage.TYPE_INT_ARGB)
    assert(0.5 === Image.fromAwt(awt1).ratio)

    val awt2 = new BufferedImage(400, 200, BufferedImage.TYPE_INT_ARGB)
    assert(2 === Image.fromAwt(awt2).ratio)

    val awt3 = new BufferedImage(333, 333, BufferedImage.TYPE_INT_ARGB)
    assert(1 === Image.fromAwt(awt3).ratio)

    val awt4 = new BufferedImage(333, 111, BufferedImage.TYPE_INT_ARGB)
    assert(3.0 === Image.fromAwt(awt4).ratio)

    val awt5 = new BufferedImage(111, 333, BufferedImage.TYPE_INT_ARGB)
    assert(1 / 3d === Image.fromAwt(awt5).ratio)
  }

  test("copy returns a new backing image") {
    val copy = image.copy
    assert(copy.awt().hashCode != image.awt().hashCode)
  }

  test("scaling correct scales the image") {
    image.scaleTo(486, 324) shouldBe Image.fromResource("/bird_486_324.png")
  }

  test("when trimming the new image has the trimmed dimensions") {
    val trimmed = image.trim(3, 4, 5, 6)
    assert(image.width - 3 - 5 === trimmed.width)
    assert(image.height - 4 - 6 === trimmed.height)
  }

  test("when trimming the new image is not empty") {
    val trimmed = image.trim(3, 4, 5, 6)
    assert(!trimmed.forall(new PixelPredicate {
      override def test(x: Int, y: Int, p: Pixel): Boolean = p.toInt == 0xFF000000 || p.toInt == 0xFFFFFFFF
    }))
  }

  test("when resizing by pixels then the output image has the given dimensions") {
    val scaled = image.resizeTo(440, 505)
    assert(440 === scaled.width)
    assert(505 === scaled.height)
  }

  test("when resizing by scale factor then the output image has the scaled dimensions") {
    val scaled = image.resize(0.5)
    assert(972 === scaled.width)
    assert(648 === scaled.height)
  }

  test("dimensions happy path") {
    val awt = new BufferedImage(200, 400, BufferedImage.TYPE_INT_ARGB)
    assert(new Dimension(200, 400) === Image.fromAwt(awt).dimensions)
  }

  test("pixel returns correct ARGB integer") {
    val image = Image.filled(50, 30, Color(0, 0, 0, 0))
    val g = image.awt().getGraphics
    g.setColor(java.awt.Color.RED)
    g.fillRect(10, 10, 10, 10)
    g.dispose()
    //image.updateFromAWT()
    assert(0 === image.pixel(0, 0).toInt)
    assert(0 === image.pixel(9, 10).toInt)
    assert(0 === image.pixel(10, 9).toInt)
    assert(0xFFFF0000 === image.pixel(10, 10).toInt)
    assert(0xFFFF0000 === image.pixel(19, 19).toInt)
    assert(0 === image.pixel(20, 20).toInt)
  }

  test("pixel array has correct number of pixels") {
    val image = Image.filled(50, 30, Color(0, 0, 0, 0))
    assert(1500 === image.pixels.length)
  }

  test("pixel array has correct ARGB integer") {
    val image = Image.filled(50, 30, Color(0, 0, 0, 0))
    val g = image.awt.getGraphics
    g.setColor(java.awt.Color.RED)
    g.fillRect(10, 10, 10, 10)
    g.dispose()
    // image.updateFromAWT()
    image.pixels()
      .apply(0).toInt shouldBe 0
    image.pixels()
      .apply(765).toInt shouldBe 0xFFFF0000
  }

  test("when created a filled copy then the dimensions are the same as the original") {
    val copy1 = image.fill(java.awt.Color.RED)
    assert(1944 === copy1.width)
    assert(1296 === copy1.height)

    val copy2 = image.fill(0x00FF00FF)
    assert(1944 === copy2.width)
    assert(1296 === copy2.height)

    val copy3 = image.fill(java.awt.Color.WHITE)
    assert(1944 === copy3.width)
    assert(1296 === copy3.height)
  }

  test("hashCode and equals reflects proper object equality") {
    val bird = Image.fromResource("/com/sksamuel/scrimage/bird.jpg")

    assert(image.hashCode === bird.hashCode)
    assert(image === bird)

    val otherImage = Image.fromAwt(new BufferedImage(445, 464, Image.CANONICAL_DATA_TYPE))
    assert(otherImage.hashCode != image.hashCode)
    assert(otherImage != image)
  }

  test("when creating a blank copy then the dimensions are the same as the original") {
    val copy = image.blank
    assert(1944 === copy.width)
    assert(1296 === copy.height)
  }

  test("when create a new filled image then the dimensions are as specified") {
    val image = Image.filled(595, 911, java.awt.Color.BLACK)
    assert(595 === image.width)
    assert(911 === image.height)
  }

  test("when creating a new empty image then the dimensions are as specified") {
    val image = Image.apply(80, 90)
    assert(80 === image.width)
    assert(90 === image.height)
  }

  test("padding should keep alpha") {
    chip
      .padWith(10, 20, 30, 40, RGBColor(100, 255, 50, 0))
      .underlay(Image.filled(1000, 1000, X11Colorlist.Firebrick)) shouldBe
      Image.fromResource("/com/sksamuel/scrimage/chip_pad.png")
  }

  test("fit should keep alpha") {
    chip.fit(200, 300, Color.Transparent) shouldBe
      Image.fromResource("/com/sksamuel/scrimage/chip_fit.png")
  }

  test("when padding to a width smaller than the image width then the width is not reduced") {
    val image = Image.apply(85, 56)
    val padded = image.padTo(55, 162)
    assert(85 === padded.width)
  }

  test("when padding to a height smaller than the image height then the height is not reduced") {
    val image = Image.apply(85, 56)
    val padded = image.padTo(90, 15)
    assert(56 === padded.height)
  }

  test("when padding to a width larger than the image width then the width is increased") {
    val image = Image.apply(85, 56)
    val padded = image.padTo(151, 162)
    assert(151 === padded.width)
  }

  test("when padding to a height larger than the image height then the height is increased") {
    val image = Image.apply(85, 56)
    val padded = image.padTo(90, 77)
    assert(77 === padded.height)
  }

  test("when padding to a size larger than the image then the image canvas is increased") {
    val image = Image.apply(85, 56)
    val padded = image.padTo(515, 643)
    assert(515 === padded.width)
    assert(643 === padded.height)
  }

  test("when padding with a border size then the width and height are increased by the right amount") {
    val padded = image.pad(4, java.awt.Color.WHITE)
    assert(1952 === padded.width)
    assert(1304 === padded.height)
  }

  test("trim should revert padWith") {
    val image = Image.apply(85, 56)
    val same = image.padWith(10, 2, 5, 7).trim(10, 2, 5, 7)
    assert(image.width === same.width)
    assert(image.height === same.height)
  }

  test("when flipping on x axis the dimensions are retained") {
    val flipped = image.flipX
    assert(1944 === flipped.width)
    assert(1296 === flipped.height)
  }

  test("when flipping on x axis the image is flipped horizontally") {
    turing.flipX shouldBe Image.fromResource("/com/sksamuel/scrimage/turing_flipx.png")
  }

  test("when flipping on y axis the image is flipped vertically") {
    turing.flipY shouldBe Image.fromResource("/com/sksamuel/scrimage/turing_flipy.png")
  }

  test("when flipping on y axis the dimensions are retained") {
    val flipped = image.flipY
    assert(1944 === flipped.width)
    assert(1296 === flipped.height)
  }

  test("when flipping on x axis a new image is created") {
    val flipped = image.flipX
    assert(!flipped.eq(image))
  }

  test("when flipping on y axis a new image is created") {
    val flipped = image.flipY
    assert(!flipped.eq(image))
  }

  test("when rotating left the width and height are reversed") {
    val rotated = small.rotateLeft
    assert(259 === rotated.width)
    assert(388 === rotated.height)
    rotated shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_rotated_left.png")
  }

  test("when rotating right the width and height are reversed") {
    val rotated = small.rotateRight
    assert(259 === rotated.width)
    assert(388 === rotated.height)
    rotated shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_rotated_right.png")
  }

  test("brightness happy path") {
    val brightened = small.brightness(1.5)
    brightened shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_small_brightened.png")
  }

  test("contrast happy path") {
    val contrasted = small.contrast(3)
    contrasted shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_small_contrasted.png")
  }

  test("when fitting an image the output image should have the specified dimensions") {
    val fitted = image.fit(51, 66)
    assert(51 === fitted.width)
    assert(66 === fitted.height)
  }

  test("a fitted image can be aligned to centre") {
    val actual1 = small.fit(200, 100, color = X11Colorlist.Burlywood2, position = Position.Center)
    val actual2 = turing.fit(200, 400, color = X11Colorlist.Azure, position = Position.Center)
    actual1 shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_small_aligned_centre.png")
    actual2 shouldBe Image.fromResource("/com/sksamuel/scrimage/turing_aligned_centre.png")
  }

  test("a fitted image can be aligned to top left") {
    val actual1 = small.fit(200, 100, color = X11Colorlist.Burlywood2, position = Position.TopLeft)
    val actual2 = turing.fit(200, 400, color = X11Colorlist.Azure, position = Position.TopLeft)
    actual1 shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_small_aligned_top_left.png")
    actual2 shouldBe Image.fromResource("/com/sksamuel/scrimage/turing_aligned_top_left.png")
  }

  test("a fitted image can be aligned to top right") {
    val actual1 = small.fit(200, 100, color = X11Colorlist.Burlywood2, position = Position.TopRight)
    val actual2 = turing.fit(200, 400, color = X11Colorlist.Azure, position = Position.TopRight)
    actual1 shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_small_aligned_top_right.png")
    actual2 shouldBe Image.fromResource("/com/sksamuel/scrimage/turing_aligned_top_right.png")
  }

  test("a fitted image can be aligned to bottom right") {
    val actual1 = small.fit(200, 100, color = X11Colorlist.Burlywood2, position = Position.BottomRight)
    val actual2 = turing.fit(200, 400, color = X11Colorlist.Azure, position = Position.BottomRight)
    actual1 shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_small_aligned_bottom_right.png")
    actual2 shouldBe Image.fromResource("/com/sksamuel/scrimage/turing_aligned_bottom_right.png")
  }

  test("a fitted image can be aligned to bottom left") {
    val actual1 = small.fit(200, 100, color = X11Colorlist.Burlywood2, position = Position.BottomLeft)
    val actual2 = turing.fit(200, 400, color = X11Colorlist.Azure, position = Position.BottomLeft)
    actual1 shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_small_aligned_bottom_left.png")
    actual2 shouldBe Image.fromResource("/com/sksamuel/scrimage/turing_aligned_bottom_left.png")
  }

  test("a fitted image can be aligned to centre left") {
    val actual1 = small.fit(200, 100, color = X11Colorlist.Burlywood2, position = Position.CenterLeft)
    val actual2 = turing.fit(200, 400, color = X11Colorlist.Azure, position = Position.CenterLeft)
    actual1 shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_small_aligned_centre_left.png")
    actual2 shouldBe Image.fromResource("/com/sksamuel/scrimage/turing_aligned_centre_left.png")
  }

  test("a fitted image can be aligned to centre right") {
    val actual1 = small.fit(200, 100, color = X11Colorlist.Burlywood2, position = Position.CenterRight)
    val actual2 = turing.fit(200, 400, color = X11Colorlist.Azure, position = Position.CenterRight)
    actual1 shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_small_aligned_centre_right.png")
    actual2 shouldBe Image.fromResource("/com/sksamuel/scrimage/turing_aligned_centre_right.png")
  }

  test("a fitted image can be aligned to top cente") {
    val actual1 = small.fit(200, 100, color = X11Colorlist.Burlywood2, position = Position.TopCenter)
    val actual2 = turing.fit(200, 400, color = X11Colorlist.Azure, position = Position.TopCenter)
    actual1 shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_small_aligned_top_centre.png")
    actual2 shouldBe Image.fromResource("/com/sksamuel/scrimage/turing_aligned_top_centre.png")
  }

  test("a fitted image can be aligned to bottom centre") {
    val actual1 = small.fit(200, 100, color = X11Colorlist.Burlywood2, position = Position.BottomCenter)
    val actual2 = turing.fit(200, 400, color = X11Colorlist.Azure, position = Position.BottomCenter)
    actual1 shouldBe Image.fromResource("/com/sksamuel/scrimage/bird_small_aligned_bottom_centre.png")
    actual2 shouldBe Image.fromResource("/com/sksamuel/scrimage/turing_aligned_bottom_centre.png")
  }

  test("when resizing an image the output image should have specified dimensions") {
    val r = image.resizeTo(900, 300)
    assert(900 === r.width)
    assert(300 === r.height)
  }

  test("when resizing an image to the ratio the output image should have specified dimensions") {
    val srcRatio = image.ratio()
    val actual1 = image.resizeToRatio(srcRatio)
    val actual2 = image.resizeToRatio(1)
    val actual3 = image.resizeToRatio(2)
    assert(actual1 === image)
    assert(actual2.ratio === 1)
    assert(actual3.ratio === 2)
  }

  test("when scaling an image the output image should match as expected") {
    val scaled = image.scale(0.25)
    val expected = Image.fromResource("/com/sksamuel/scrimage/bird_scale_025.png")
    assert(expected.width === scaled.width)
    assert(expected.height === scaled.height)
  }

  test("when scaling an image the output image should have specified dimensions") {
    val scaled = image.scaleTo(900, 300)
    assert(900 === scaled.width)
    assert(300 === scaled.height)
  }

  test("when fitting an image the output image should match as expected") {
    val fitted = image.fit(900, 300, java.awt.Color.RED)
    val expected = Image.fromResource("/com/sksamuel/scrimage/bird_fitted2.png")
    assert(fitted.pixels.length === fitted.pixels.length)
    assert(expected == fitted)
  }

  test("when fitting an image the output image should have specified dimensions") {
    val fitted = image.fit(900, 300, java.awt.Color.RED)
    assert(900 === fitted.width)
    assert(300 === fitted.height)
  }

  test("when scaling by width then target image maintains aspect ratio") {
    val scaled = image.scaleToWidth(500)
    assert(scaled.width === 500)
    assert(scaled.ratio - image.ratio < 0.01)
  }

  test("when scaling by height then target image maintains aspect ratio") {
    val scaled = image.scaleToHeight(400)
    assert(scaled.height === 400)
    assert(scaled.ratio - image.ratio < 0.01)
  }

  test("argb returns array of ARGB bytes") {
    val image = Image.filled(20, 20, java.awt.Color.YELLOW)
    val components = image.argb
    assert(400 === components.length)
    for (component <- components)
      assert(component === Array(255, 255, 255, 0))
  }

  test("rgb returns array of RGB bytes") {
    val image = Image.filled(20, 20, java.awt.Color.YELLOW)
    val components = image.rgb
    assert(400 === components.length)
    for (component <- components)
      assert(component === Array(255, 255, 0))
  }

  test("argb pixel returns an array for the ARGB components") {
    val image = Image.filled(20, 20, java.awt.Color.YELLOW)
    val rgb = image.argb(10, 10)
    assert(rgb === Array(255, 255, 255, 0))
  }

  test("rgb pixel returns an array for the RGB components") {
    val image = Image.filled(20, 20, java.awt.Color.YELLOW)
    val argb = image.rgb(10, 10)
    assert(argb === Array(255, 255, 0))
  }

  test("pixel coordinate returns an ARGB integer for the pixel at that coordinate") {
    val image = Image.filled(20, 20, java.awt.Color.YELLOW)
    val pixel = image.pixel(10, 10)
    assert(0xFFFFFF00 === pixel.toInt)
  }

  test("foreach accesses to each pixel") {
    val image = Image.apply(100, 100)
    var count = 0
    image.foreach(new PixelFunction {
      override def apply(x: Int, y: Int, p: Pixel): Unit = count = count + 1
    })
    assert(10000 === count)
  }

  test("map modifies each pixel and returns new image") {
    val image = Image.apply(100, 100)
    val mapped = image.map((_, _, _) => new Pixel(0xFF00FF00))
    for (component <- mapped.argb)
      assert(component === Array(255, 0, 255, 0))
  }

  test("enlarging a canvas with TopLeft should position the image to the left and top") {
    val scaled = image.scaleTo(100, 100)
    val resized = scaled.resizeTo(200, 200, Position.TopLeft)
    assert(200 === resized.width)
    assert(200 === resized.height)
    for (x <- 0 until 100; y <- 0 until 100) assert(scaled.pixel(x, y) === resized.pixel(x, y))
    for (x <- 0 until 200; y <- 100 until 200) resized.pixel(x, y).toARGBInt shouldBe RGBColor(0, 0, 0, 0).toARGBInt
    for (x <- 100 until 200; y <- 0 until 100) resized.pixel(x, y).toARGBInt shouldBe RGBColor(0, 0, 0, 0).toARGBInt
  }

  test("overlay should retain source background") {
    val image1 = Image.filled(100, 100, X11Colorlist.PaleVioletRed1)
    val image2 = Image.filled(75, 75, X11Colorlist.GreenYellow)
    val result = image1.overlay(image2, 10, 10)
    result.color(0, 0) shouldBe X11Colorlist.PaleVioletRed1
    result.color(10, 10) shouldBe X11Colorlist.GreenYellow
    result.color(84, 84) shouldBe X11Colorlist.GreenYellow
    result.color(85, 85) shouldBe X11Colorlist.PaleVioletRed1
    result.color(99, 99) shouldBe X11Colorlist.PaleVioletRed1
  }

  test("enlarging a canvas with BottomRight should position the image to the bottom and to the right") {
    val scaled = image.scaleTo(100, 100)
    val resized = scaled.resizeTo(200, 200, Position.BottomRight)
    assert(200 === resized.width)
    assert(200 === resized.height)
    for (x <- 0 until 100; y <- 0 until 200) resized.pixel(x, y).toARGBInt shouldBe RGBColor(0, 0, 0, 0).toARGBInt
    for (x <- 100 until 200; y <- 0 until 100) resized.pixel(x, y).toARGBInt shouldBe RGBColor(0, 0, 0, 0).toARGBInt
    for (x <- 100 until 200; y <- 100 until 200) assert(scaled.pixel(x - 100, y - 100) === resized.pixel(x, y))
  }

  test("enlarging a canvas with TopRight should position the image to the top and to the right") {
    val scaled = image.scaleTo(100, 100)
    val resized = scaled.resizeTo(200, 200, Position.TopRight)
    assert(200 === resized.width)
    assert(200 === resized.height)

    for (x <- 0 until 100; y <- 0 until 200) resized.pixel(x, y).toARGBInt shouldBe RGBColor(0, 0, 0, 0).toARGBInt
    for (x <- 100 until 200; y <- 100 until 200) resized.pixel(x, y).toARGBInt shouldBe RGBColor(0, 0, 0, 0).toARGBInt
    for (x <- 100 until 200; y <- 0 until 100) assert(scaled.pixel(x - 100, y) === resized.pixel(x, y))
  }

  test("enlarging a canvas with Centre should position the image in the center") {
    val scaled = image.scaleTo(100, 100)
    val resized = scaled.resizeTo(200, 200, Position.Center)
    assert(200 === resized.width)
    assert(200 === resized.height)
    for (x <- 0 until 50; y <- 0 until 50) resized.pixel(x, y).toARGBInt shouldBe RGBColor(0, 0, 0, 0).toARGBInt
    for (x <- 50 until 150; y <- 50 until 150) assert(scaled.pixel(x - 50, y - 50) === resized.pixel(x, y))
    for (x <- 150 until 200; y <- 150 until 200) resized.pixel(x, y).toARGBInt shouldBe RGBColor(0, 0, 0, 0).toARGBInt
  }

  test("when enlarging the background should be set to the specified parameter") {
    val scaled = image.scaleTo(100, 100)
    val resized = scaled.resizeTo(200, 200, Position.Center, java.awt.Color.BLUE)
    for (x <- 0 until 200; y <- 0 until 50) assert(0xFF0000FF === resized.pixel(x, y).toInt)
    for (x <- 0 until 200; y <- 150 until 200) assert(0xFF0000FF === resized.pixel(x, y).toInt)
  }

  test("when bounding, an image under the bounds should not change") {
    val bounded = image.bound(10000, 10000)
    bounded shouldBe image
  }

  test("when bounding, an image on the bounds should not change") {
    image.bound(image.width, image.height) shouldBe image
    image.bound(image.width * 2, image.height) shouldBe image
    image.bound(image.width, image.height * 2) shouldBe image
  }

  test("when bounding, an image under the bounds should be resized") {
    image.bound(image.width / 2, image.height).width shouldBe image.width / 2
    image.bound(image.width / 2, image.height).height shouldBe image.height / 2
    image.bound(image.width, image.height / 2).width shouldBe image.width / 2
    image.bound(image.width, image.height / 2).height shouldBe image.height / 2
  }

  test("when maxing an image the dimensions should not exceed the bounds") {
    val maxed = image.max(20, 20)
    assert(maxed.width <= 20)
    assert(maxed.height <= 20)
  }

  test("when maxing an image vertically the height should equal the target height parameter") {
    val maxed = image.max(200, 20)
    assert(20 === maxed.height)
  }

  test("when maxing an image horizontally the width should equal the target width parameter") {
    val maxed = image.max(20, 156)
    assert(20 === maxed.width)
  }

  test("max operation happy path") {
    val maxed = image.max(200, 200)
    val expected = Image.fromResource("/com/sksamuel/scrimage/bird_bound_200x200.png")
    assert(maxed === expected)
  }

  test("when covering an image the output image should have the specified dimensions") {
    val covered = image.cover(51, 66)
    assert(51 === covered.width)
    assert(66 === covered.height)
  }

  test("cover operation happy path") {
    val covered = image.cover(200, 200)
    val expected = Image.fromResource("/com/sksamuel/scrimage/bird_cover_200x200.png")
    assert(covered === expected)
  }

  test("column") {
    val striped = Image.apply(200, 100).map((x, y, p) => if (y % 2 == 0) new Pixel(255) else new Pixel(0))
    val col = striped.col(51)
    assert(striped.height === col.length)
    for (y <- 0 until striped.height) {
      if (y % 2 == 0) assert(new Pixel(255) === col(y), "col was " + col(y))
      else assert(new Pixel(0) === col(y), "col was " + col(y))
    }
  }

  test("row") {
    val striped = Image.apply(200, 100).map((x, y, p) => if (y % 2 == 0) new Pixel(255) else new Pixel(0))
    val row1 = striped.row(44)
    assert(striped.width === row1.length)
    assert(row1.forall(_ == new Pixel(255)))
    val row2 = striped.row(45)
    assert(striped.width === row2.length)
    assert(row2.forall(_ == new Pixel(0)))
  }

  test("pixels region") {
    val striped = Image.apply(200, 100).map((x, y, p) => if (y % 2 == 0) new Pixel(255) else new Pixel(0))
    val pixels = striped.pixels(10, 10, 10, 10)
    for (k <- 0 until 10)
      assert(new Pixel(255) === pixels(k))
    for (k <- 10 until 19)
      assert(0 === pixels(k).toInt)
    for (k <- 20 until 29)
      assert(new Pixel(255) === pixels(k))
    for (k <- 30 until 39)
      assert(new Pixel(0) === pixels(k))
  }

  test("autocrop removes background") {
    val image = Image.fromResource("/com/sksamuel/scrimage/dyson.png")
    val autocropped = image.autocrop(java.awt.Color.WHITE)
    assert(282 === autocropped.width)
    assert(193 === autocropped.height)
    val expected = Image.fromResource("/com/sksamuel/scrimage/dyson_autocropped.png")
    assert(expected == autocropped)
  }

  test("removeTransparency conserve pixels on non transparent image") {
    val image = Image.fromResource("/com/sksamuel/scrimage/jazz.jpg")
    val withoutTransparency = image.removeTransparency(java.awt.Color.BLACK)
    val pixels = image.pixels
    val pxwt = withoutTransparency.pixels
    assert(pixels.toList === pxwt.toList)
  }
}
