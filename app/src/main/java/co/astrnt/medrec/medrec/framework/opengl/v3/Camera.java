package co.astrnt.medrec.medrec.framework.opengl.v3;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.opengl.v3.type.ScriptedObject2D;

/**
 * Created by hill on 7/16/17.
 */

public class Camera extends ScriptedObject2D implements SurfaceTexture.OnFrameAvailableListener {
    private SurfaceTexture mSTexture;
    private android.hardware.Camera mCamera;

    public Camera(Resources mResources) {
        super(mResources);
        addBuffer("cameraVertex", -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f);
        addBuffer("textureVertex", 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
        textureCamera = createTexture();
        applyCamera(textureCamera);

    }
    int textureCamera;




    @Override
    public void draw() {
        beginDraw();
        updateTexture();
        float[] trans = new float[16];
        mSTexture.getTransformMatrix(trans);
        float[] mat = new float[16];
        Matrix.setIdentityM(mat,0);
        fillMatrix("uMVP",mat);
        fillMatrix("uSTm", trans);
        enableVertices("cameraVertex", "vPosition");
        enableVertices("textureVertex", "vTexCoord");
        fillUniform("sTexture", 0.0f);
        activeTexture(hTex[0]);
        GLES20.glDrawArrays(5, 0, 4);
        endDraw();

    }

    @Override
    public int getVertexSource() {
        return R.raw.cam_v;
    }

    @Override
    public int getFragmentSource() {
        return R.raw.cam_f;
    }

    int[] hTex;

    public int createTexture() {

        hTex = new int[1];
        GLES20.glGenTextures(1, hTex, 0);
        GLES20.glBindTexture(36197, hTex[0]);
        GLES20.glTexParameteri(36197, 10242, 33071);
        GLES20.glTexParameteri(36197, 10243, 33071);
        GLES20.glTexParameteri(36197, 10241, 9728);
        GLES20.glTexParameteri(36197, 10240, 9728);
        return hTex[0];
    }

    public void applyCamera(int textureId) {
        this.mSTexture = new SurfaceTexture(textureId);
        this.mSTexture.setOnFrameAvailableListener(this);
        this.mCamera = android.hardware.Camera.open(android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
        mCamera.getParameters().set("orientation", "portrait");
        mCamera.getParameters().set("rotation", -90);

        mCamera.setDisplayOrientation(270);
        try {

            this.mCamera.setPreviewTexture(this.mSTexture);
        } catch (IOException e) {
        }

        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
    }

    boolean updateTexture;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateTexture = true;
        log("frame available");
    }

    public void updateTexture() {
        synchronized (this) {
            if (this.updateTexture) {
                this.mSTexture.updateTexImage();
                updateTexture = false;
            }
        }
    }

    public void activeTexture(int textureId) {
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(36197, textureId);
    }

    public void startPreview(int width, int height) {

            android.hardware.Camera.Parameters param = this.mCamera.getParameters();
            List<android.hardware.Camera.Size> psize = param.getSupportedPreviewSizes();
            if (psize.size() > 0) {
                int i = 0;
                while (i < psize.size() && ((android.hardware.Camera.Size) psize.get(i)).width >= width && ((android.hardware.Camera.Size) psize.get(i)).height >= height) {
                    i++;
                }
                if (i > 0) {
                    i--;
                }
                param.setPreviewSize(((android.hardware.Camera.Size) psize.get(i)).width, ((android.hardware.Camera.Size) psize.get(i)).height);
            }
            mCamera.getParameters().set("orientation", "portrait");
            mCamera.getParameters().set("rotation", -90);
            param.set("orientation", "portrait");

            this.mCamera.setParameters(param);

            this.mCamera.startPreview();

    }

    public static void log(String s) {
        Log.e("HILGL_Camera", "> " + s);
    }

    public void fillMatrix(String name, float[] mMVPMatrix) {
        log("getting uniform forname :" + name + "   result: " + GLES20.glGetUniformLocation(this.getProgramPointer(), name) + " " + " for program " + getProgramPointer());
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(this.getProgramPointer(), name), 1, false, mMVPMatrix, 0);
    }

    public void fillMatrixIdentity(String name) {
        float[] matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        fillMatrix(name, matrix);
    }
}
