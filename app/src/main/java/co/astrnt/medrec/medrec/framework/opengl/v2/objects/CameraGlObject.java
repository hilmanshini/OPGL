package co.astrnt.medrec.medrec.framework.opengl.v2.objects;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import imageogl.view.opengl_x.GLProgram;
import java.io.IOException;
import java.util.List;

public class CameraGlObject extends SimpleGlObject implements OnFrameAvailableListener {
    int[] hTex;
    private Camera mCamera;
    private SurfaceTexture mSTexture;
    boolean mUpdateST;
    boolean open = true;

    public CameraGlObject(GLProgram glProgram) {
        super(glProgram);
    }

    public void applyCamera(int textureId) {
        this.mSTexture = new SurfaceTexture(textureId);
        this.mSTexture.setOnFrameAvailableListener(this);
        this.mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
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
            this.mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            mCamera.getParameters().set("orientation", "portrait");
            mCamera.getParameters().set("rotation", -90);
            try {
                this.mCamera.setPreviewTexture(this.mSTexture);
            } catch (IOException e) {
            }
        }
        GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);
    }

    public int createTexture() {
        this.hTex = new int[1];
        GLES20.glGenTextures(1, this.hTex, 0);
        GLES20.glBindTexture(36197, this.hTex[0]);
        GLES20.glTexParameteri(36197, 10242, 33071);
        GLES20.glTexParameteri(36197, 10243, 33071);
        GLES20.glTexParameteri(36197, 10241, 9728);
        GLES20.glTexParameteri(36197, 10240, 9728);
        return this.hTex[0];
    }

    public void onSurfaceChanged(int width, int height) {
        if (this.open) {
            Parameters param = this.mCamera.getParameters();
            List<Size> psize = param.getSupportedPreviewSizes();
            if (psize.size() > 0) {
                int i = 0;
                while (i < psize.size() && ((Size) psize.get(i)).width >= width && ((Size) psize.get(i)).height >= height) {
                    i++;
                }
                if (i > 0) {
                    i--;
                }
                param.setPreviewSize(((Size) psize.get(i)).width, ((Size) psize.get(i)).height);
            }
            mCamera.getParameters().set("orientation", "portrait");
            mCamera.getParameters().set("rotation", -90);
            param.set("orientation", "portrait");

            this.mCamera.setParameters(param);

            this.mCamera.startPreview();
        }
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.mUpdateST = true;
    }

    public void onDrawFrame() {
        synchronized (this) {
            if (this.mUpdateST) {
                this.mSTexture.updateTexImage();
                this.mUpdateST = false;
            }
        }
    }

    public void activeTexture(int textureId) {
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(36197, textureId);
    }
    public float[] getTransMatrix(){
        float[] q = new float[16];
        mSTexture.getTransformMatrix(q);
        return q;
    }
}
