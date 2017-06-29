package co.astrnt.medrec.medrec.framework.opengl.programs;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by hill on 6/28/17.
 */

abstract class OpenGlProgram {
    private int programPointer = -1;
    public static final int STATE_FAILED_TO_CREATE_PROGRAM = 0;
    public static final int STATE_PROGRAM_OK = 1;
    private int programState;

    public OpenGlProgram() {
        programPointer = GLES20.glCreateProgram();
        if (programPointer == 0) {
            programState = STATE_FAILED_TO_CREATE_PROGRAM;
        }
        programState = STATE_PROGRAM_OK;
    }

    public int getProgramState() {
        return programState;
    }


    public int getProgramPointer() {
        return programState;
    }

    private static final String TAG = "HILGL_PROGRAM_COMPILE";

    @Override
    public String toString() {
        Log.e(TAG, "Program compile state");
        if (programState == STATE_PROGRAM_OK) {
            Log.e(TAG, "Program compile state OK with id " + programPointer);
        } else {
            Log.e(TAG, "Program compile FAILED");
        }
        return super.toString();
    }

    public void release() {
        if (programPointer > 0) {
            GLES20.glDeleteProgram(getProgramPointer());
        }
    }
    public void use(){
        GLES20.glUseProgram(programPointer);
    }

}
