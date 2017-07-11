package imageogl.view.opengl_x;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import imageogl.view.opengl_x.GLUtils.PROGRAM_STATE;
import co.astrnt.medrec.medrec.framework.opengl.v2.objects.SimpleGlObject;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GLProgram {
    public int fsource;
    public Map<String, FloatBuffer> mapBuffers = new HashMap();
    List<SimpleGlObject> objects = new ArrayList();
    public int programPointer;
    public PROGRAM_STATE state;
    public int vsource;
    private float wFactor;
    private float hFactor;

    public GLProgram(int vsource, int fsource, Resources res) {
        this.vsource = vsource;
        this.fsource = fsource;
        init(res);
    }

    private void init(Resources res) {
        this.state = GLUtils.createProgram(res, this.vsource, this.fsource);
        this.programPointer = this.state.programPointer;
        String e = ";";
        if (this.state == PROGRAM_STATE.FAILED_TO_COMPILE_VERTEX) {
            e = GLES20.glGetShaderInfoLog(35633);
        }
        if (this.state == PROGRAM_STATE.FAILED_TO_COMPILE_FRAGMENT) {
            e = GLES20.glGetShaderInfoLog(35632);
        }
        Log.e("PROG", "CREATE " + this.programPointer + " " + this + " " + this.state.name() + " " + e);
    }

    public void allocate(String name, float... values) {
        this.mapBuffers.put(name, GLUtils.allocate(values));
    }


    public float[] createCenterArray(float n) {
        return new float[]{n, n, n, -n, -n, -n, -n, n};
    }

    public float[] createPositiveArray(float n) {
        return new float[]{n, n, n, 0.0f, 0.0f, 0.0f, 0.0f, n};
    }

    public void clear() {
        GLES20.glClear(16384);
    }

    public void clear(float x, float y, float z) {
        GLES20.glClear(16384);
        GLES20.glClearColor(x, y, z, 1.0f);
    }

    public void beginDraw() {
        GLES20.glUseProgram(this.programPointer);
    }

    public void endDraw() {
        GLES20.glUseProgram(0);
    }

    public void addObject(SimpleGlObject... objec) {
        for (SimpleGlObject o : objec) {
            this.objects.add(o);
        }
    }

    public void onSurfaceChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        for (SimpleGlObject s : this.objects) {
            float ratio = ((float) width) / ((float) height);
            Matrix.frustumM(s.mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 3.0f, 7.0f);
        }
        Log.e("SCREENWH"," "+width+":"+height);
        wFactor = 1f / width;
        hFactor = 1f / height;
        float ratio = (float) width / height;
    }

    public void onDrawFrame(float xDelta, float yDelta,float xScale,float yScale,float zScale) {
        for (SimpleGlObject s : this.objects) {
            Matrix.setLookAtM(s.mViewMatrix, 0, 0.0f, 0.0f, -3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);

            Matrix.scaleM(s.mViewMatrix,0,xScale,yScale,zScale);
            Matrix.translateM(s.mViewMatrix, 0, 0.0f - xDelta, 0.0f - yDelta, 0.0f);

            Matrix.multiplyMM(s.mMVPMatrix, 0, s.mProjectionMatrix, 0, s.mViewMatrix, 0);
        }
    }

    public float getwFactor() {
        return wFactor;
    }

    public void setwFactor(float wFactor) {
        this.wFactor = wFactor;
    }

    public float gethFactor() {
        return hFactor;
    }

    public void sethFactor(float hFactor) {
        this.hFactor = hFactor;
    }
    public float translatePixelWidth(int pixel) {
        return pixel * wFactor;
    }

    public float translatePixelHeight(int pixel) {
        return pixel * hFactor;
    }
}
