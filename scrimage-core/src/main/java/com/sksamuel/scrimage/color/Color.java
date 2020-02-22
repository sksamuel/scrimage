package com.sksamuel.scrimage.color;

import com.sksamuel.scrimage.Pixel;

import java.awt.*;

interface Color {

    // by setting other colours to 255, then this will default to white if transparency is not available on the image.
    RGBColor Transparent = new RGBColor(255, 255, 255, 0);

    /**
     * Returns a conversion of this Color into an RGBColor.
     * If this colour is already an instance of RGBColor then the same instance will be returned.
     */
    RGBColor toRGB();

    /**
     * Returns a conversion of this color into a CYMK color.
     * If this colour is already a CYMK then the same instance will be returned.
     */
    default CMYKColor toCMYK() {
        return toRGB().toCMYK();
    }

    default HSVColor toHSV() {
        return toRGB().toHSV();
    }

    default HSLColor toHSL() {
        return toRGB().toHSL();
    }

    default Grayscale toGrayscale() {
        RGBColor rgb = toRGB();
        double red = 0.2126 * rgb.red;
        double green = 0.7152 * rgb.green;
        double blue = 0.0722 * rgb.blue;
        double gray = red + green + blue;
        return new Grayscale((int) Math.round(gray), rgb.alpha);
    }

    /**
     * Returns a HEX String of this colour. Eg for 0,255,0, this method will return 00FF00.
     */
    default String toHex() {
        return Integer.toHexString(toRGB().toInt() & 0xffffff).toUpperCase();
    }

    /**
     * Returns this instance as a java.awt.Color.
     * AWT Colors use the RGB color model.
     */
    default java.awt.Color toAWT() {
        RGBColor rgb = toRGB();
        return new java.awt.Color(rgb.red, rgb.green, rgb.blue, rgb.alpha)
    }

    default Pixel toPixel() {
        RGBColor rgb = toRGB();
        return new Pixel(rgb.red, rgb.green, rgb.blue, rgb.alpha);
    }

    /**
     * Returns this colour as an AWT Paint.
     */
    default Paint paint() {
        return new java.awt.Color(toRGB().toInt());
    }
}

//class IntConvert(val i:Int)extends AnyVal{
//        def toColor:RGBColor=apply(i)
//        }
//        implicit
//
//class ColorConvert(val c:Color)extends AnyVal{
//        def toAWT:java.awt.Color=new java.awt.Color(c.toRGB.toInt)
//        }
//        implicit
//
//class AwtColorConvert(val awt:java.awt.Color)extends AnyVal{
//        def toColor:RGBColor=RGBColor(awt.getRed,awt.getGreen,awt.getBlue,awt.getAlpha)
//        }
//
//        def apply(red:Int,green:Int,blue:Int,alpha:Int=255):RGBColor=RGBColor(red,green,blue,alpha)
//        def apply(argb:Int):RGBColor={
//        val alpha=(argb>>24)&0xFF
//        val red=(argb>>16)&0xFF
//        val green=(argb>>8)&0xFF
//        val blue=argb&0xFF
//        RGBColor(red,green,blue,alpha)
//        }
//
//        val White=RGBColor(255,255,255)
//        val Black=RGBColor(0,0,0)
//        }
//
//
//        case
//

