package co.astrnt.medrec.medrec.framework.opengl.v1;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hill on 6/28/17.
 */

public abstract class BufferedProgram extends ScriptedProgram {
    Map<String, Buffer> bufferMap = new HashMap<>();

    public BufferedProgram(Resources mResources) {
        super(mResources);
    }

    public void addBuffer(String name, float... data) {
        if (bufferMap.containsKey(name)) {
            bufferMap.get(name).clear();
        }
        Buffer mBuffer = Utils.allocate(data);
        bufferMap.put(name, mBuffer);
    }

    public void addBuffer(String name, int... data) {
        if (bufferMap.containsKey(name)) {
            bufferMap.get(name).clear();
        }
        Buffer mBuffer = Utils.allocate(data);
        bufferMap.put(name, mBuffer);
    }

    public void addBuffer(String name, short... data) {
        if (bufferMap.containsKey(name)) {
            bufferMap.get(name).clear();
        }
        Buffer mBuffer = Utils.allocate(data);
        bufferMap.put(name, mBuffer);
    }

    int vertexActive = -1;

    public void enableVertexBuffer(String name, String var, int size) {
        int posLoc = GLES20.glGetAttribLocation(getProgramPointer(), var);
        Buffer mBuffer = bufferMap.get(name);
        GLES20.glVertexAttribPointer(posLoc, size, GLES20.GL_FLOAT, false, 0, mBuffer);
        GLES20.glEnableVertexAttribArray(posLoc);
        vertexActive = posLoc;
    }

    public void release() {
        if (vertexActive != -1) {
            GLES20.glDisableVertexAttribArray(vertexActive);
        }
        for (Buffer buffer : bufferMap.values()) {
            buffer.clear();
        }
        bufferMap.clear();
        super.release();
    }

    public void disableVertexBuffer(String var) {
        int posLoc = GLES20.glGetAttribLocation(getProgramPointer(), var);
        GLES20.glDisableVertexAttribArray(posLoc);
    }

    public void enableUniform(String name, String var, int size) {
        Buffer mBuffer = bufferMap.get(name);
        int location = GLES20.glGetUniformLocation(getProgramPointer(), var);
        if (mBuffer instanceof IntBuffer) {
            GLES20.glUniform4iv(location, size, (IntBuffer) mBuffer);
        } else if (mBuffer instanceof FloatBuffer) {
            GLES20.glUniform4fv(location, size, (FloatBuffer) mBuffer);
        }
    }
    private static final String TAG = "HILGL_BUFFER";
    @Override
    public String toString() {

        String x = super.toString();
        Log.e(TAG,"BUFFERS: "+bufferMap.size());
        for (String s : bufferMap.keySet()) {
            Log.e(TAG,"     name: "+s);
        }
        return x;
    }
    public void setUniform(String value, float a) {
        GLES20.glUniform1f(GLES20.glGetUniformLocation(getProgramPointer(), value), a);
    }
    public void setUniform3(String value, float x,float y,float z) {
        GLES20.glUniform3f(GLES20.glGetUniformLocation(getProgramPointer(), value), x,y,z);
    }
    public void setUniform4(String value, float x,float y,float z,float a) {
        GLES20.glUniform4f(GLES20.glGetUniformLocation(getProgramPointer(), value), x,y,z,a);
    }

}
