package com.sksamuel.scrimage

import java.awt.image.BufferedImage
import org.apache.commons.io.FilenameUtils
import java.net.URLConnection
import java.awt.{Image => AWTImage}
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

/** @author Stephen Samuel */
object ImageTools {

    val BG_COLOR = java.awt.Color.WHITE
    val SCALING_METHOD = java.awt.Image.SCALE_AREA_AVERAGING

    // write out the image to bytes
    def toBytes(image: BufferedImage, format: String) = {
        require(format != null)
        val output = new ByteArrayOutputStream
        ImageIO.write(image, format, output)
        output.toByteArray
    }

    def contentType(filename: String) = {
        val ext = FilenameUtils.getExtension(filename)
        ext match {
            case "gif" => "image/gif"
            case "jpg" => "image/jpg"
            case "jpeg" => "image/jpeg"
            case "png" => "image/png"
            case _ => Option(URLConnection.guessContentTypeFromName(filename)).getOrElse("application/octet-stream")
        }
    }

    /**
     * Scales the given image to fit the target dimensions while keeping the current aspect ratio.
     */
    def fit(source: AWTImage, size: (Int, Int)): BufferedImage = {
        val sizePrime = normalizeSize(source, size)
        val fitted = dimensionsToFit(sizePrime, (source.getWidth(null), source.getHeight(null)))
        val scaled = source.getScaledInstance(fitted._1, fitted._2, SCALING_METHOD)
        val offset = ((sizePrime._1 - fitted._1) / 2, (sizePrime._2 - fitted._2) / 2)
        _draw(sizePrime, offset, scaled)
    }

    def normalizeSize(source: AWTImage, size: (Int, Int)) = size match {
        case (0, 0) => (0, 0)
        case (0, h) => ((ratio(source) * h).toInt, h)
        case (w, 0) => (w, (w.toDouble / ratio(source)).toInt)
        case (w, h) => (w, h)
    }

    private def _draw(size: (Int, Int), offset: (Int, Int), image: AWTImage) = {
        val target = new BufferedImage(size._1, size._2, BufferedImage.TYPE_INT_RGB)
        val g = target.createGraphics
        g.setColor(BG_COLOR)
        g.fillRect(0, 0, size._1, size._2)
        g.drawImage(image, offset._1, offset._2, null)
        target
    }

    /**
     * Resizes the given image into the new target dimensions.
     */
    def resize(source: AWTImage, size: (Int, Int)): BufferedImage = {
        val sizePrime = normalizeSize(source, size)
        val scaled = source.getScaledInstance(sizePrime._1, sizePrime._2, SCALING_METHOD)
        _draw(sizePrime, (0, 0), scaled)
    }

    def ratio(source: AWTImage): Double = ratio(source.getWidth(null), source.getHeight(null))
    def ratio(width: Int, height: Int): Double = if (height == 0) 0 else width / height.toDouble

    def dimensionsToCover(target: (Int, Int), source: (Int, Int)): (Int, Int) = {

        val minWidth = if (target._1 < source._1) source._1 else target._1
        val minHeight = if (target._2 < source._2) source._2 else target._2

        val wscale = minWidth / source._1.toDouble
        val hscale = minHeight / source._2.toDouble

        if (wscale < hscale)
            ((source._1 * hscale).toInt, (source._2 * hscale).toInt)
        else
            ((source._1 * wscale).toInt, (source._2 * wscale).toInt)
    }

    /**
     * Returns width and height that allow the given source width, height to fit inside the target width, height
     * without losing aspect ratio
     */
    def dimensionsToFit(target: (Int, Int), source: (Int, Int)): (Int, Int) = {

        // if target width/height is zero then we have no preference for that, so set it to the original value,
        // since it cannot be any larger
        val maxWidth = if (target._1 == 0) source._1 else target._1
        val maxHeight = if (target._2 == 0) source._2 else target._2

        val wscale = maxWidth / source._1.toDouble
        val hscale = maxHeight / source._2.toDouble

        if (wscale < hscale)
            ((source._1 * wscale).toInt, (source._2 * wscale).toInt)
        else
            ((source._1 * hscale).toInt, (source._2 * hscale).toInt)
    }
}
