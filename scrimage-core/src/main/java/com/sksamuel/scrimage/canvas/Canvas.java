package com.sksamuel.scrimage.canvas;

import com.sksamuel.scrimage.ImmutableImage;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;

/**
 * A Canvas extends an ImmutableImage with functionality for drawing.
 */
public class Canvas {

    private final ImmutableImage image;

    public Canvas(ImmutableImage image) {
        this.image = image;
    }

    private Graphics2D g2(ImmutableImage image) {
        return (Graphics2D) image.awt().getGraphics();
    }

    public void drawInPlace(Drawable... drawables) {
        drawInPlace(Arrays.asList(drawables));
    }

    public void drawInPlace(Collection<Drawable> drawables) {
        Graphics2D g = g2(image);
        drawables.forEach(d -> {
            d.context().configure(g);
            d.draw(g);
        });
        g.dispose();
    }

    public Canvas draw(Drawable... drawables) {
        return draw(Arrays.asList(drawables));
    }

    public Canvas draw(Collection<Drawable> drawables) {
        ImmutableImage target = image.copy();
        Graphics2D g = g2(target);
        drawables.forEach(d -> {
            d.context().configure(g);
            d.draw(g);
        });
        g.dispose();
        return new Canvas(target);
    }
}


//class RichGraphics2(g2:Graphics2D){
//
//        def setBasicStroke(width:Float):Unit=g2.setStroke(new BasicStroke(width))
//
//        def setWhite():Unit=g2.setColor(Color.White.toAWT)
//        def setBlack():Unit=g2.setColor(Color.Black.toAWT)
//
//        def setAntiAlias(aa:Boolean):Unit={
//        if(aa){
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON)
//        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
//        }else{
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF)
//        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)
//        }
//        }
//        }
//
//        }

