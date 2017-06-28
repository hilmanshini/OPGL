package co.astrnt.medrec.medrec.framework.opengl.programs;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.util.Log;

import static android.opengl.GLES20.*;

/**
 * Created by hill on 6/28/17.
 */

abstract class ScriptedProgram extends OpenGlProgram {
    Resources mResources;
    private static final int FAILED_TO_CREATE_SHADER = -1000;
    public static final int FAILED_TO_CREATE_FRAGMENT_SHADER_STATE = -1;
    public static final int FAILED_TO_CREATE_VERTEX_SHADER_STATE = -2;
    public static final int VERTEX_SHADER_COMPILE_OK = 0;
    public static final int LINK_FAILED = -1;
    public static final int LINK_SUCCESS = 0;

    public ScriptedProgram(Resources mResources) {
        super();
        this.mResources = mResources;
        if (getProgramState() == STATE_PROGRAM_OK) {
            compile();
            if (shaderCompileState == VERTEX_SHADER_COMPILE_OK) {
                link();
            }
        }
    }


    private void link() {
        glAttachShader(getProgramPointer(), vertexShaderPointer);
        glAttachShader(getProgramPointer(), fragmentShaderPointer);
        glLinkProgram(getProgramPointer());
        int status[] = new int[1];
        glGetProgramiv(getProgramPointer(), GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            linkStatus = LINK_FAILED;
        }
        linkStatus = LINK_SUCCESS;
    }

    private int linkStatus = -1;
    private int vertexShaderPointer;
    private int fragmentShaderPointer;
    private int shaderCompileState = 0;

    private int compile() {
        vertexShaderPointer = compile(getVertexShaderResourceInt(), GL_VERTEX_SHADER);
        if (vertexShaderPointer == FAILED_TO_CREATE_SHADER) {
            shaderCompileState = FAILED_TO_CREATE_VERTEX_SHADER_STATE;
            return shaderCompileState;
        }
        fragmentShaderPointer = compile(getFragmentShaderResourceInt(), GL_FRAGMENT_SHADER);
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

    public abstract int getVertexShaderResourceInt();

    public abstract int getFragmentShaderResourceInt();

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
                Log.e(TAG, "Link failed for program " + getProgramPointer());
            } else {
                Log.e(TAG, "PROGRAM OK");
            }
        }

        return x;
    }
    public int getLinkStatus() {
        return linkStatus;
    }
}
