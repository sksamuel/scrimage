import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object FlipLeftRight{

	/**
	    * Method to flip an image left-right
	    * @param Image, @param typeImage
	    * @return
    	*/
    
    def flipLeftRight(image : BufferedImage, imageType : String) : BufferedImage = {
		var pixel : Int = 0
		var height : Int = image.getHeight()
		var width : Int = image.getWidth()
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
				flipImage.setRGB(width - 1 - j, i, pixel)
			}
		} 
		flipImage
	}
}
