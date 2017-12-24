package com.sksamuel.scrimage.filter;

import com.sksamuel.scrimage.Filter;
import com.sksamuel.scrimage.Image;
import com.sksamuel.scrimage.RGBColor;
import thirdparty.romainguy.BlendComposite;
import thirdparty.romainguy.BlendingMode;

import java.awt.*;

public class BackgroundBlendFilter implements Filter {

    @Override
    public void apply(Image image) {
        Image background = image.fill(new RGBColor(51, 0, 0, 255));
        Graphics2D g = (Graphics2D) image.awt().getGraphics();
        g.setComposite(BlendComposite.getInstance(BlendingMode.ADD, 1f));
        g.drawImage(background.awt(), 0, 0, null);
        g.dispose();
    }
}
