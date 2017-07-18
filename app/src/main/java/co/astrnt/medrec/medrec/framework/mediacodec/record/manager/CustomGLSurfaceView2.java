package co.astrnt.medrec.medrec.framework.mediacodec.record.manager;

import android.content.Context;
import android.media.MediaMuxer;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import co.astrnt.medrec.medrec.framework.mediacodec.record.CompressedMediaVideoRecordHandler;
import imageogl.view.opengl_x.mediacodec.VideoCodec;

/**
 * Created by hill on 7/18/17.
 */

public class CustomGLSurfaceView2 extends GLSurfaceView implements GLSurfaceView.Renderer {
    private CustomDrawer mCustomDrawer;
    MediaMuxer mediaMuxer;
    private CompressedMediaVideoRecordHandler mMediaVideoRecordHandler;
    AudioVideoRecordManager2 mEventListener;

    public CustomGLSurfaceView2(Context context, MediaMuxer mediaMuxer, AudioVideoRecordManager2 mEventListener) {
        super(context);
        this.mEventListener = mEventListener;
        this.mediaMuxer = mediaMuxer;
        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    public CustomGLSurfaceView2(Context context, AttributeSet attrs, MediaMuxer mediaMuxer, AudioVideoRecordManager2 mEventListener) {
        super(context, attrs);
        this.mEventListener = mEventListener;
        this.mediaMuxer = mediaMuxer;
        setEGLContextClientVersion(2);
        setRenderer(this);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mCustomDrawer = new CustomDrawer(getResources());
        mCustomDrawer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCustomDrawer.onSurfaceChanged(width, height);
        try {
            mMediaVideoRecordHandler = CompressedMediaVideoRecordHandler.start(getResources(), mCustomDrawer, EGL14.eglGetCurrentContext(), mEventListener, width, height, mediaMuxer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaVideoRecordHandler.sendMessage(VideoCodec.INIT_CAMERA, new int[]{width, height, mCustomDrawer.mCamera.getTextureCamera()});
    }

    boolean paused;

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mCustomDrawer.draw(new float[]{0, 0});
        if (!terminated && !paused) {
            mMediaVideoRecordHandler.sendMessage(VideoCodec.DRAW_FRAME, new float[]{0f, 0f});
        }

    }

    Boolean terminated = false;

    public void stop() {
        terminated = true;
        mMediaVideoRecordHandler.sendMessage(VideoCodec.TERMINATE, new Object());
    }
}
