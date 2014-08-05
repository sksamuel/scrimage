package com.sksamuel.scrimage

trait ColorModel {
    type PixelType
    def n_comp: Int
    def getComp(comp: Int, pixel: PixelType) : Byte
    def unpack(pixel: PixelType, out: Array[Byte] = Array.ofDim[Byte](n_comp)) : Array[Byte]
    def pack(comps :Array[Byte]) : PixelType
    def toARGB(pixel: PixelType): Int
    def fromARGB(c: Int): PixelType
    def toColor(pixel: PixelType): Color
    def fromColor(color: Color): PixelType
    def foldComp(px: PixelType, comp: Int, c: Byte) : PixelType
    def newDataModel(size: Int) : Array[PixelType]
}

trait IntColorModel extends ColorModel {
    type PixelType = Int
    def newDataModel(size: Int) = Array.ofDim[Int](size)
}

trait ARGBColorModel extends IntColorModel {
    val n_comp = 4
    def getComp(comp: Int, pixel: PixelType): Byte = (pixel >> (24 - comp * 8)).toByte
    def pack(comps: Array[Byte]): PixelType =
        (comps(0) & 0xff) << 24 | (comps(1) & 0xff) << 16 | (comps(2) & 0xff) << 8 | (comps(3) & 0xff)
    def unpack(pixel: PixelType, out: Array[Byte] = Array.ofDim[Byte](n_comp)) : Array[Byte] = {
        out(0) = (pixel >> 24).toByte
        out(1) = (pixel >> 16).toByte
        out(2) = (pixel >> 8).toByte
        out(3) = pixel.toByte
        out
    }
    def toARGB(px: PixelType): Int = px
    def fromARGB(c: Int): PixelType = c
    def toColor(pixel: PixelType) = Color(toARGB(pixel))
    def fromColor(c: Color) = fromARGB(c.toRGB.argb)
    def foldComp(px: PixelType, comp: Int, c: Byte) = px | (c & 0xff) << (24 - comp * 8)
}

trait RGBColorModel extends IntColorModel {
    val n_comp = 3
    def getComp(comp: Int, pixel: PixelType): Byte = (pixel >> (16 - comp * 8)).toByte
    def pack(comps: Array[Byte]): PixelType =
        (comps(0) & 0xff) << 16 | (comps(1) & 0xff) << 8 | (comps(2) & 0xff)
    def unpack(pixel: PixelType, out: Array[Byte] = Array.ofDim[Byte](n_comp)) : Array[Byte] = {
        out(0) = (pixel >> 16).toByte
        out(1) = (pixel >> 8).toByte
        out(2) = pixel.toByte
        out
    }
    def toARGB(px: PixelType): Int = px | 0xff000000
    def fromARGB(c: Int): PixelType = c
    def toColor(pixel: PixelType) = Color(toARGB(pixel))
    def fromColor(c: Color) = fromARGB(c.toRGB.argb)
    def foldComp(px: PixelType, comp: Int, c: Byte) = px | (c & 0xff) << (16 - comp * 8)
}

trait ByteColorModel extends ColorModel {
    type PixelType = Byte
    val n_comp = 1
    def getComp(comp: Int, pixel: PixelType): Byte = pixel
    def pack(comps: Array[Byte]): PixelType = comps(0)
    def unpack(pixel: PixelType, out: Array[Byte] = Array.ofDim[Byte](n_comp)) : Array[Byte] = {
        out(0) = pixel
        out
    }
    def foldComp(pixel: PixelType)(comp: Int)(c: Byte) = c
    def newDataModel(size: Int) = Array.ofDim[PixelType](size)
}

trait GreyColorModel extends ByteColorModel {
    def toARGB(pixel: PixelType): Int = (pixel & 0xff) * 0x00010101 | 0xff000000
    def fromARGB(c: Int): PixelType = fromColor(Color(c))
    def toColor(pixel: PixelType) = Color(toARGB(pixel))
    def fromColor(c: Color) = {
        val rgb = c.toRGB
        (rgb.red * 0.2989f + rgb.green * 0.5870f + rgb.blue * 0.1140f).toByte
    }
}

// trait RGBLayerColorModel(layer: Int) extends ByteColorModel {
//     def toARGB(pixel: PixelType): Int = pixel << (16 - layer * 8) | 0xff000000
//     def fromARGB(c: Int): PixelType = (c >> (16 - layer * 8)).toByte
//     def toColor(pixel: PixelType) = Color(toARGB(pixel))
//     def fromColor(c: Color) = fromARGB(c.toRGB.argb)
// }
