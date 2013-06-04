/*
Copyright 2006 Jerry Huxtable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

/**
 * A filter which priduces a video feedback effect by repeated transformations.
 */
public class FeedbackFilter extends AbstractBufferedImageOp {
    private float centreX = 0.5f, centreY = 0.5f;
    private float distance;
    private float angle;
    private float rotation;
    private float zoom;
    private float startAlpha = 1;
    private float endAlpha = 1;
    private int iterations;

    /**
     * Construct a FeedbackFilter.
     */
    public FeedbackFilter() {
	}
	
    /**
     * Construct a FeedbackFilter.
     * @param distance the distance to move on each iteration
     * @param angle the angle to move on each iteration
     * @param rotation the amount to rotate on each iteration
     * @param zoom the amount to scale on each iteration
     */
	public FeedbackFilter( float distance, float angle, float rotation, float zoom ) {
        this.distance = distance;
        this.angle = angle;
        this.rotation = rotation;
        this.zoom = zoom;
    }
    
	/**
     * Specifies the angle of each iteration.
     * @param angle the angle of each iteration.
     * @angle
     * @see #getAngle
     */
	public void setAngle( float angle ) {
		this.angle = angle;
	}

	/**
     * Returns the angle of each iteration.
     * @return the angle of each iteration.
     * @see #setAngle
     */
	public float getAngle() {
		return angle;
	}
	
	/**
     * Specifies the distance to move on each iteration.
     * @param distance the distance
     * @see #getDistance
     */
	public void setDistance( float distance ) {
		this.distance = distance;
	}

	/**
     * Get the distance to move on each iteration.
     * @return the distance
     * @see #setDistance
     */
	public float getDistance() {
		return distance;
	}
	
	/**
     * Specifies the amount of rotation on each iteration.
     * @param rotation the angle of rotation
     * @angle
     * @see #getRotation
     */
	public void setRotation( float rotation ) {
		this.rotation = rotation;
	}

	/**
     * Returns the amount of rotation on each iteration.
     * @return the angle of rotation
     * @angle
     * @see #setRotation
     */
	public float getRotation() {
		return rotation;
	}
	
	/**
     * Specifies the amount to scale on each iteration.
     * @param zoom the zoom factor
     * @see #getZoom
     */
	public void setZoom( float zoom ) {
		this.zoom = zoom;
	}

	/**
     * Returns the amount to scale on each iteration.
     * @return the zoom factor
     * @see #setZoom
     */
	public float getZoom() {
		return zoom;
	}
	
	/**
     * Set the alpha value at the first iteration.
     * @param startAlpha the alpha value
     * @min-value 0
     * @max-value 1
     * @see #getStartAlpha
     */
	public void setStartAlpha( float startAlpha ) {
		this.startAlpha = startAlpha;
	}

	/**
     * Get the alpha value at the first iteration.
     * @return the alpha value
     * @see #setStartAlpha
     */
	public float getStartAlpha() {
		return startAlpha;
	}
	
	/**
     * Set the alpha value at the last iteration.
     * @param endAlpha the alpha value
     * @min-value 0
     * @max-value 1
     * @see #getEndAlpha
     */
	public void setEndAlpha( float endAlpha ) {
		this.endAlpha = endAlpha;
	}

	/**
     * Get the alpha value at the last iteration.
     * @return the alpha value
     * @see #setEndAlpha
     */
	public float getEndAlpha() {
		return endAlpha;
	}
	
	/**
	 * Set the centre of the effect in the X direction as a proportion of the image size.
	 * @param centreX the center
     * @see #getCentreX
	 */
	public void setCentreX( float centreX ) {
		this.centreX = centreX;
	}

	/**
	 * Get the centre of the effect in the X direction as a proportion of the image size.
	 * @return the center
     * @see #setCentreX
	 */
	public float getCentreX() {
		return centreX;
	}
	
	/**
	 * Set the centre of the effect in the Y direction as a proportion of the image size.
	 * @param centreY the center
     * @see #getCentreY
	 */
	public void setCentreY( float centreY ) {
		this.centreY = centreY;
	}

	/**
	 * Get the centre of the effect in the Y direction as a proportion of the image size.
	 * @return the center
     * @see #setCentreY
	 */
	public float getCentreY() {
		return centreY;
	}
	
	/**
	 * Set the centre of the effect as a proportion of the image size.
	 * @param centre the center
     * @see #getCentre
	 */
	public void setCentre( Point2D centre ) {
		this.centreX = (float)centre.getX();
		this.centreY = (float)centre.getY();
	}

	/**
	 * Get the centre of the effect as a proportion of the image size.
	 * @return the center
     * @see #setCentre
	 */
	public Point2D getCentre() {
		return new Point2D.Float( centreX, centreY );
	}
	
	/**
     * Set the number of iterations.
     * @param iterations the number of iterations
     * @min-value 0
     * @see #getIterations
     */
	public void setIterations( int iterations ) {
		this.iterations = iterations;
	}

	/**
     * Get the number of iterations.
     * @return the number of iterations
     * @see #setIterations
     */
	public int getIterations() {
		return iterations;
	}
	
    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        if ( dst == null )
            dst = createCompatibleDestImage( src, null );
        float cx = (float)src.getWidth() * centreX;
        float cy = (float)src.getHeight() * centreY;
        float imageRadius = (float)Math.sqrt( cx*cx + cy*cy );
        float translateX = (float)(distance * Math.cos( angle ));
        float translateY = (float)(distance * -Math.sin( angle ));
        float scale = (float)Math.exp(zoom);
        float rotate = rotation;

        if ( iterations == 0 ) {
            Graphics2D g = dst.createGraphics();
            g.drawRenderedImage( src, null );
            g.dispose();
            return dst;
        }
        
		Graphics2D g = dst.createGraphics();
		g.drawImage( src, null, null );
        for ( int i = 0; i < iterations; i++ ) {
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
			g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, ImageMath.lerp( (float)i/(iterations-1), startAlpha, endAlpha ) ) );

            g.translate( cx+translateX, cy+translateY );
            g.scale( scale, scale );  // The .0001 works round a bug on Windows where drawImage throws an ArrayIndexOutofBoundException
            if ( rotation != 0 )
                g.rotate( rotate );
            g.translate( -cx, -cy );

            g.drawImage( src, null, null );
        }
		g.dispose();
        return dst;
    }
    
	public String toString() {
		return "Effects/Feedback...";
	}
}
