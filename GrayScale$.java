package com.sksamuel.scrimage.scala;

object GrayScale{

	/**
	    * Method to convert Coloured of an GrayScale image
	    * @param Image, @param typeImage
	    * @return
    	*/


	def grayScale(image : BufferedImage, imageType : String) : BufferedImage = {
	     var height : Int = image.getHeight()
	     var width : Int = image.getWidth()
	     var pixel : Int = 0
	     var i : Int = 0
	     var j :Int = 0
	     for (i <- 0 until height) {
	          for (j <- 0 until width) {
			pixel = image.getRGB(j, i);
			pixel = grayPixel(image, pixel, imageType)
			image.setRGB(j, i, pixel)
		   }
		}
		image
	}

	def grayPixel(image : BufferedImage, pixel : Int, imageType : String) : Int = {
		var red : Int = (pixel >> 16) & 0xff
    		var green : Int = (pixel >> 8) & 0xff
    	        var blue : Int = (pixel) & 0xff
		var avg : Int = ((0.2989 * red + 0.5870 * green + 0.1140 * blue).asInstanceOf[Int])
		var gray : Int = 0
		if (imageType == "png") {
		     var alpha : Int = (pixel >> 24) & 0xff
		     gray = (alpha << 24) | (avg << 16) | (avg << 8) | avg;
		} else {
		     gray = (avg << 16) | (avg << 8) | avg;
		}
		gray
	}

}
