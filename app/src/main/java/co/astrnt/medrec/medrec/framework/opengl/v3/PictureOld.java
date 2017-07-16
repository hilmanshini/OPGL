package co.astrnt.medrec.medrec.framework.opengl.v3;

import android.app.admin.DeviceAdminInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import android.util.Log;

import java.util.regex.MatchResult;

import co.astrnt.medrec.medrec.framework.opengl.v1.BitmapProgram;

import co.astrnt.medrec.medrec.framework.opengl.v1.GLDrawable;
import co.astrnt.medrec.medrec.framework.opengl.v1.TextureGLProgram;
import imageogl.view.opengl_x.GLProgram;

/**
 * Created by hill on 7/13/17.
 */

public class PictureOld extends TextureGLProgram {
    Bitmap mBitmap;
    int x, y, width, height;
    Resources resources;
    Scene scene;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    public PictureOld(Resources mResources, Bitmap mBitmap, int x, int y, int width, int height, Scene scene) {
//        super(mResources, mBitmap);
        super(mResources);
        this.mBitmap = mBitmap;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.scene = scene;

        addBuffer("bitmapf", new float[]{1, 1, 1, 0, 0, 0, 0, 1});
        addBuffer("bitmapf_m90", new float[]{1, 0, 0, 0, 0, 1, 1, 1});
        addBuffer("bitmapf_90", new float[]{0, 0, 1, 0, 1, 1, 0, 1});
        addBuffer("bitmapf_f90", new float[]{0, 0, 0, 1, 1, 1, 1, 0});

        addBuffer("order", new short[]{0, 1, 2, 0, 2, 3});
        float xF = scene.getWidthFactor(x);
        float yF = scene.getHeightFactor(y);
        float wf = scene.getWidthFactor(width);
        float hf = scene.getHeightFactor(height);
        Log.e("WXWX", " " + wf);
        Log.e("XXXX", " " + xF + " " + yF + " " + wf + " " + hf);
        addBuffer("bitmapv", new float[]{
                //top right
                -1 + xF + wf, 1 - yF,
                //bottom right
                -1 + xF + wf, 1 - ((yF + hf)),
                //bottom left
                -1 + xF, 1 - ((yF + hf)),
                //top left
                -1 + xF, 1 - (yF)});
//        Matrix.frustumM(mProjectionMatrix, 0, -scene.getRatio(), scene.getRatio(), -1, 1, 3, 7);

    }

    int d = 0;

    public void draw() {


        use();
        Matrix.setIdentityM(mMVPMatrix, 0);

        Matrix.translateM(mMVPMatrix, 0, scene.getWidthFactor(x), scene.getHeightFactor(y), 1f);

//        bind();

        // Draw square
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(getProgramPointer(), "uMVP"), 1, false, mMVPMatrix, 0);
        enableVertexBuffer("bitmapf_f90", "aTexCoord", 2);
        enableVertexBuffer("bitmapv", "aPosition", 2);
        setUniform("alpha", 1f);
//        enableAlphaChannel();
        draw("order", 6);

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }


    public int getHeight() {
        return height;
    }

    //    float[] rotMatrix = new float[16];
//    float[] scaleMatrix = new float[16];
//    float[] transMatrix = new float[16];
//    int rotation = 0;
//
//    public float[] getRotMatrix() {
//        Matrix.setIdentityM(rotMatrix, 0);
//        Matrix.rotateM(rotMatrix, 0, angle, rotation, 1, 1);
//        return rotMatrix;
//    }
//
//    public float[] getScaleMatrix() {
//        Matrix.setIdentityM(scaleMatrix, 0);
//        Matrix.scaleM(scaleMatrix, 0, scene.getWidthFactor(width), scene.getHeightFactor(height), 1f);
//        return scaleMatrix;
//    }
//
//    public float[] getTransMatrix() {
//        Matrix.setIdentityM(transMatrix, 0);
//        Matrix.translateM(transMatrix, 0, scene.getWidthFactor(x), scene.getHeightFactor(y), 0f);
//        return transMatrix;
//    }
}
