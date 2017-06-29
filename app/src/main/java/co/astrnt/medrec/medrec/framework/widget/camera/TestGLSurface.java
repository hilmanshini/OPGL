package co.astrnt.medrec.medrec.framework.widget.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import co.astrnt.medrec.medrec.framework.mediacodec.decoder.VideoDecoder;
import co.astrnt.medrec.medrec.framework.opengl.programs.GLScene;
import co.astrnt.medrec.medrec.framework.opengl.programs.TextureGLProgram;

/**
 * Created by hill on 6/29/17.
 */

public class TestGLSurface extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private GLScene mGlScene;

    public TestGLSurface(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    public TestGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    TextureGLProgram mTextureGLProgram;
    VideoDecoder mVideoDecoder;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mGlScene = new GLScene(1f, 0f, 1f);
        mTextureGLProgram = new TextureGLProgram(getResources());
        mTextureGLProgram.addBuffer("vertex", new float[]{-1, -1, -1, 1, 1, 1, 1, -1});
        mTextureGLProgram.addBuffer("fragment", new float[]{1, 1, 1, 0, 0, 0, 0, 1});
        mTextureGLProgram.addBuffer("order", new short[]{0, 1, 2, 0, 2, 3});


    }

    SurfaceTexture mSurfaceTexture;


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mGlScene.viewport(width, height);
        mSurfaceTexture = mTextureGLProgram.createSurfaceTexture(width, height);
        mVideoDecoder = new VideoDecoder(mSurfaceTexture, "/sdcard/test.mp4");

        if (mVideoDecoder.getState() == VideoDecoder.HAS_VIDEO_TRACK) {
            mVideoDecoder.createDecoder();
            mVideoDecoder.start();
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mGlScene.clear();
        boolean needDraw = mVideoDecoder.drawIfAny();
        if(needDraw){
            mTextureGLProgram.use();
            mTextureGLProgram.enableVertexBuffer("vertex","vPosition",2);
            mTextureGLProgram.enableVertexBuffer("fragment","vTexCoord",2);
            mTextureGLProgram.activeTexture();
            mTextureGLProgram.flushTransform();
            mTextureGLProgram.fillMatrixIdentity("uMVP");
            mTextureGLProgram.setTexture("sTexture",0);
            mTextureGLProgram.draw("order",6);
        }






    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.e("HILGL_FRAME","AVAIL");
    }
}
