package co.astrnt.medrec.medrec.framework.opengl.v3;

import android.content.res.Resources;
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import co.astrnt.medrec.medrec.framework.opengl.v1.Utils;
import co.astrnt.medrec.medrec.framework.opengl.v3.type.Object2D;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;

/**
 * Created by hill on 7/16/17.
 */

public class Scene {
    public float getWidthFactor(int x) {
        return 0;
    }

    public float getHeightFactor(int y) {
        return 0;
    }

    private static final int FAILED_TO_CREATE_SHADER = -1000;
    public static final int FAILED_TO_CREATE_FRAGMENT_SHADER_STATE = -1;
    public static final int FAILED_TO_CREATE_VERTEX_SHADER_STATE = -2;
    public static final int VERTEX_SHADER_COMPILE_OK = 0;
    public static final int LINK_FAILED = -1;
    public static final int LINK_SUCCESS = 0;
    private final Resources mResources;
    private int programPointer = -1;
    public static final int STATE_FAILED_TO_CREATE_PROGRAM = 0;
    public static final int STATE_PROGRAM_OK = 1;
    private int programState;
    int a, r, g, b;

    public Scene(Resources mResources, int a, int r, int g, int b) {
        this.mResources = mResources;
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    int width;
    int height;

    public void updateViewPort(int width, int height) {
        this.width = width;
        this.height = height;
        GLES20.glViewport(0, 0, width, height);
    }

    public void clear() {
        GLES20.glClear(GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(r/255,g/255,b/255,a/255);
    }

    List<Object2D> object2DList = new ArrayList<>();

    public void addOBject(Object2D object2D) {
        object2DList.add(object2D);
    }

    public void pack() {
        for (Object2D object2D : object2DList) {
            log("packing " + object2D.getClass());
            programPointer = GLES20.glCreateProgram();

            if (programPointer == 0) {
                programState
                        = STATE_FAILED_TO_CREATE_PROGRAM;
                log("packing failed ccreate program");
            } else {
                programState = STATE_PROGRAM_OK;
                object2D.setProgramPointer(programPointer);
                log("program ok");
            }

            if (programState == STATE_PROGRAM_OK) {
                applySource(object2D.getVertexSource(), object2D.getFragmentSource());
                object2D.setVertexShaderPointer(vertexShaderPointer);
                object2D.setFragmentShaderPointer(fragmentShaderPointer);
                if (shaderCompileState == VERTEX_SHADER_COMPILE_OK) {
                    log("compile OK");
                    link();
                    if (getLinkStatus() == LINK_SUCCESS) {
                        log("link Ok");
                    } else {
                        log("link failed");
                    }
                } else {
                    log("compile failed");
                }
            }
        }

    }

    private void log(String s) {
        Log.e("HILGL_SceneGL", "> " + s);
    }


    private void link() {
        glAttachShader(programPointer, vertexShaderPointer);
        glAttachShader(programPointer, fragmentShaderPointer);
        glLinkProgram(programPointer);
        int status[] = new int[1];
        glGetProgramiv(programPointer, GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            linkStatus = LINK_FAILED;
        }
        linkStatus = LINK_SUCCESS;
    }

    private int linkStatus = -1;
    private int vertexShaderPointer = -1;
    private int fragmentShaderPointer = -1;
    private int shaderCompileState = 0;

    private int applySource(int vsource, int fsource) {
        vertexShaderPointer = compile(vsource, GL_VERTEX_SHADER);
        if (vertexShaderPointer == FAILED_TO_CREATE_SHADER) {
            shaderCompileState = FAILED_TO_CREATE_VERTEX_SHADER_STATE;
            return shaderCompileState;
        }
        fragmentShaderPointer = compile(fsource, GL_FRAGMENT_SHADER);
        if (fragmentShaderPointer == FAILED_TO_CREATE_SHADER) {
            shaderCompileState = FAILED_TO_CREATE_FRAGMENT_SHADER_STATE;
            return shaderCompileState;
        }
        shaderCompileState = VERTEX_SHADER_COMPILE_OK;
        return shaderCompileState;
    }

    private int compile(int source, int type) {
        String sourceString = Utils.loadShaderSource(mResources, source);
        log("compiling " + sourceString);
        int shaderPointer = glCreateShader(type);
        glShaderSource(shaderPointer, sourceString);
        glCompileShader(shaderPointer);
        int[] status = new int[1];
        glGetShaderiv(shaderPointer, GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            return FAILED_TO_CREATE_SHADER;
        }
        return shaderPointer;
    }


    public int getVertexShaderPointer() {
        return vertexShaderPointer;
    }


    public int getFragmentShaderPointer() {
        return fragmentShaderPointer;
    }


    public int getShaderCompileState() {
        return shaderCompileState;
    }

    private static final String TAG = "HILGL_VERTEX_COMPILE";

    @Override
    public String toString() {
        String x = super.toString();
        Log.e(TAG, "shader compile");
        if (shaderCompileState == FAILED_TO_CREATE_VERTEX_SHADER_STATE) {
            Log.e(TAG, "vertex shader compile failed");
        } else if (shaderCompileState == FAILED_TO_CREATE_FRAGMENT_SHADER_STATE) {
            Log.e(TAG, "fragment shader  compile FAILED");
        } else {
            Log.e(TAG, "vertex compile OK");
            if (linkStatus == LINK_FAILED) {
                Log.e(TAG, "Link failed for program " + programPointer);
            } else {
                Log.e(TAG, "PROGRAM OK");
            }
        }

        return x;
    }

    public int getLinkStatus() {
        return linkStatus;
    }

    public void release() {
        GLES20.glUseProgram(0);

        if (vertexShaderPointer > 0) {
            GLES20.glDeleteShader(vertexShaderPointer);
        }

        if (fragmentShaderPointer > 0) {
            GLES20.glDeleteShader(fragmentShaderPointer);
        }
        if (programPointer > 0) {
            GLES20.glDeleteProgram(programPointer);
        }
    }
}
