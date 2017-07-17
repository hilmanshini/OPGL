package co.astrnt.medrec.medrec.framework.opengl.v3;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import co.astrnt.medrec.medrec.BuildConfig;
import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.opengl.v1.Utils;
import co.astrnt.medrec.medrec.framework.opengl.v3.type.ScriptedObject2D;

/**
 * Created by hill on 7/16/17.
 */

public class Picture extends ScriptedObject2D {
    private int textureActiveId;
    float[] mMVPMatrix = new float[16];
    int bitmapSrc;

    public Picture(Resources mResources, int bitmap) {
        super(mResources);
        this.bitmapSrc = bitmap;
        mPictureAlign = new PictureAlign();
    }

    PictureAlign mPictureAlign;

    public Picture(Resources mResources, int bitmap, PictureAlign mPictureAlign) {
        super(mResources);
        this.bitmapSrc = bitmap;
        this.mPictureAlign = mPictureAlign;

    }

    public int getVertexSource() {
        return R.raw.test1_v1;
    }

    public int getFragmentSource() {
        return R.raw.test1_f1;
    }

    @Override
    public void draw() {
        beginDraw();
        Matrix.setIdentityM(mMVPMatrix, 0);
        fillMatrix("uMVP", mMVPMatrix);
        enableVertices("positionVertex", "aPosition");
        enableVertices("coordVertex", "aTexCoord");
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, 6, GLES20.GL_UNSIGNED_SHORT, getBuffer("order"));
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        enableAlphaChannel();
        endDraw();
    }

    int width = 540, height = 980;
    int x = 0, y = 0;

    @Override
    public void onUpdateViewPort(Scene scene, int width, int height, int orientation) {

        if (orientation == Surface.ROTATION_0) {
            this.textureActiveId = loadTexture(bitmapSrc);
            Log.e("ORR", " " + orientation + " " + originalTextureW + " " + originalTextureH);
            this.width = originalTextureW;
            this.height = originalTextureH;
            x = width / 2 - this.width / 2;
            y = height / 2 - this.height / 2;
            applyAlign(width, height);
            float valW = (scene.getWidthFactor(this.width) * 2);
            float valH = (scene.getHeightFactor(this.height) * 2);

            float valX = -1 + (scene.getWidthFactor(x) * 2);
            float valY = -1 + (scene.getHeightFactor(y) * 2);
            Log.e("SIZEX", " " + valX + " " + valY + " " + valW + " " + valH);
            if (mPictureAlign.getAlign() == PictureAlign.CENTER) {
                addBuffer("positionVertex",
                        //top right
                        valX + valW, valY,
                        //bottom right
                        valX + valW, valY + valH,
                        //bottom left
                        valX, valY + valH,
                        //top left
                        valX, valY
                );
            } else if (mPictureAlign.getAlign() == PictureAlign.TOP_LEFT) {
                addBuffer("positionVertex",
                        //top right
                        valX + valW, valY,
                        //bottom right
                        valX + valW, valY + valH,
                        //bottom left
                        valX, valY + valH,
                        //top left
                        valX, valY
                );
            }

            addBuffer("coordVertex", 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
            addBuffer("order", new short[]{0, 1, 2, 0, 2, 3});

        } else if (orientation == Surface.ROTATION_90) {
            this.textureActiveId = loadTexture(bitmapSrc);
            Log.e("ORR", " " + orientation + " " + originalTextureW + " " + originalTextureH);
            this.width = originalTextureW;
            this.height = originalTextureH;

            x = width / 2 - this.width / 2;
            y = height / 2 - this.height / 2;
            applyAlign(width, height);
            Log.e("RESULTZ", " " + x + " " + y + " " + this.width + " " + this.height);
            float valW = (scene.getWidthFactor(this.width) * 2);
            float valH = (scene.getHeightFactor(this.height) * 2);

            float valX = -1 + (scene.getWidthFactor(x) * 2);
            float valY = -1 + (scene.getHeightFactor(y) * 2);
            Log.e("SIZEX", " " + valX + " " + valY + " " + valW + " " + valH);
            Log.e("SIZEX", " top right " + (valX + valW) + ": " + valY);
            Log.e("SIZEX", " bottom right " + (valX + valW) + ": " + (valY + valH));
            Log.e("SIZEX", " top left " + (valX) + ": " + (valY));
            Log.e("SIZEX", " bottom left " + (valX) + ": " + (valY + valH));

            addBuffer("positionVertex",
                    //top right
                    valX + valW, valY,
                    //bottom right
                    valX + valW, valY + valH,
                    //top left
                    valX, valY,
                    //bottom left
                    valX, valY + valH
            );


            addBuffer("coordVertex", 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f);
            addBuffer("order", new short[]{3, 2, 0, 3, 0, 1,});

        }

    }

    private void applyAlign(int width, int height) {
        if (mPictureAlign.getAlign() == PictureAlign.BOTTOM_RIGHT) {
            int scale = mPictureAlign.getScalePercent();
            int oldWidth = this.width;
            int mustHaveWidth = (int) ((float) width * ((float) scale / 100));
            float factor = (float) mustHaveWidth / (float) oldWidth;

            Log.e("RESULTZZ","BEFORE "+this.width);
            this.width = mustHaveWidth;
            this.height = (int) (this.height * factor);
            int marginW = width / 100 * mPictureAlign.getMarginPercent();
            int marginH = height / 100 * mPictureAlign.getMarginPercent();
            Log.e("RESULTZZ", " " + width + " " + this.width + " " + marginW + " " + mustHaveWidth + " " + factor + " " );
            x = width - this.width - marginW;
            y = marginH;
        } else if (mPictureAlign.getAlign() == PictureAlign.TOP_RIGHT) {
            int scale = mPictureAlign.getScalePercent();
            int oldWidth = this.width;
            int mustHaveWidth = (int) ((float) width * ((float) scale / 100));
            float factor = (float) mustHaveWidth / (float) oldWidth;

            Log.e("RESULTZZ","BEFORE "+this.width);
            this.width = mustHaveWidth;
            this.height = (int) (this.height * factor);
            int marginW = width / 100 * mPictureAlign.getMarginPercent();
            int marginH = height / 100 * mPictureAlign.getMarginPercent();
            Log.e("RESULTZZ", " " + width + " " + this.width + " " + marginW + " " + mustHaveWidth + " " + factor + " " );
            x = width - this.width - marginW;
            y = height - marginH;
        } else if (mPictureAlign.getAlign() == PictureAlign.BOTTOM_LEFT) {
            int scale = mPictureAlign.getScalePercent();
            int oldWidth = this.width;
            int mustHaveWidth = (int) ((float) width * ((float) scale / 100));
            float factor = (float) mustHaveWidth / (float) oldWidth;

            Log.e("RESULTZZ","BEFORE "+this.width);
            this.width = mustHaveWidth;
            this.height = (int) (this.height * factor);
            int marginW = width / 100 * mPictureAlign.getMarginPercent();
            int marginH = height / 100 * mPictureAlign.getMarginPercent();
            Log.e("RESULTZZ", " " + width + " " + this.width + " " + marginW + " " + mustHaveWidth + " " + factor + " " );
            x = marginW;
            y = height - this.height - marginH;
        } else if (mPictureAlign.getAlign() == PictureAlign.TOP_LEFT) {
            int scale = mPictureAlign.getScalePercent();
            int oldWidth = this.width;
            int mustHaveWidth = (int) ((float) width * ((float) scale / 100));
            float factor = (float) mustHaveWidth / (float) oldWidth;

            Log.e("RESULTZZ","BEFORE "+this.width);
            this.width = mustHaveWidth;
            this.height = (int) (this.height * factor);
            int marginW = width / 100 * mPictureAlign.getMarginPercent();
            int marginH = height / 100 * mPictureAlign.getMarginPercent();
            Log.e("RESULTZZ", " " + width + " " + this.width + " " + marginW + " " + mustHaveWidth + " " + factor + " " );
            x = marginW;
            y = marginH;
        }
    }


}
