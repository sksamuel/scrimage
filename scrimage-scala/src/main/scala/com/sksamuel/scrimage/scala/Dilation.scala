import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object Dilation{
	
  /**
	    * Method to dilate an Image it will expand the pixels in the image it will fill all the missing pixel in the image
	    * @param Image,@param structuring_element @param typeImage @param size_of_median_filter 
	    * @return
      *structuring element tells in which shape you want to expand the pixel
      *Median filter is used in this algorithm
    	*/
	

	def min_occurance(list : List[Int]) : Int = {
        	list.sorted(Ordering.Int)
		var len : Int = list.length
		var min : Int = len * 2
		var count : Int = 1
		var index : Int  = 0
		var pixel : Int = 0
		while ( index < len) {
			var index2 : Int = index
			while (index2 + 1 < len && list(index) == list(index2 + 1)) {
				count = count + 1	
				index2 = index2 + 1
				index = index2			
			} 
			if (min > count) {	
			    pixel = list(index2)
			    min = count
			} 
			index = index + 1
			count  = 1
		}
		return pixel
	}
    
	def dilation(image : BufferedImage, struct : Array[Array[Int]], image_type : String, median_value : Int) : BufferedImage = {
		var height : Int = image.getHeight();
		var width : Int = image.getWidth();
		var dilation_image = image
		if (image_type == "png") {
			var image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
			dilation_image = image1			
		 } else {
			var image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
			dilation_image = image1
		}
		var pixel : Int = 0
		var i : Int = 0
		var col : Int = 0
		var row : Int = 0
		for (i <- 0 to height - 1) {
			for (j <- 0 to width - 1) {
				var list : List[Int] = List()
				var count : Int = 0
		    		if ((i - median_value >= 0) && (i + median_value < height) && (j - median_value >= 0) && (j + median_value < width)) {		
					row = 0
					col = 0
					for (k <- i - median_value to i + median_value) {
					 	for (l <- j - median_value to j + median_value) {
							pixel = image.getRGB(l, k) * struct(col)(row)
							list = pixel +: list
							col = col + 1 	
						}
						row = row + 1
						col = 0
					}
					pixel = min_occurance(list)
				} else {
					pixel = image.getRGB(j, i)	
				}	 
				dilation_image.setRGB(j, i, pixel)
			}
		} 
		return dilation_image
	}
}
