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
import java.util.*;

public class ShatterFilter extends AbstractBufferedImageOp {
    private float centreX = 0.5f, centreY = 0.5f;
    private float distance;
    private float transition;
    private float rotation;
    private float zoom;
    private float startAlpha = 1;
    private float endAlpha = 1;
    private int iterations = 5;
    private int tile;

    public ShatterFilter() {
	}
	
	public void setTransition( float transition ) {
		this.transition = transition;
	}

	public float getTransition() {
		return transition;
	}
	
	public void setDistance( float distance ) {
		this.distance = distance;
	}

	public float getDistance() {
		return distance;
	}
	
	public void setRotation( float rotation ) {
		this.rotation = rotation;
	}

	public float getRotation() {
		return rotation;
	}
	
	public void setZoom( float zoom ) {
		this.zoom = zoom;
	}

	public float getZoom() {
		return zoom;
	}
	
	public void setStartAlpha( float startAlpha ) {
		this.startAlpha = startAlpha;
	}

	public float getStartAlpha() {
		return startAlpha;
	}
	
	public void setEndAlpha( float endAlpha ) {
		this.endAlpha = endAlpha;
	}

	public float getEndAlpha() {
		return endAlpha;
	}
	
	public void setCentreX( float centreX ) {
		this.centreX = centreX;
	}

	public float getCentreX() {
		return centreX;
	}
	
	public void setCentreY( float centreY ) {
		this.centreY = centreY;
	}

	public float getCentreY() {
		return centreY;
	}
	
	public void setCentre( Point2D centre ) {
		this.centreX = (float)centre.getX();
		this.centreY = (float)centre.getY();
	}

	public Point2D getCentre() {
		return new Point2D.Float( centreX, centreY );
	}
	
	public void setIterations( int iterations ) {
		this.iterations = iterations;
	}

	public int getIterations() {
		return iterations;
	}
	
	public void setTile( int tile ) {
		this.tile = tile;
	}

	public int getTile() {
		return tile;
	}
	
	static class Tile {
		float x, y, vx, vy, w, h;
		float rotation;
		Shape shape;
	}
	
    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        if ( dst == null )
            dst = createCompatibleDestImage( src, null );
        float width = (float)src.getWidth();
        float height = (float)src.getHeight();
        float cx = (float)src.getWidth() * centreX;
        float cy = (float)src.getHeight() * centreY;
        float imageRadius = (float)Math.sqrt( cx*cx + cy*cy );

//        BufferedImage[] tiles = new BufferedImage[iterations];
		int numTiles = iterations*iterations;
        Tile[] shapes = new Tile[numTiles];
        float[] rx = new float[numTiles];
        float[] ry = new float[numTiles];
        float[] rz = new float[numTiles];

		Graphics2D g = dst.createGraphics();
//		g.drawImage( src, null, null );

        Random random = new Random( 0 );
		float lastx = 0, lasty = 0;
/*
        for ( int i = 0; i <= numTiles; i++ ) {
            double angle = (double)i * 2*Math.PI / numTiles;
            float x = cx + width*(float)Math.cos(angle);
            float y = cy + height*(float)Math.sin(angle);
            g.setColor( Color.black );
            g.setColor( Color.getHSBColor( (float)angle, 1, 1 ) );
			if ( i != 0 ) {
				rz[i-1] = tile*(2*random.nextFloat()-1);
				ry[i-1] = tile*random.nextFloat();
				rz[i-1] = tile*random.nextFloat();
                GeneralPath p = new GeneralPath();
                p.moveTo( cx, cy );
                p.lineTo( lastx, lasty );
                p.lineTo( x, y );
                p.closePath();
				shapes[i-1] = p;
//                Rectangle r = p.getBounds();
//                r.intersect( r, new Rectangle( (int)width, (int)height ), r );
            }
            lastx = x;
            lasty = y;
        }
*/
        for ( int y = 0; y < iterations; y++ ) {
			int y1 = (int)height*y/iterations;
			int y2 = (int)height*(y+1)/iterations;
			for ( int x = 0; x < iterations; x++ ) {
				int i = y*iterations+x;
				int x1 = (int)width*x/iterations;
				int x2 = (int)width*(x+1)/iterations;
				rx[i] = tile*random.nextFloat();
				ry[i] = tile*random.nextFloat();
			rx[i] = 0;
			ry[i] = 0;
				rz[i] = tile*(2*random.nextFloat()-1);
                Shape p = new Rectangle( x1, y1, x2-x1, y2-y1 );
				shapes[i] = new Tile();
				shapes[i].shape = p;
				shapes[i].x = (x1+x2)*0.5f;
				shapes[i].y = (y1+y2)*0.5f;
				shapes[i].vx = width-(cx-x);
				shapes[i].vy = height-(cy-y);
				shapes[i].w = x2-x1;
				shapes[i].h = y2-y1;
            }
        }

		for ( int i = 0; i < numTiles; i++ ) {
			float h = (float)i / numTiles;
			double angle = h * 2*Math.PI;
			float x = transition * width*(float)Math.cos(angle);
			float y = transition * height*(float)Math.sin(angle);

			Tile tile = shapes[i];
			Rectangle r = tile.shape.getBounds();
			AffineTransform t = g.getTransform();
x = tile.x + transition * tile.vx;
y = tile.y + transition * tile.vy;
			g.translate( x, y );
//			g.translate( tile.w*0.5f, tile.h*0.5f );
			g.rotate( transition * rz[i] );
//			g.scale( (float)Math.cos( transition * rx[i] ), (float)Math.cos( transition * ry[i] ) );
//			g.translate( -tile.w*0.5f, -tile.h*0.5f );
			g.setColor( Color.getHSBColor( h, 1, 1 ) );
			Shape clip = g.getClip();
			g.clip( tile.shape );
			g.drawImage( src, 0, 0, null );
			g.setClip( clip );
			g.setTransform( t );
		}

		g.dispose();
        return dst;
    }
    
	public String toString() {
		return "Transition/Shatter...";
	}
}
