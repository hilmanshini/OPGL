package co.astrnt.medrec.medrec.framework.opengl.v1;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ShortBuffer;

/**
 * Created by hill on 6/29/17.
 */

public class MatrixGLProgram extends BufferedProgram implements GLDrawable {
    public float[] mProjectionMatrix = new float[16];
    public float[] mViewMatrix = new float[16];
    public float[] mMVPMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    private int screenWidth;
    private int screenHeight;
    private float wFactor;
    private float hFactor;

    public MatrixGLProgram(Resources mResources) {
        super(mResources);
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
    }

    public void viewport(int width, int height) {
        screenWidth = width;
        screenHeight = height;
        wFactor = 1f / screenWidth;
        hFactor = 1f / screenHeight;
        float ratio = (float) width / height;

//        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public void draw() {

    }

    public float translatePixelWidth(int pixel) {
        return pixel * wFactor;
    }

    public float translatePixelHeight(int pixel) {
        return pixel * hFactor;
    }


    @Override
    public int getVertexShaderResourceInt() {
        return 0;
    }

    @Override
    public int getFragmentShaderResourceInt() {
        return 0;
    }

    @Override
    public void draw(int size) {
//        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,size);
    }

    @Override
    public void draw(String bufferName, int size) {
//        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        Buffer buffer = bufferMap.get(bufferName);;
        if(buffer instanceof ShortBuffer){
            ShortBuffer mShortBuffer = (ShortBuffer) bufferMap.get(bufferName);
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,size,GLES20.GL_UNSIGNED_SHORT,mShortBuffer);
        }
    }

    public void translate(int xDelta, int yDelta) {
        Log.e("MATR", "TRANSLATE");
        Matrix.translateM(mViewMatrix, 0, 0.0f - translatePixelWidth(xDelta), 0.0f - translatePixelHeight(yDelta), 0);

    }

    int xpos = 100;
    int ypos = 0;

    public void fillMatrix(String var) {
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(getProgramPointer(), var), 1, false, mMVPMatrix, 0);
    }
    public void fillMatrix(String var,float[] data) {
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(getProgramPointer(), var), 1, false, data, 0);
    }
    public void flushTransform() {
        Log.e("MATR", "MULTIPLY");
        translate(xpos, ypos);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    public int getXpos() {
        return xpos;
    }

    public void setXpos(int xpos) {
        this.xpos = xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public void setYpos(int ypos) {
        this.ypos = ypos;
    }

    public float[] getRotationMatrix(int mAngle){
        float[] scratch = new float[16];
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        return scratch;
    }
}
