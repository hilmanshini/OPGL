package co.astrnt.medrec.medrec.framework.mediacodec.record.manager;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import co.astrnt.medrec.medrec.framework.mediacodec.record.CompressedMediaVideoRecordHandler;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecordHandler;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;
import co.astrnt.medrec.medrec.tesst.TestCustomDrawer;
import imageogl.view.opengl_x.mediacodec.VideoCodec;

/**
 * Created by hill on 7/18/17.
 */

public class CustomGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private CustomDrawer mCustomDrawer;
    MediaMuxer mediaMuxer;
    private CompressedMediaVideoRecordHandler mMediaVideoRecordHandler;
    AudioVideoRecordManager mEventListener;

    public CustomGLSurfaceView(Context context, MediaMuxer mediaMuxer, AudioVideoRecordManager mEventListener) {
        super(context);
        this.mEventListener = mEventListener;
        this.mediaMuxer = mediaMuxer;
        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    public CustomGLSurfaceView(Context context, AttributeSet attrs, MediaMuxer mediaMuxer, AudioVideoRecordManager mEventListener) {
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

    @Override
    public void onDrawFrame(GL10 gl) {
        mCustomDrawer.draw(new float[]{0, 0});
        if(!terminated){
            mMediaVideoRecordHandler.sendMessage(VideoCodec.DRAW_FRAME, new float[]{0f, 0f});
        }

    }
    Boolean terminated = false;
    public void stop(){
        terminated = true;
        mMediaVideoRecordHandler.sendMessage(VideoCodec.TERMINATE, new Object());
    }
}
