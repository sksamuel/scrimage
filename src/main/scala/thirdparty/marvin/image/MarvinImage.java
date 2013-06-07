/**
Marvin Project <2007-2009>

Initial version by:

Danilo Rosetto Munoz
Fabio Andrijauskas
Gabriel Ambrosio Archanjo

site: http://marvinproject.sourceforge.net

GPL
Copyright (C) <2007>  

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package thirdparty.marvin.image;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Image object with many operations. This class is the image
 * representation used for the other classes in the framework.
 *
 * @version 1.0 02/13/08
 * @author Danilo Roseto Munoz
 * @author Fabio Andrijaukas
 * @author Gabriel Ambrosio Archanjo
 */
public class MarvinImage implements Cloneable {
	
	public final static int COLOR_MODEL_RGB 	= 0;
	public final static int COLOR_MODEL_BINARY 	= 1;
	
	// Definitions
	public final static int PROPORTIONAL = 0;	

	// Image
	protected BufferedImage image;
	
	// Array Color
	protected int[] arrIntColor;
	protected boolean[] arrBinaryColor;
	
	// Colors
	protected int rgb, r, b, g;
	protected Color color;

	// Color Model
	protected int colorModel;
	
	// Format
	protected String formatName;
	
	// Components
	protected int numComponents;
	
	// Dimension
	int width;
	int height;
	
	/**
	 * Constructor using a image in memory
	 * @param img Image
	 */
	public MarvinImage(BufferedImage img){		
		this.image =  img;
		formatName = "jpg";
		width = img.getWidth();
		height = img.getHeight();
		colorModel = COLOR_MODEL_RGB;
		updateColorArray();
	}
	
	/**
	 * Constructor using a image in memory
	 * @param img Image
	 * @param fmtName Image format name
	 */
	public MarvinImage(BufferedImage img, String fmtName){
		this.image =  img;
		formatName = fmtName;		
		width = img.getWidth();
		height = img.getHeight();
		colorModel = COLOR_MODEL_RGB;
		updateColorArray();
	}

	/**
	 * Constructor to blank image, passing the size of image
	 * @param int width
	 * @param int height
	 */
	public MarvinImage(int w, int h){
		colorModel = COLOR_MODEL_RGB;
		formatName = "jpg";
		setDimension(w, h);
	}
	
	public MarvinImage(int w, int h, int cm){
		colorModel = cm;
		formatName = "jpg";
		setDimension(w, h);
	}
	
	
	public int getComponents(){
		return numComponents;
	}
	
	public MarvinImage crop(int x, int y, int w, int h){		 
		return(new MarvinImage(image.getSubimage(x, y, w, h)));
	}
	
	public void updateColorArray(){
		arrIntColor = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
//		arrIntColor = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}
	
	public void update(){
		int w = image.getWidth();
		switch(colorModel){
			case COLOR_MODEL_RGB:
				image.setRGB(0, 0, image.getWidth(), image.getHeight(), arrIntColor,0,w);
				break;
			case COLOR_MODEL_BINARY:
				image.setRGB(0, 0, image.getWidth(), image.getHeight(), MarvinColorModelConverter.binaryToRgb(arrBinaryColor),0,w);
				break;
		}
	}
	
	public void clearImage(int color){
		for(int y=0; y<height; y++){
			for(int x=0; x<width; x++){
				setIntColor(x,y,color);
			}
		}
	}
	
	/**
	 * Gets the type
	 */
	public int getType(){		
		return image.getType();
	}
	
	public int getColorModel(){
		return colorModel;
	}
	
	public void setColorModel(int cm){
		colorModel = cm;
		allocColorArray();
	}
	
	//@todo remove ambiguity between Type and FormatName
	/*
	 * @return image format name
	 */
	public String getFormatName(){
		return formatName;
	}
	
	public void setDimension(int w, int h){
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		width = w;
		height = h;
		
		allocColorArray();
				
	}
	
	public void allocColorArray(){
		switch(colorModel){
			case COLOR_MODEL_RGB:
				arrBinaryColor = null;
				arrIntColor = new int[width*height];
				break;
			case COLOR_MODEL_BINARY:
				arrIntColor = null;
				arrBinaryColor = new boolean[width*height];
				break;
		}
	}

	/**
	 * @return integer color array for the entire image.
	 */
	public int[] getIntColorArray(){
		return arrIntColor;
	}
	
	/**
	 *	Set the integer color array for the entire image.
	 **/
	public void setIntColorArray(int[] arr){
		arrIntColor = arr;
	}
	
	public static void copyColorArray(MarvinImage imgSource, MarvinImage imgDestine){
		
		if(imgSource.getColorModel() != imgDestine.getColorModel()){
			throw new RuntimeException("copyColorArray(): Incompatible Images Color Model");
		}
		
		switch(imgSource.getColorModel()){
			case COLOR_MODEL_RGB:
				copyIntColorArray(imgSource, imgDestine);
				break;
			case COLOR_MODEL_BINARY:
				copyBinaryColorArray(imgSource, imgDestine);
				break;
		}
	}
	
	protected static void copyIntColorArray(MarvinImage imgSource, MarvinImage imgDestine){
		System.arraycopy(imgSource.getIntColorArray(), 0, imgDestine.getIntColorArray(), 0, imgSource.getWidth()*imgSource.getHeight());
	}
	
	protected static void copyBinaryColorArray(MarvinImage imgSource, MarvinImage imgDestine){
		System.arraycopy(imgSource.getBinaryColorArray(), 0, imgDestine.getBinaryColorArray(), 0, imgSource.getWidth()*imgSource.getHeight());
	}
	
	public boolean[] getBinaryColorArray(){
		return arrBinaryColor;
	}
	
	public boolean getBinaryColor(int x, int y){
		return arrBinaryColor[y*width+x];
	}
	
	public void setBinaryColor(int x, int y, boolean value){
		arrBinaryColor[y*width+x] = value;
	}
	
	/**
	 * Gets the integer color composition for x, y position
	 * @param int		x 
	 * @param int		y
	 * @return int		color
	 */
	public int getIntColor(int x, int y){
		return arrIntColor[y*width+x];
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @return alpha component
	 */
	public int getAlphaComponent(int x, int y){
		return (arrIntColor[((y*width+x))]& 0xFF000000) >>> 24;
	}
	
	/**
	 * Gets the integer color component 0  in the x and y position
	 * @param int		x 
	 * @param int		y
	 * @return int		color component 0
	 */	
	public int getIntComponent0(int x, int y){
		return (arrIntColor[((y*width+x))]& 0x00FF0000) >>> 16;
	}

	/**
	 * Gets the integer color component 1 in the x and y position
	 * @param int	x 
	 * @param int	y
	 * @return int color component 1
	 */	
	public int getIntComponent1(int x, int y){
		return (arrIntColor[((y*width+x))]& 0x0000FF00) >>> 8;
	}

	/**
	 * Gets the integer color component 2 in the x and y position
	 * @param int	x 
	 * @param int	y
	 * @return int blue color
	 */	
	public int getIntComponent2(int x, int y){
		return (arrIntColor[((y*width+x))] & 0x000000FF);
	}

	/**
	 * Returns the width 
	 * @return int	width
	 */
	public int getWidth(){
		return(image.getWidth());  	
	}

	/**
	 * Returns the height 
	 * @return int	height
	 */
	public int getHeight(){
		return(image.getHeight());  
	}
	
	public boolean isValidPosition(int x, int y){
		if(x >= 0 && x < image.getWidth() && y >= 0 && y < getHeight()){
			return true;
		}
		return false;
	}

	public void setIntColor(int x, int y, int alpha, int color){
		arrIntColor[((y*image.getWidth()+x))] = (alpha << 24) + color;
	}
	
	/**
	 * Sets the integer color composition in X an Y position
	 * @param x 		position
	 * @param y 		position
	 * @param color 	color value
	 */
	public void setIntColor(int x, int y, int color){
		arrIntColor[((y*image.getWidth()+x))] = color;
	}

	/**
	 * Sets the integer color in X an Y position
	 * @param x 	position
	 * @param y 	position
	 * @param c0	component 0
	 * @param c1 	component 1
	 * @param c2 	component 2
	 */
	public void setIntColor(int x, int y, int c0, int c1, int c2){
		int alpha = (arrIntColor[((y*width+x))]& 0xFF000000) >>> 24;
		setIntColor(x,y,alpha,c0,c1,c2);
	}
	
	/**
	 * Sets the integer color in X an Y position
	 * @param x 	position
	 * @param y 	position
	 * @param c0	component 0
	 * @param c1 	component 1
	 * @param c2 	component 2
	 */
	public void setIntColor(int x, int y, int alpha, int c0, int c1, int c2){
		arrIntColor[((y*image.getWidth()+x))] = (alpha << 24)+
		(c0 << 16)+
		(c1 << 8)+
		c2;
	}

	/**
	 * Sets a new image 
	 * @param BufferedImage imagem
	 */
	public void setBufferedImage(BufferedImage img){		
		image = img;
		width = img.getWidth();
		height = img.getHeight();
		updateColorArray();		
	}

	/**
	 * @returns a BufferedImage associated with the MarvinImage 
	 */
	public BufferedImage getBufferedImage(){
		return image;
	}
	
	public BufferedImage getBufferedImageNoAlpha(){
		
		// Only for RGB images
		if(colorModel == COLOR_MODEL_RGB){
			int pixels = width*height;
			int[] pixelData = new int[pixels];
			for(int i=0; i<pixels; i++){
				pixelData[i] = arrIntColor[i] & 0x00FFFFFF;
			}
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			image.setRGB(0, 0, width, height, pixelData,0,width);
			return image;
		}
		return null;
	}
	
	/**
	 * Limits the color value between 0 and 255.
	 * @return int - the color value
	 */
	public int limit8bitsColor(int color){

		if(color > 255){
			color = 255;
			return(color);
		}

		if(color < 0){
			color = 0;
			return(color);
		}
		return color;
	}

	/**
	 * Convolution operator
	 * @return int[]
	 */
	public int[] Multi8p(int x, int y,int masc[][]){
		//a b c
		//d e f
		//g h i
		int aR = getIntComponent0(x-1,y-1);   int bR = getIntComponent0(x-1,y);  int cR = getIntComponent0(x-1,y+1);
		int aG = getIntComponent1(x-1,y-1);   int bG = getIntComponent1(x-1,y);  int cG = getIntComponent1(x-1,y+1);
		int aB = getIntComponent2(x-1,y-1);   int bB = getIntComponent2(x-1,y);  int cB = getIntComponent2(x-1,y+1);


		int dR = getIntComponent0(x,y-1);      int eR = getIntComponent0(x,y);   int fR = getIntComponent0(x,y+1);
		int dG = getIntComponent1(x,y-1);      int eG = getIntComponent1(x,y);   int fG = getIntComponent1(x,y+1);
		int dB = getIntComponent2(x,y-1);      int eB = getIntComponent2(x,y);   int fB = getIntComponent2(x,y+1);


		int gR = getIntComponent0(x+1,y-1);    int hR = getIntComponent0(x+1,y);   int iR = getIntComponent0(x+1,y+1);
		int gG = getIntComponent1(x+1,y-1);    int hG = getIntComponent1(x+1,y);   int iG = getIntComponent1(x+1,y+1);
		int gB = getIntComponent2(x+1,y-1);    int hB = getIntComponent2(x+1,y);   int iB = getIntComponent2(x+1,y+1);

		int rgb[] = new int[3];

		rgb[0] = ( (aR * masc[0][0]) + (bR * masc[0][1]) +(cR * masc[0][2])+
				(dR * masc[1][0]) + (eR * masc[1][1]) +(fR * masc[1][2])+
				(gR * masc[2][0]) + (hR * masc[2][1]) +(iR * masc[2][2]) ); 

		rgb[1] = ( (aG * masc[0][0]) + (bG * masc[0][1]) +(cG * masc[0][2])+
				(dG * masc[1][0]) + (eG * masc[1][1]) +(fG * masc[1][2])+
				(gG * masc[2][0]) + (hG * masc[2][1]) +(iG * masc[2][2]) ); 

		rgb[2] = ( (aB * masc[0][0]) + (bB * masc[0][1]) +(cB * masc[0][2])+
				(dB * masc[1][0]) + (eB * masc[1][1]) +(fB * masc[1][2])+
				(gB * masc[2][0]) + (hB * masc[2][1]) +(iB * masc[2][2]) ); 

		// return the value for all channel
		return(rgb);

	}

	/**
	 * Return a new instance of the BufferedImage
	 * @return BufferedImage
	 */
	public BufferedImage getNewImageInstance(){
		BufferedImage buf = new BufferedImage(image.getWidth(),image.getHeight(), image.getType());
		buf.setData(image.getData());
		return buf;
	}

	/**
	 * Resize and return the image passing the new height and width
	 * @param height
	 * @param width
	 * @return
	 */
	public BufferedImage getBufferedImage(int width, int height)
	{
		// using the new approach of Java 2D API 
		BufferedImage buf = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) buf.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(image,0,0,width,height,null);
		g2d.dispose();
		return(buf);
	}
	
	/**
	 * Resize and return the image passing the new height and width, but maintains width/height factor
	 * @param height
	 * @param width
	 * @return
	 */
	 public BufferedImage getBufferedImage(int width, int height, int type){
		 int	wDif, 
				hDif,				
				fWidth = 0, 
				fHeight = 0;

		 double	imgWidth,
				imgHeight;

		 double factor;
		 imgWidth = image.getWidth();
		 imgHeight = image.getHeight();
		

		 switch(type)
		 {
			 case PROPORTIONAL:
				 wDif = (int)imgWidth - width;
				 hDif = (int)imgHeight - height;
				 if(wDif > hDif){
					 factor = width/imgWidth;
				 }
				 else{
					 factor = height/imgHeight;
				 }
				 fWidth = (int)Math.floor(imgWidth*factor);
				 fHeight = (int)Math.floor(imgHeight*factor);
				 break;
		 }
		 return getBufferedImage(fWidth, fHeight);
	 }
	/**
	 * Resize the image passing the new height and width
	 * @param height
	 * @param width
	 * @return
	 */
	public void resize(int w, int h)
	{

		// using the new approach of Java 2D API 
		BufferedImage buf = new BufferedImage(w,h, image.getType());
		Graphics2D g2d = (Graphics2D) buf.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(image,0,0,w,h,null);
		g2d.dispose();
		image = buf;
		width = w;
		height = h;
		updateColorArray();
	}
	
	/**
	 * Clones the {@link thirdparty.marvin.image.MarvinImage}
	 */
	public MarvinImage clone() {
		MarvinImage newMarvinImg = new MarvinImage(getWidth(), getHeight(), getColorModel());
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		newMarvinImg.setBufferedImage(newImage);
		MarvinImage.copyColorArray(this, newMarvinImg);
		newMarvinImg.update();
		return newMarvinImg;
	}
	/**
	 * Multiple of gradient windwos per masc relation of x y 
	 * @return int[]
	 */
	public double multi8p(int x, int y,double masc){
		int aR = getIntComponent0(x-1,y-1);     int bR = getIntComponent0(x-1,y);    int cR = getIntComponent0(x-1,y+1);
		int aG = getIntComponent1(x-1,y-1);   int bG = getIntComponent1(x-1,y);  int cG = getIntComponent1(x-1,y+1);
		int aB = getIntComponent1(x-1,y-1);   int bB = getIntComponent1(x-1,y);  int cB = getIntComponent1(x-1,y+1);

		
		int dR = getIntComponent0(x,y-1);        int eR = getIntComponent0(x,y);     int fR = getIntComponent0(x,y+1);
		int dG = getIntComponent1(x,y-1);      int eG = getIntComponent1(x,y);   int fG = getIntComponent1(x,y+1);
		int dB = getIntComponent1(x,y-1);      int eB = getIntComponent1(x,y);   int fB = getIntComponent1(x,y+1);

		
		int gR = getIntComponent0(x+1,y-1);      int hR = getIntComponent0(x+1,y);     int iR = getIntComponent0(x+1,y+1);
		int gG = getIntComponent1(x+1,y-1);    int hG = getIntComponent1(x+1,y);   int iG = getIntComponent1(x+1,y+1);
		int gB = getIntComponent1(x+1,y-1);    int hB = getIntComponent1(x+1,y);   int iB = getIntComponent1(x+1,y+1);

		double rgb = 0;

		rgb = ( (aR * masc) + (bR * masc) +(cR * masc)+
				(dR * masc) + (eR * masc) +(fR * masc)+
				(gR * masc) + (hR * masc) +(iR * masc) ); 

		return(rgb);

	}
	
	public int boundRGB(int rgb){

		if(rgb > 255){
			rgb = 255;
			return(rgb);
		}

		if(rgb < 0){
			rgb = 0;
			return(rgb);
		}
		return rgb;
	}
	
	/**
	 * Bresenham�s Line Drawing implementation
	 */
	public void drawLine(int x0, int y0, int x1, int y1, Color c) {
		int colorRGB = c.getRGB();
		int dy = y1 - y0;
		int dx = x1 - x0;
		int stepx, stepy;
		int fraction;
		
		
		if (dy < 0) { dy = -dy; stepy = -1; 
		} else { stepy = 1; 
		}
	 	if (dx < 0) { dx = -dx; stepx = -1; 
		} else { stepx = 1; 
		}
		dy <<= 1; 							// dy is now 2*dy
		dx <<= 1; 							// dx is now 2*dx
	 
		setIntColor(x0, y0, colorRGB);

		if (dx > dy) {
			fraction = dy - (dx >> 1);	// same as 2*dy - dx
			while (x0 != x1) {
				if (fraction >= 0) {
					y0 += stepy;
					fraction -= dx; 		// same as fraction -= 2*dx
				}
				x0 += stepx;
	   		fraction += dy; 				// same as fraction -= 2*dy
	   		setIntColor(x0, y0, colorRGB);
			}
		} else {
			fraction = dx - (dy >> 1);
			while (y0 != y1) {
				if (fraction >= 0) {
					x0 += stepx;
					fraction -= dy;
				}
			y0 += stepy;
			fraction += dx;
			setIntColor(x0, y0, colorRGB);
			}
		}
	} 

	
	/**
	 * Draws a rectangle in the image. It�s useful for debugging purposes.
	 * @param x		rect�s start position in x-axis
	 * @param y		rect�s start positioj in y-axis
	 * @param w		rect�s width
	 * @param h		rect�s height
	 * @param c		rect�s color
	 */
	public void drawRect(int x, int y, int w, int h, Color c){
		int color = c.getRGB();
		for(int i=x; i<x+w; i++){
			setIntColor(i, y, color);
			setIntColor(i, y+(h-1), color);
		}
		
		for(int i=y; i<y+h; i++){
			setIntColor(x, i, color);
			setIntColor(x+(w-1), i, color);
		}
	}
	
	/**
	 * Fills a rectangle in the image.
	 * @param x		rect�s start position in x-axis
	 * @param y		rect�s start positioj in y-axis
	 * @param w		rect�s width
	 * @param h		rect�s height
	 * @param c		rect�s color
	 */
	public void fillRect(int x, int y, int w, int h, Color c){
		int color = c.getRGB();
		for(int i=x; i<x+w; i++){
			for(int j=y; j<y+h; j++){
				setIntColor(i,j,color);
			}
		}
	}
	
	/**
	 * Compare two MarvinImage objects
	 * @param obj	object to be compared. MarvinImage object is expected.
	 */
	public boolean equals(Object obj){
		MarvinImage img = (MarvinImage) obj;
		int[] l_arrColor = img.getIntColorArray();
		
		if(getWidth() != img.getWidth() || getHeight() != img.getHeight()){
			return false;
		}
		
		for(int l_cont=0; l_cont<getHeight(); l_cont++){
			if(arrIntColor[l_cont] != l_arrColor[l_cont]){
				return false;
			}
		}	
		return true;
	}
}