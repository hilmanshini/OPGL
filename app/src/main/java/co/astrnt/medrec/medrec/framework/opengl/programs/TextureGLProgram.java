package co.astrnt.medrec.medrec.framework.opengl.programs;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import co.astrnt.medrec.medrec.R;

/**
 * Created by hill on 6/29/17.
 */

public class TextureGLProgram extends MatrixGLProgram {
    private int mTextureID;

    public TextureGLProgram(Resources mResources) {
        super(mResources);
    }

    @Override
    public int getVertexShaderResourceInt() {
        return R.raw.test_texture_v;
    }

    @Override
    public int getFragmentShaderResourceInt() {
        return R.raw.test_texture_f;
    }
    public SurfaceTexture createSurfaceTexture(int w, int h) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        SurfaceTexture mSurfaceTexture = new SurfaceTexture(mTextureID);
        mSurfaceTexture.setDefaultBufferSize(w, h);
        return mSurfaceTexture;
    }

    public void activeTexture() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID);
    }

    public void setTexture(String name,float value) {
        GLES20.glUniform1f(GLES20.glGetUniformLocation(getProgramPointer(), name), value);
    }
}
