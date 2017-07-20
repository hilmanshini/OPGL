package co.astrnt.medrec.medrec.framework.opengl.v1;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.util.Log;

import co.astrnt.medrec.medrec.R;

/**
 * Created by hill on 6/29/17.
 */

public class BitmapProgram extends MatrixGLProgram {
    Bitmap mBitmap;
    private int textureActive;

    public BitmapProgram(Resources mResources, Bitmap mBitmap) {
        super(mResources);
        this.mBitmap = mBitmap;
        loadTexture();
    }

    @Override
    public int getVertexShaderResourceInt() {
        return R.raw.script_test_bitmap_v;
    }

    @Override
    public int getFragmentShaderResourceInt() {
        return R.raw.script_test_bitmap_f;
    }

    public void loadTexture() {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);
        Log.e("TEXTT2", "" + textureHandle[0]);
        if (textureHandle[0] != 0) {


            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            // Load the bitmap into the bound texture.

            android.opengl.GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            mBitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }
        textureActive = textureHandle[0];
    }

    public void enableAlphaChannel() {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
    }

}
