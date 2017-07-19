package co.astrnt.medrec.medrec.framework.mediacodec.record.manager;

import android.content.res.Resources;
import android.view.Surface;

import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;
import co.astrnt.medrec.medrec.framework.opengl.v3.Camera;
import co.astrnt.medrec.medrec.framework.opengl.v3.Picture;
import co.astrnt.medrec.medrec.framework.opengl.v3.Scene;

/**
 * Created by hill on 7/18/17.
 */

public class CustomDrawer implements IDrawer {
    Camera mCamera;
    Scene mScene;
    int textureCamera = -1;
    boolean open = false;
    Picture mPicture;
    Resources mResources;

    public CustomDrawer(Resources mResources) {
        open = true;
        this.mResources = mResources;
    }

    public CustomDrawer(Resources mResources, int textureCamera) {
        this.mResources = mResources;
        this.textureCamera = textureCamera;
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        int rotation = Surface.ROTATION_90;
        if (open) {
            mCamera.startPreview(width, height, rotation);
        }
        mScene.updateViewPort(width, height, rotation);
    }

    @Override
    public void draw(Object... params) {
        mCamera.draw();
//            mPicture.enableAlphaChannel();
        mPicture.draw();
    }

    @Override
    public void clear() {
        mScene.clear();
    }

    @Override
    public void onSurfaceCreated() {
        mScene = new Scene(mResources, 1, 1, 1, 1);
        mPicture = new Picture(mResources, R.drawable.logointro);
        if (textureCamera >= 0) {
            mCamera = new Camera(mResources, textureCamera, mScene);
        } else {
            mCamera = new Camera(mResources, mScene);
            this.textureCamera = mCamera.getTextureCamera();
        }
        mScene.addOBject(mPicture);
        mScene.addOBject(mCamera);
        mScene.pack();
    }

    @Override
    public void release() {
        mCamera.release();
        mPicture.release();
        mScene.release();
    }

}