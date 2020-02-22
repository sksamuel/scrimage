import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object AntiClockWise{

	/**
	    * Method to rotate image 90 degree AntiClockWise
	    * @param Image, @param typeImage
	    * @return
    	*/


	def rotateAnticlockwise(image : BufferedImage, image_type : String) : BufferedImage = {
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
				rotated_image.setRGB(i, height - 1 - j, pixel)
			}
		}
		rotated_image
	}

}
