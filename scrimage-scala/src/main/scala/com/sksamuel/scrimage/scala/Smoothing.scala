import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object Smoothing{

	/**
	    * Method to smoothen an image
	    * smoothening is done using median filter
	    * @param Image, @param image_type
	    * @return
    	*/	

	def smoothing(image : BufferedImage, image_type : String) : BufferedImage = {
	        var height : Int = image.getHeight();
		var width : Int = image.getWidth();
		var smooth_image = image
		if (image_type == "png") {
			var temp_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
			smooth_image = temp_image		
		} else {
			var temp_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
			smooth_image = temp_image
		}
		var pixel : Int = 0
		var median_value : Int = 2
		for (i <- 0 to height - 1) {
			for (j <- 0 to width - 1) {
				var list : List[Int] = List()
		    		if ((i - median_value >= 0) && (i + median_value < height) && (j - median_value >= 0) && (j + median_value < width)) {
					for (k <- i - median_value to i + median_value) {
					 	for (l <- j - median_value to j + median_value) {	
							pixel = image.getRGB(l,k)					
							list = pixel +: list
						}
					}
					list.sorted(Ordering.Int)
					var value  : Int = list((list.length)/2)
					for (k <- i - median_value to i + median_value) {
					 	for (l <- j - median_value to j + median_value) {
							smooth_image.setRGB(l, k, value)
						}
					}
				} else {
					pixel = image.getRGB(j, i)
					smooth_image.setRGB(j, i, pixel)
				}		 

			}
		} 
		return smooth_image
	}
}
