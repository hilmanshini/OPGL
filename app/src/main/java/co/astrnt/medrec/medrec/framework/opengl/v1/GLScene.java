package co.astrnt.medrec.medrec.framework.opengl.v1;

import android.opengl.GLES20;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hill on 6/28/17.
 */

public class GLScene {
    float r,g,b;

    public GLScene(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    int width = MATCH_PARENT;
    int height = MATCH_PARENT;
    public static final int MATCH_PARENT = -2;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    Map<String, OpenGlProgram> mOpenGlProgramMap = new HashMap<>();

    public void addProgram(String name, OpenGlProgram mProgram) {
        mOpenGlProgramMap.put(name, mProgram);
    }

    public void release() {
        for (OpenGlProgram openGlProgram : mOpenGlProgramMap.values()) {
            openGlProgram.release();
        }
    }
    public void viewport(int w,int h){
        GLES20.glViewport(0,0,w,h);
    }
    public void clear(){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(r,g,b,1f);
    }
}
