package imageogl.view.opengl_x;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Scanner;

import co.astrnt.medrec.medrec.BuildConfig;

public class GLUtils {

    public enum PROGRAM_STATE {
        CANT_CREATE_PROGRAM,
        UNINITIALIZED,
        FAILED_TO_COMPILE_VERTEX,
        FAILED_TO_COMPILE_FRAGMENT,
        LINK_FAILED,
        SUCCESS;

        public String extra;
        public int fragmentShaderPointer;
        public int programPointer;
        public int vertexShaderPointer;
    }

    public static PROGRAM_STATE createProgram(Resources resources, int vertexSource, int fragmentSource) {
        PROGRAM_STATE state = PROGRAM_STATE.UNINITIALIZED;
        String vertexSourceString = loadShader(resources, vertexSource);
        String fragmentSourceString = loadShader(resources, fragmentSource);
        int programPointer = GLES20.glCreateProgram();
        if (programPointer == 0) {
            state = PROGRAM_STATE.CANT_CREATE_PROGRAM;
            state.extra = BuildConfig.FLAVOR + GLES20.glGetError() + " " + android.opengl.GLUtils.getEGLErrorString(GLES20.glGetError()) + " " + GLES20.glGetProgramInfoLog(programPointer);
            return state;
        }
        int vertexShaderPointer = GLES20.glCreateShader(35633);
        GLES20.glShaderSource(vertexShaderPointer, vertexSourceString);
        GLES20.glCompileShader(vertexShaderPointer);
        int[] status = new int[1];
        GLES20.glGetShaderiv(vertexShaderPointer, 35713, status, 0);
        if (status[0] == 0) {
            state = PROGRAM_STATE.FAILED_TO_COMPILE_VERTEX;
            state.extra = GLES20.glGetShaderInfoLog(vertexShaderPointer);
            return state;
        }
        int fragmentShaderPointer = GLES20.glCreateShader(35632);
        GLES20.glShaderSource(fragmentShaderPointer, fragmentSourceString);
        GLES20.glCompileShader(fragmentShaderPointer);
        GLES20.glGetShaderiv(fragmentShaderPointer, 35713, status, 0);
        if (status[0] == 0) {
            state = PROGRAM_STATE.FAILED_TO_COMPILE_FRAGMENT;
            state.extra = GLES20.glGetShaderInfoLog(vertexShaderPointer);
            return state;
        }
        GLES20.glAttachShader(programPointer, vertexShaderPointer);
        GLES20.glAttachShader(programPointer, fragmentShaderPointer);
        GLES20.glLinkProgram(programPointer);
        GLES20.glGetProgramiv(programPointer, 35714, status, 0);
        if (status[0] == 0) {
            state = PROGRAM_STATE.LINK_FAILED;
            state.extra = GLES20.glGetProgramInfoLog(programPointer);
            return state;
        }
        GLES20.glUseProgram(programPointer);
        state = PROGRAM_STATE.SUCCESS;
        state.programPointer = programPointer;
        state.vertexShaderPointer = vertexShaderPointer;
        state.fragmentShaderPointer = fragmentShaderPointer;
        return state;
    }

    public static String loadShader(Resources resources, int id) {
        Scanner scanner = new Scanner(resources.openRawResource(id));
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
            sb.append("\n");
        }
        return sb.toString();
    }

    public static FloatBuffer allocate(float... x) {
        FloatBuffer verticFB = ByteBuffer.allocateDirect(x.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticFB.put(x);
        verticFB.position(0);
        return verticFB;
    }

    public static ShortBuffer allocateShort(short... x) {

        ShortBuffer verticFB = ByteBuffer.allocateDirect(x.length * (Short.SIZE / 8)).order(ByteOrder.nativeOrder()).asShortBuffer();
        verticFB.put(x);
        verticFB.position(0);
        return verticFB;
    }

    public static float[] test(float[] mMMatrix, float mAngleX, float mAngleY, float scaleFactorx, float scaleFactory, float scaleFactorz,float mVMatrix[],float mProjMatrix[]) {
        float[] mAccumulatedRotation = new float[16];
        Matrix.setIdentityM(mMMatrix, 0);
        float[] mMVPMatrix = new float[16];

        float[] mCurrentRotation = new float[16];
        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.rotateM(mCurrentRotation, 0, mAngleX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotation, 0, mAngleY, -1.0f, 0.0f, 0.0f);
        mAngleX = 0.0f;
        mAngleY = 0.0f;
        Matrix.scaleM(mMMatrix, 0, scaleFactorx, scaleFactory, scaleFactorz);

        // Multiply the current rotation by the accumulated rotation, and then set the accumulated rotation to the result.
        float[] mTemporaryMatrix = new float[16];
        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);

        // Rotate the cube taking the overall rotation into account.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mMMatrix, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMMatrix, 0, 16);

        //Matrix.multiplyMM(mMMatrix, 0, tmpMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
        mAngleX = 0.0f;
        mAngleY = 0.0f;
        return  mMVPMatrix;
    }
}
