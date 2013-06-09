package com.sksamuel.scrimage

import com.sksamuel.scrimage.Position.Center
import java.awt.Graphics2D

/** @author Stephen Samuel */
class AsyncImage(image: Image) {

    /**
     *
     * Resize will resize the canvas, it will not scale the image.
     * This is like a "canvas resize" in Photoshop.
     *
     * If the dimensions are smaller than the current canvas size
     * then the image will be cropped.
     *
     * @param targetWidth the target width
     * @param targetHeight the target height
     * @param position where to position the original image after the canvas size change
     *
     * @return a new Image that is the result of resizing the canvas.
     */
    def resizeTo(targetWidth: Int, targetHeight: Int, position: Position = Center): Image = {
        val target = Image.empty(targetWidth, targetHeight)
        val g2 = target.awt.getGraphics.asInstanceOf[Graphics2D]
        g2.drawImage(image.awt, 0, 0, null)
        g2.dispose()
        target
    }
}
