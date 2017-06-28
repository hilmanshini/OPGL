package co.astrnt.medrec.medrec.framework.opengl.programs;

import android.opengl.GLES20;

/**
 * Created by hill on 6/28/17.
 */

public class RectangleProgram extends OpenGlProgram {
    int programPointer;

    public RectangleProgram() {
        programPointer = GLES20.glCreateProgram();
        if(programPointer == 0){

        }
    }

    @Override
    public int getProgramPointer() {
        return 0;
    }
}
