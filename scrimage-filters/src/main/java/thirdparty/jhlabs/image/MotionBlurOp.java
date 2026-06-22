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

package thirdparty.jhlabs.image;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

/**
 * A filter which produces motion blur the faster, but lower-quality way.
 */
public class MotionBlurOp extends AbstractBufferedImageOp {

    private float centreX = 0.5f, centreY = 0.5f;
    private float distance;
    private float angle;
    private float rotation;
    private float zoom;

    /**
     * Construct a MotionBlurOp.
     */
    public MotionBlurOp() {
	}

    /**
     * Construct a MotionBlurOp.
     * @param distance the distance of blur.
     * @param angle the angle of blur.
     * @param rotation the angle of rotation.
     * @param zoom the zoom factor.
     */
	public MotionBlurOp( float distance, float angle, float rotation, float zoom ) {
        this.distance = distance;
        this.angle = angle;
        this.rotation = rotation;
        this.zoom = zoom;
    }

	/**
     * Specifies the angle of blur.
     * @param angle the angle of blur.
     */
	public void setAngle( float angle ) {
		this.angle = angle;
	}

	/**
     * Set the distance of blur.
     * @param distance the distance of blur.
     */
	public void setDistance( float distance ) {
		this.distance = distance;
	}

	/**
     * Set the blur rotation.
     * @param rotation the angle of rotation.
     */
	public void setRotation( float rotation ) {
		this.rotation = rotation;
	}

	/**
     * Set the blur zoom.
     * @param zoom the zoom factor.
     */
	public void setZoom( float zoom ) {
		this.zoom = zoom;
	}

    private int log2( int n ) {
        int m = 1;
        int log2n = 0;

        while (m < n) {
            m *= 2;
            log2n++;
        }
        return log2n;
    }

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        if ( dst == null )
            dst = createCompatibleDestImage( src, null );
        BufferedImage tsrc = src;
        float cx = (float)src.getWidth() * centreX;
        float cy = (float)src.getHeight() * centreY;
        float imageRadius = (float)Math.sqrt( cx*cx + cy*cy );
        float translateX = (float)(distance * Math.cos( angle ));
        float translateY = (float)(distance * -Math.sin( angle ));
        float scale = zoom;
        float rotate = rotation;
        float maxDistance = distance + Math.abs(rotation*imageRadius) + zoom*imageRadius;
        int steps = log2((int)maxDistance);

		translateX /= maxDistance;
		translateY /= maxDistance;
		scale /= maxDistance;
		rotate /= maxDistance;

        if ( steps == 0 ) {
            Graphics2D g = dst.createGraphics();
            g.drawRenderedImage( src, null );
            g.dispose();
            return dst;
        }

        BufferedImage tmp = createCompatibleDestImage( src, null );
        for ( int i = 0; i < steps; i++ ) {
            Graphics2D g = tmp.createGraphics();
            g.drawImage( tsrc, null, null );
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
			g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.5f ) );

            g.translate( cx+translateX, cy+translateY );
            g.scale( 1.0001+scale, 1.0001+scale );  // The .0001 works round a bug on Windows where drawImage throws an ArrayIndexOutofBoundException
            if ( rotation != 0 )
                g.rotate( rotate );
            g.translate( -cx, -cy );

            g.drawImage( dst, null, null );
            g.dispose();
            BufferedImage ti = dst;
            dst = tmp;
            tmp = ti;
            tsrc = dst;

            translateX *= 2;
            translateY *= 2;
            scale *= 2;
            rotate *= 2;
        }
        return dst;
    }

	public String toString() {
		return "Blur/Faster Motion Blur...";
	}
}
