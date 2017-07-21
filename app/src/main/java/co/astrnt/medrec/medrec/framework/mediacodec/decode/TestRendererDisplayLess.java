package co.astrnt.medrec.medrec.framework.mediacodec.decode;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import co.astrnt.medrec.medrec.framework.opengl.v3.Scene;
import co.astrnt.medrec.medrec.framework.opengl.v3.Video;

/**
 * Created by hill on 7/21/17.
 */

public class TestRendererDisplayLess implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    Resources mRes;
    GLSurfaceView mGlSurfaceView;

    public TestRendererDisplayLess(Resources mRes) {
        this.mRes = mRes;
        this.mGlSurfaceView = mGlSurfaceView;
    }


    Video mVideo;
    private static final float[] VERTICES = {1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f};
    private static final float[] TEXCOORD = {1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f};
    Scene mScene;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mScene = new Scene(mRes, 255, 255, 255, 255);
        mVideo = new Video(mRes,this,"/sdcard/axuu.mp4");
        mScene.addOBject(mVideo);
        mScene.pack();
    }



    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mScene.updateViewPort(width, height, Surface.ROTATION_90);
        mVideo.addBuffer(Video.VERTEX_VAR,VERTICES);
        mVideo.addBuffer(Video.FRAGMENT_VAR,TEXCOORD);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mScene.clear();
        mVideo.draw();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.e("MCODEC", " FRAME AVAIL");
        mGlSurfaceView.requestRender();
    }
}
