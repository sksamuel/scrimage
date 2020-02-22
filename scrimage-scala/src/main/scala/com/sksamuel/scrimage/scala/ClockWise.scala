import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object ClockWise{

    /**
	    * Method to Rotate an image 90 degree clockwise
	    * @param Image, @param typeImage
	    * @return
    	*/
	

	def rotateClockwise(image : BufferedImage, image_type : String) : BufferedImage = {
		var width : Int = image.getWidth()
    		var height : Int = image.getHeight()
		var rotated_image = image
		if (image_type == "png") {
			var temp_image = new BufferedImage(height, width, BufferedImage.TYPE_INT_ARGB)
			rotated_image = temp_image
		} else {
			var temp_image = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB)
			rotated_image = temp_image
		}
		for (i <- 0 to height - 1) {
			for (j <- 0 to width - 1) {
				var pixel : Int = image.getRGB(j, i)
				rotated_image.setRGB(width - 1 - i, j, pixel)
			}
		}
		rotated_image
	}

}
