package image;

import java.awt.*;
import java.awt.image.BufferedImage;

import static java.awt.Image.SCALE_DEFAULT;

class ImageItem {

    private static final int SMALL_SIZE = 100;

    private String source;
    private BufferedImage imageSmall;

    ImageItem(BufferedImage image, String source) {
        this.source = source;

        this.imageSmall = new BufferedImage(SMALL_SIZE, SMALL_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = this.imageSmall.createGraphics();
        bGr.drawImage(image.getScaledInstance(SMALL_SIZE,SMALL_SIZE, SCALE_DEFAULT), 0, 0, null);
        bGr.dispose();
    }

    String getSource() {
        return source;
    }

    BufferedImage getImageSmall() {
        return imageSmall;
    }
}
