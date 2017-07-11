package co.astrnt.medrec.medrec.framework.widget.camera;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
//import co.astrnt.medrec.medrec.framework.opengl.v1.TextureGLProgram;

/**
 * Created by hill on 6/29/17.
 */

public class TestGLSurface extends GLSurfaceView implements GLSurfaceView.Renderer {
    public TestGLSurface(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
//    private GLScene mGlScene;
//    VideoEncoder mVideoEncoder;
//
//    public TestGLSurface(Context context) {
//        super(context);
//        setEGLContextClientVersion(2);
//        setRenderer(this);
//
//
//        //mVideoEncoder = new VideoEncoder(1024, 768, 1000000, 15, 10, "/sdcard/www.mp4", "video/avc", this);
//
//    }
//
//    public TestGLSurface(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        setEGLContextClientVersion(2);
//        setRenderer(this);
//    }
//
//    TextureGLProgram mTextureGLProgram;
//    VideoDecoder mVideoDecoder;
//
//
//    @Override
//    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        mGlScene = new GLScene(1f, 0f, 1f);
//
//
//        mTextureGLProgram = new TextureGLProgram(getResources());
//        mTextureGLProgram.addBuffer("vertex", new float[]{-1, -1, -1, 1, 1, 1, 1, -1});
//        mTextureGLProgram.addBuffer("fragment", new float[]{1, 1, 1, 0, 0, 0, 0, 1});
//        mTextureGLProgram.addBuffer("order", new short[]{0, 1, 2, 0, 2, 3});
//
//    }
//
//    SurfaceTexture mSurfaceTexture;
//
//
//    @Override
//    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        mGlScene.viewport(width, height);
//        mSurfaceTexture = mTextureGLProgram.createSurfaceTexture(width, height);
//        mVideoDecoder = new VideoDecoder(mSurfaceTexture, "/sdcard/test.mp4");
//
//
//        if (mVideoDecoder.getState() == VideoDecoder.HAS_VIDEO_TRACK) {
//            mVideoDecoder.createDecoder();
//            mVideoDecoder.start();
//        }
//        mTextureGLProgram.viewport(width, height);
//    }
//
//    @Override
//    public void onDrawFrame(GL10 gl) {
//        boolean needDraw = mVideoDecoder.drawIfAny();
//
//        if (needDraw) {
//
//            mTextureGLProgram.use();
//            mTextureGLProgram.enableVertexBuffer("vertex", "vPosition", 2);
//            mTextureGLProgram.enableVertexBuffer("fragment", "vTexCoord", 2);
//            mTextureGLProgram.activeTexture();
//            mTextureGLProgram.fillMatrix("uMVP", mTextureGLProgram.getRotationMatrix(90));
//            mTextureGLProgram.draw("order", 6);
//        }
//        if (mVideoEncoder != null) {
//            mVideoEncoder.encode();
//        }
//
//
//    }
//


}
