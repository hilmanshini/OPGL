package co.astrnt.medrec.medrec.framework.opengl.v2.objects;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import imageogl.view.opengl_x.GLProgram;

import java.nio.FloatBuffer;

public class SimpleGlObject {
    public GLProgram glProgram;
    public float[] mMVPMatrix = new float[16];
    public float[] mProjectionMatrix = new float[16];
    public float[] mViewMatrix = new float[16];

    public SimpleGlObject(GLProgram glProgram) {
        this.glProgram = glProgram;
    }

    public void enableVertices(String name, String var) {
        FloatBuffer verticFB2 = (FloatBuffer) this.glProgram.mapBuffers.get(name);
        int posLoc = GLES20.glGetAttribLocation(this.glProgram.programPointer, var);
        GLES20.glVertexAttribPointer(posLoc, 2, 5126, false, 0, verticFB2);
        GLES20.glEnableVertexAttribArray(posLoc);
    }

    public void checkGlError(String op) {
        int error = GLES20.glGetError();
        if (error != 0) {
            Log.e("PROGR", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    public void viewPort(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    public void fillUniform(String var, float val) {
        GLES20.glUniform1f(GLES20.glGetUniformLocation(this.glProgram.programPointer, var), val);
    }

    public void fillUniform(String var, float x, float y, float z, float w) {
        GLES20.glUniform4f(GLES20.glGetUniformLocation(this.glProgram.programPointer, var), x, y, z, w);
    }

    public void fillUniformMatrix(String var, float[] mMVPMatrix) {
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(this.glProgram.programPointer, var), 1, false, mMVPMatrix, 0);
    }

    public void draw(int size) {
        GLES20.glDrawArrays(5, 0, size);
    }

    private final float[] mRotationMatrix = new float[16];

    public float[] getRotationMatrix(int mAngle) {
        float[] scratch = new float[16];
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        return scratch;
    }
    public float[] getRotationMatrix(int mAngle,float[] mMVPMatrix) {
        float[] scratch = new float[16];
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        return scratch;
    }
}
