package co.astrnt.medrec.medrec.framework.opengl.v3;

/**
 * Created by hill on 7/17/17.
 */

public class PictureAlign {
    public static final int CENTER = 0;
    public static final int TOP_LEFT = 1;
    public static final int TOP_RIGHT = 2;
    public static final int BOTTOM_LEFT = 3;
    public static final int BOTTOM_RIGHT = 4;
    int align = BOTTOM_RIGHT;
    int scalePercent = 30;
    int marginPercent = 4;

    public int getMarginPercent() {
        return marginPercent;
    }

    public void setMarginPercent(int marginPercent) {
        this.marginPercent = marginPercent;
    }

    public PictureAlign() {
    }

    public PictureAlign(int align, int scalePercent) {
        this.align = align;
        this.scalePercent = scalePercent;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public int getScalePercent() {
        return scalePercent;
    }

    public void setScalePercent(int scalePercent) {
        this.scalePercent = scalePercent;
    }
}
