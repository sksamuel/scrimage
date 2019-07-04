import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object PixelValue{

	/**
	    * Method to print all channels of an image 
	    * @param Image, @param typeImage
	    * Print Alpha, Red, Green, Blue value for png Images
	    * Print Red, Green, Blue value for jpg Images
	    * @return
    	*/	

	def printARGB(pixel : Int) {
	    var alpha : Int = (pixel >> 24) & 0xff
	    var red : Int = (pixel >> 16) & 0xff
    	    var green : Int = (pixel >> 8) & 0xff
    	    var blue : Int = (pixel) & 0xff
    	    println("argb: " + alpha + ", " + red + ", " + green + ", " + blue)
  	}
  	
  	def printRGB(pixel : Int) {
  	    var red : Int = (pixel >> 16) & 0xff 
  	    var green : Int = (pixel >> 8) & 0xff
  	    var blue : Int = (pixel) & 0xff
  	    println("rgb: " + red + ", " + green + ", " + blue)
  	}

  	def traverseImage(image : BufferedImage, imageType : String) {
      	    var width : Int = image.getWidth();
    	    var height : Int = image.getHeight();
    	    for (i <- 0 to height - 1) {
      		for (j <- 0 to width - 1) {
        		println("x, y : " + j + ", " + i);
        		var pixel : Int = image.getRGB(j, i);
        		if (imageType == "png") {
        		    printARGB(pixel)
        		} else {
        		    printRGB(pixel)
        		}
        		println("")
      		}
    	     }
  	}   
}
