package co.astrnt.medrec.medrec.framework.mediacodec.decode.test2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.Surface;

import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;
import co.astrnt.medrec.medrec.framework.opengl.v3.Picture;
import co.astrnt.medrec.medrec.framework.opengl.v3.PictureAlign;
import co.astrnt.medrec.medrec.framework.opengl.v3.Rectangle;
import co.astrnt.medrec.medrec.framework.opengl.v3.Scene;
import co.astrnt.medrec.medrec.framework.opengl.v3.Video;

/**
 * Created by hill on 7/21/17.
 */

public class MyDrawer implements IDrawer, SurfaceTexture.OnFrameAvailableListener {
    Video mVideo;
    String inputPath;

    Resources mResources;

    public MyDrawer(String inputPath, Resources mResources) {
        this.inputPath = inputPath;
        this.mResources = mResources;
    }

    Scene mScene;
    private static final float[] VERTICES = {1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f};
    private static final float[] TEXCOORD = {1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
    Rectangle mRectangle;
    Picture mPicture;

    @Override
    public void onSurfaceChanged(int width, int height) {
        mScene = new Scene(mResources, 255, 255, 255, 255);
        mVideo = new Video(mResources, this, inputPath);
        mRectangle = new Rectangle(mResources, 0, 100, 100, 100);
        mPicture = new Picture(mResources, R.drawable.logointro, new PictureAlign(PictureAlign.CENTER, 40));
        mScene.addOBject(mRectangle);
        mScene.addOBject(mVideo);
        mScene.addOBject(mPicture);
        mScene.pack();
        mScene.updateViewPort(width, height, Surface.ROTATION_90);
        mVideo.addBuffer(Video.VERTEX_VAR, VERTICES);
        mVideo.addBuffer(Video.FRAGMENT_VAR, TEXCOORD);
    }

    public Rectangle getmRectangle() {
        return mRectangle;
    }

    @Override
    public void draw(Object... params) {
        if(phase == 0){
            mScene.clear();
            mVideo.draw();
            mRectangle.draw();
            hasFrame = false;
        } else if(phase == 1){
//            mScene.clear();
            mPicture.draw();
            hasFrame = false;
        }

    }

    public void setPhase(int phase) {
        this.phase = phase;
    }

    int phase = 0;

    @Override
    public void clear() {

    }

    @Override
    public void onSurfaceCreated() {

    }

    @Override
    public void release() {

    }

    public boolean feed() {
        boolean eos = mVideo.readSampleData();

        mVideo.writeSampleData();
        Log.e("DECEND", "TESTFRAME " + hasFrame);
        return eos;
    }

    boolean hasFrame;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.e("DECEND", "HASFRM");
        hasFrame = true;
    }

    public void updateFadeOut1stStep(float factor) {
        Log.e("FADEOUT", (int) (factor * 100) + "");
        mRectangle.setA((int) (factor * 100));
    }
}
