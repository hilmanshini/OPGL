package co.astrnt.medrec.medrec.framework.opengl.v3.type;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import co.astrnt.medrec.medrec.framework.opengl.v1.Utils;

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

public abstract class ScriptedObject2D extends BufferedObject2D {
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

    public ScriptedObject2D(Resources mResources) {
        super(mResources);
        programPointer = GLES20.glCreateProgram();
        if (programPointer == 0) {
            programState = STATE_FAILED_TO_CREATE_PROGRAM;
        } else {
            programState = STATE_PROGRAM_OK;
        }
        this.mResources = mResources;
        if (programState == STATE_PROGRAM_OK) {
            compile();
            if (shaderCompileState == VERTEX_SHADER_COMPILE_OK) {
                link();
            }
        }
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

    private int compile() {
        vertexShaderPointer = compile(getVertexSource(), GL_VERTEX_SHADER);
        if (vertexShaderPointer == FAILED_TO_CREATE_SHADER) {
            shaderCompileState = FAILED_TO_CREATE_VERTEX_SHADER_STATE;
            return shaderCompileState;
        }
        fragmentShaderPointer = compile(getFragmentSource(), GL_FRAGMENT_SHADER);
        if (fragmentShaderPointer == FAILED_TO_CREATE_SHADER) {
            shaderCompileState = FAILED_TO_CREATE_FRAGMENT_SHADER_STATE;
            return shaderCompileState;
        }
        shaderCompileState = VERTEX_SHADER_COMPILE_OK;
        return shaderCompileState;
    }

    private int compile(int source, int type) {
        String sourceString = Utils.loadShaderSource(mResources, source);
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

    public abstract int getVertexSource();

    public abstract int getFragmentSource();

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

    public Resources getResources() {
        return mResources;
    }

    public void beginDraw(){
        log("enabling program "+programPointer);

        GLES20.glUseProgram(programPointer);
    }

    private void log(String s) {
        Log.e(getClass().getName()+"HILGL_ScrObj2D","> "+s);
    }

    public void endDraw()
    {
        log("disabling program "+programPointer);
        GLES20.glUseProgram(0);
    }
    public void fillUniform(String var, float val) {
        log("fill uniform1 for "+var+" #"+GLES20.glGetUniformLocation(this.programPointer, var));
        GLES20.glUniform1f(GLES20.glGetUniformLocation(this.programPointer, var), val);
    }
    public void fillUniform4(String var, float... val) {
        log("fill uniform4 for "+var+" #"+GLES20.glGetUniformLocation(this.programPointer, var));

        GLES20.glUniform4f(GLES20.glGetUniformLocation(this.programPointer, var), val[0],val[1],val[2],val[3]);
    }
}
