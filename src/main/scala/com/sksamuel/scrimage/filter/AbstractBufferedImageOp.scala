package com.sksamuel.scrimage.filter

import java.awt.image.{ColorModel, BufferedImage, BufferedImageOp}
import java.awt.geom.{Point2D, Rectangle2D}
import java.awt.{RenderingHints, Rectangle}

/**
 *
 * @author Stephen Samuel
 *
 *         Copyright 2013 Stephen Samuel
 *         Modified from original Java code copyright 2006 Jerry Huxtable
 *
 *         Licensed under the Apache License, Version 2.0 (the "License");
 *         you may not use this file except in compliance with the License.
 *         You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *         See the License for the specific language governing permissions and
 *         limitations under the License.
 *
 */
abstract class AbstractBufferedImageOp extends BufferedImageOp with Cloneable {

    def createCompatibleDestImage(src: BufferedImage, dstCM: ColorModel): BufferedImage = {
        // todo if (dstCM == null) dstCM = src.getColorModel
        new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth, src.getHeight), dstCM.isAlphaPremultiplied, null)
    }

    def getBounds2D(src: BufferedImage): Rectangle2D = {
        new Rectangle(0, 0, src.getWidth, src.getHeight)
    }

    def getPoint2D(srcPt: Point2D, dstPt: Point2D): Point2D = {
        // todo   if (dstPt == null) dstPt = new Point2D.Double
        dstPt.setLocation(srcPt.getX, srcPt.getY)
        dstPt
    }

    def getRenderingHints: RenderingHints = {
        null
    }

    /**
     * A convenience method for getting ARGB pixels from an image. This tries to avoid the performance
     * penalty of BufferedImage.getRGB unmanaging the image.
     *
     * @param image  a BufferedImage object
     * @param x      the left edge of the pixel block
     * @param y      the right edge of the pixel block
     * @param width  the width of the pixel arry
     * @param height the height of the pixel arry
     * @param pixels the array to hold the returned pixels. May be null.
     * @return the pixels
     * @see #setRGB
     */
    def getRGB(image: BufferedImage, x: Int, y: Int, width: Int, height: Int, pixels: Array[Int]): Array[Int] = {
        val `type`: Int = image.getType
        if (`type` == BufferedImage.TYPE_INT_ARGB || `type` == BufferedImage.TYPE_INT_RGB) return image
          .getRaster
          .getDataElements(x, y, width, height, pixels)
          .asInstanceOf[Array[Int]]
        image.getRGB(x, y, width, height, pixels, 0, width)
    }
    /**
     * A convenience method for setting ARGB pixels in an image. This tries to avoid the performance
     * penalty of BufferedImage.setRGB unmanaging the image.
     *
     * @param image  a BufferedImage object
     * @param x      the left edge of the pixel block
     * @param y      the right edge of the pixel block
     * @param width  the width of the pixel arry
     * @param height the height of the pixel arry
     * @param pixels the array of pixels to set
     * @see #getRGB
     */
    def setRGB(image: BufferedImage, x: Int, y: Int, width: Int, height: Int, pixels: Array[Int]) {
        val `type`: Int = image.getType
        if (`type` == BufferedImage.TYPE_INT_ARGB || `type` == BufferedImage.TYPE_INT_RGB) image
          .getRaster
          .setDataElements(x, y, width, height, pixels)
        else image.setRGB(x, y, width, height, pixels, 0, width)
    }
}
