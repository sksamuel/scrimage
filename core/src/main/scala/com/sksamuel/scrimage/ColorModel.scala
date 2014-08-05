package com.sksamuel.scrimage

trait ColorModel {
    type PixelType
    def n_comp: Int
    def getComp(comp: Int, pixel: PixelType) : Int
    def unpack(pixel: PixelType, out: Array[Int] = Array.ofDim[Int](n_comp)) : Array[Int]
    def pack(comps :Array[Int]) : PixelType
    def toARGB(pixel: PixelType): Int
    def fromARGB(c: Int): PixelType
    def toColor(pixel: PixelType): Color
    def fromColor(color: Color): PixelType
    def zipComp(px: PixelType)(comp: Int)(c: Int) : PixelType
    def newDataModel(size: Int) : Array[PixelType]
}

trait IntColorModel extends ColorModel {
    type PixelType = Int
    def newDataModel(size: Int) = Array.ofDim[Int](size)
}

trait ARGBColorModel extends IntColorModel {
    val n_comp = 4
    def getComp(comp: Int, pixel: PixelType): Int = (pixel >> (24 - comp * 8)) & 0xff
    def pack(comps: Array[Int]): PixelType =
        comps(0) << 24 | comps(1) << 16 | comps(2) << 8 | comps(3)
    def unpack(pixel: PixelType, out: Array[Int] = Array.ofDim[Int](n_comp)) : Array[Int] = {
        out(0) = (pixel >> 24) & 0xff
        out(1) = (pixel >> 16) & 0xff
        out(2) = (pixel >> 8) & 0xff
        out(3) = pixel & 0xff
        out
    }
    def toARGB(px: PixelType): Int = px
    def fromARGB(c: Int): PixelType = c
    def toColor(pixel: PixelType) = Color(toARGB(pixel))
    def fromColor(c: Color) = fromARGB(c.toRGB.argb)
    def zipComp(px: PixelType)(comp: Int)(c: Int) = px | c << (24 - comp * 8)
}

trait RGBColorModel extends IntColorModel {
    val n_comp = 3
    def getComp(comp: Int, pixel: PixelType): Int = (pixel >> (16 - comp * 8)).toByte
    def pack(comps: Array[Int]): PixelType =
        comps(0) << 16 | comps(1) << 8 | comps(2)
    def unpack(pixel: PixelType, out: Array[Int] = Array.ofDim[Int](n_comp)) : Array[Int] = {
        out(0) = (pixel >> 16) & 0xff
        out(1) = (pixel >> 8) & 0xff
        out(2) = pixel & 0xff
        out
    }
    def toARGB(px: PixelType): Int = px | 0xff000000
    def fromARGB(c: Int): PixelType = c
    def toColor(pixel: PixelType) = Color(toARGB(pixel))
    def fromColor(c: Color) = fromARGB(c.toRGB.argb)
    def zipComp(px: PixelType)(comp: Int)(c: Int) = px | c << (16 - comp * 8)
}

trait ByteColorModel extends ColorModel {
    type PixelType = Byte
    val n_comp = 1
    def toInt(b: Byte) = if(b < 0) 256 + b else b
    def getComp(comp: Int, pixel: PixelType): Int = toInt(pixel)
    def pack(comps: Array[Int]): PixelType = comps(0).toByte
    def unpack(pixel: PixelType, out: Array[Int] = Array.ofDim[Int](n_comp)) : Array[Int] = {
        out(0) = toInt(pixel)
        out
    }
    def zipComp(pixel: PixelType)(comp: Int)(c: Int) = (pixel | c).toByte
    def newDataModel(size: Int) = Array.ofDim[PixelType](size)
}

trait GreyColorModel extends ByteColorModel {
    def toARGB(pixel: PixelType): Int = toInt(pixel) * 0x00010101 | 0xff000000
    def fromARGB(c: Int): PixelType = fromColor(Color(c))
    def toColor(pixel: PixelType) = Color(toARGB(pixel))
    def fromColor(c: Color) = {
        val rgb = c.toRGB
        (rgb.red * 0.2989f + rgb.green * 0.5870f + rgb.blue * 0.1140f).toInt.toByte
    }
}

// trait RGBLayerColorModel(layer: Int) extends ByteColorModel {
//     def toARGB(pixel: PixelType): Int = pixel << (16 - layer * 8) | 0xff000000
//     def fromARGB(c: Int): PixelType = (c >> (16 - layer * 8)).toByte
//     def toColor(pixel: PixelType) = Color(toARGB(pixel))
//     def fromColor(c: Color) = fromARGB(c.toRGB.argb)
// }
