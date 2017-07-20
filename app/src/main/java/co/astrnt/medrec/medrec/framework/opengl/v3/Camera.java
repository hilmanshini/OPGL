package co.astrnt.medrec.medrec.framework.opengl.v3;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.opengl.v3.type.ScriptedObject2D;

/**
 * Created by hill on 7/16/17.
 */

public class Camera extends ScriptedObject2D implements SurfaceTexture.OnFrameAvailableListener {
    private SurfaceTexture mSTexture;
    private android.hardware.Camera mCamera;
    boolean open = true;
    Scene mScene;

    public Camera(Resources mResources, Scene mScene) {
        super(mResources);
        this.mScene = mScene;
        addBuffer("cameraVertex", -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f);
        addBuffer("textureVertex", 0f, 1f, 0f, 0f, 1f, 1f, 1f, 0f);
        textureCamera = createTexture();
        applyCamera(textureCamera);

    }

    public Camera(Resources mResources, int textureCamera, Scene mScene) {
        super(mResources);
        this.mScene = mScene;
        addBuffer("cameraVertex", -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f);
        addBuffer("textureVertex", 0f, 1f, 0f, 0f, 1f, 1f, 1f, 0f);
        this.textureCamera = textureCamera;
        applyCamera(textureCamera, false);
    }

    public int getTextureCamera() {
        return textureCamera;
    }

    int textureCamera;

    public void logArray(float[] array) {
        for (int i = 0; i < array.length; i++) {
            Log.e("ARR#", i + " " + array[i] + "  " + " " + open);
        }

    }

    @Override
    public void draw() {
        beginDraw();
        updateTexture();


        float[] mat = new float[16];
        Matrix.setIdentityM(mat, 0);
        fillMatrix("uMVP", mat);
        fillMatrix("uSTm", mat);
        enableVertices("cameraVertex", "vPosition");
        enableVertices("textureVertex", "vTexCoord");
        fillUniform("sTexture", 0.0f);
        activeTexture(textureCamera);
        GLES20.glDrawArrays(5, 0, 4);
        endDraw();

    }

    @Override
    public void onUpdateViewPort(Scene scene,  int width, int height,int i) {
        if(i == Surface.ROTATION_90){
            addBuffer("textureVertex", 1f, 1f, 0f, 1f, 1f, 0f, 0f, 0f);
        }
    }

    @Override
    public int getVertexSource() {
        return R.raw.script_cam_v;
    }

    @Override
    public int getFragmentSource() {
        return R.raw.script_cam_f;
    }

    //int[] hTex;

    public int createTexture() {

        int[] hTex = new int[1];
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

    public void applyCamera(int textureId, boolean open) {
        this.open = open;
        this.mSTexture = new SurfaceTexture(textureId);
        this.mSTexture.setOnFrameAvailableListener(this);
        if (open) {
            this.mCamera = android.hardware.Camera.open(android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
            mCamera.getParameters().set("orientation", "portrait");
            mCamera.getParameters().set("rotation", -90);
            try {
                this.mCamera.setPreviewTexture(this.mSTexture);
            } catch (IOException e) {
            }
        }
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
    }

    AtomicInteger updateTexture = new AtomicInteger(0);

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateTexture.getAndIncrement();
        log("frame available");
    }

    public void updateTexture() {
        synchronized (this) {
            while (this.updateTexture.get() > 0) {
                this.mSTexture.updateTexImage();
                this.updateTexture.getAndDecrement();
            }
        }
    }

    public void activeTexture(int textureId) {
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(36197, textureId);
    }
    android.hardware.Camera.Size videoSize;

    public android.hardware.Camera.Size getVideoSize() {
        return videoSize;
    }

    public void startPreview(int width, int height, int orientation) {
        android.hardware.Camera.Parameters param = this.mCamera.getParameters();
        List<android.hardware.Camera.Size> psize = param.getSupportedPreviewSizes();

        for (android.hardware.Camera.Size size : psize) {
            if( size.width > height){
                videoSize = size;
                log("choosing "+size.width+" "+size.height);
                param.setPreviewSize(size.width,size.height);
                break;
            }
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

    @Override
    public void release() {
        super.release();
        GLES20.glDeleteTextures(1,new int[]{textureCamera},0);
        if(mCamera != null){
            mCamera.release();
        }
    }
}
