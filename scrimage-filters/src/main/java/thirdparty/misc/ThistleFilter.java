package thirdparty.misc;

import thirdparty.romainguy.BlendComposite;
import thirdparty.romainguy.BlendingMode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Sarah Schölzel
 */
public class ThistleFilter {

    public BufferedImage filter(BufferedImage i) {
        BufferedImage dstImg = new BufferedImage(i.getWidth(), i.getHeight(), BufferedImage.TYPE_INT_ARGB);

        BufferedImage tmp = new BufferedImage(i.getWidth(), i.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gtmp = tmp.createGraphics();
        gtmp.setColor(new Color(17, 24, 66));
        gtmp.fill(new Rectangle(0, 0, i.getWidth(), i.getHeight()));

        BufferedImage tmp2 = new BufferedImage(i.getWidth(), i.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gtmp2 = tmp2.createGraphics();
        gtmp2.setColor(new Color(220, 227, 84));
        gtmp2.fill(new Rectangle(0, 0, i.getWidth(), i.getHeight()));

        //Blending Mode: Lighten, 55%
        gtmp2.setComposite(BlendComposite.getInstance(BlendingMode.LIGHTEN, 0.55f));
        gtmp2.drawImage(tmp, 0, 0, i.getWidth(), i.getHeight(), null);

        //texture
        BufferedImage texture;
        try {
            texture = ImageIO.read(getClass().getResource("/com/sksamuel/scrimage/filter/texture_old_square.jpg"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        //Blending Mode: Multiply, 75%
        tmp = new BufferedImage(i.getWidth(), i.getHeight(), BufferedImage.TYPE_INT_ARGB);
        gtmp = tmp.createGraphics();
        gtmp.drawImage(texture, 0, 0, i.getWidth(), i.getHeight(), null);
        gtmp.setComposite(BlendComposite.getInstance(BlendingMode.MULTIPLY, 0.75f));
        gtmp.drawImage(tmp2, 0, 0, i.getWidth(), i.getHeight(), null);

        //Blending Mode: Overlay, 65%
        Graphics2D gdst = dstImg.createGraphics();
        gdst.drawImage(i, 0, 0, null);
        gdst.setComposite(BlendComposite.getInstance(BlendingMode.OVERLAY, 1f));
        gdst.drawImage(tmp, 0, 0, i.getWidth(), i.getHeight(), null);

        return dstImg;
    }
}
