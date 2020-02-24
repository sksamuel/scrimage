import java.awt.image.BufferedImage

object Erosion{

  /**
	    * Method to Erode an Image it will remove all irrelevant pixel from the image
	    * @return
      *structuring element tells in which shape you want to shrink the pixel
      *Median filter is used in this algorithm
    	*/


	def max_occurance(list : List[Int]) : Int = {
        	list.sorted(Ordering.Int.reverse)
		var len : Int = list.length
		var max : Int = 0
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
			if (max < count) {
			    pixel = list(index2)
			    max = count
			}
			index = index + 1
			count  = 1
		}
		return pixel
	}

	def erosion(image : BufferedImage, struct : Array[Array[Int]], image_type : String, median_value : Int) : BufferedImage = {
		var height : Int = image.getHeight();
		var width : Int = image.getWidth();
		var erosion_image = image
		if (image_type == "png") {
			var image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
			erosion_image = image1
		 } else {
			var image1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
			erosion_image = image1
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
					pixel = max_occurance(list)
				} else {
					pixel = image.getRGB(j, i)
				}
				erosion_image.setRGB(j, i, pixel)
			}
		}
		return erosion_image
	}
}
