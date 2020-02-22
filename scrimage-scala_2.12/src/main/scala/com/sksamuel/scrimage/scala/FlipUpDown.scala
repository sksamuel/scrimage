import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object FlipUpDown{

	/**
	    * Method to flip an image up-down
	    * @param Image, @param typeImage
	    * @return
    	*/


	def flipUpDown(image : BufferedImage, imageType : String) : BufferedImage = {
		var pixel : Int = 0
		var height : Int = image.getHeight();
		var width : Int = image.getWidth();
		pixel = image.getRGB(0, 0) 
		var flipImage = image
		if (imageType == "png") {
			var image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
			flipImage = image1			
		 } else {
			var image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
			flipImage = image1
		}
		
		for (i <- 0 to height - 1) {
			for (j <- 0 to width - 1) {
				pixel = image.getRGB(j, i)	
				flipImage.setRGB(j, height - 1 - i, pixel)
			}
		}
		flipImage
	}

}
