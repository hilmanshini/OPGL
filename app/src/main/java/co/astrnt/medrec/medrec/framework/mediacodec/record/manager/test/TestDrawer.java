package co.astrnt.medrec.medrec.framework.mediacodec.record.manager.test;

import android.content.res.Resources;
import android.view.Surface;

import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;
import co.astrnt.medrec.medrec.framework.opengl.v3.Picture;
import co.astrnt.medrec.medrec.framework.opengl.v3.Scene;

/**
 * Created by hill on 7/20/17.
 */

public class TestDrawer implements IDrawer {
    private Scene mScene;
    private Picture mPicture;
    Resources mResources;

    public TestDrawer(Resources mResources) {
        this.mResources = mResources;
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        mScene = new Scene(mResources, 1, 1, 1, 1);
        mPicture = new Picture(mResources, R.drawable.logointro);
        mScene.addOBject(mPicture);
        mScene.pack();
        mScene.updateViewPort(width, height, Surface.ROTATION_90);
    }

    @Override
    public void draw(Object... params) {
        mPicture.draw();
    }

    @Override
    public void clear() {

    }

    @Override
    public void onSurfaceCreated() {

    }

    @Override
    public void release() {

    }

}
