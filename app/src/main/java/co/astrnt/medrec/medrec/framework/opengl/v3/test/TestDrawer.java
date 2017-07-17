package co.astrnt.medrec.medrec.framework.opengl.v3.test;

import android.content.res.Resources;
import android.opengl.GLSurfaceView.Renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import co.astrnt.medrec.medrec.framework.opengl.v3.Camera;
import co.astrnt.medrec.medrec.framework.opengl.v3.Picture;
import co.astrnt.medrec.medrec.framework.opengl.v3.Scene;

/**
 * Created by hill on 7/16/17.
 */

public class TestDrawer implements Renderer {
    Resources mResources;

    public TestDrawer(Resources mResources) {
        this.mResources = mResources;
    }

    private Scene mScene;
    Camera mCamera;
    Picture mPicture;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mScene = new Scene(mResources, 255, 0, 255, 100);
        mCamera = new Camera(mResources,mScene);
        mPicture = new Picture(mResources,android.R.drawable.ic_media_play);
        mPicture.addBuffer("positionVertex", 0.5f, 0.5f, 0.5f, -0.5f, -0.5f, -0.5f, -0.5f, 0.5f);
        mScene.addOBject(mCamera);
        mScene.addOBject(mPicture);
        mScene.pack();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mScene.updateViewPort(width, width, mResources.getConfiguration().orientation);
        mCamera.startPreview(width, height,mResources.getConfiguration().orientation);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mScene.clear();
        mCamera.draw();
        mPicture.draw();
    }
}
