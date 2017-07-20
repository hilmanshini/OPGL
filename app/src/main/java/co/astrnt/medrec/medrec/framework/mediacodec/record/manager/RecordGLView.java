package co.astrnt.medrec.medrec.framework.mediacodec.record.manager;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import co.astrnt.medrec.medrec.framework.mediacodec.record.CompressedMediaVideoRecordHandler;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecordThread;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecordThread;
import imageogl.view.opengl_x.mediacodec.VideoCodec;

/**
 * Created by hill on 7/18/17.
 */

public class RecordGLView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private CustomDrawer mCustomDrawer;
    MediaMuxer mediaMuxer;
    MediaVideoRecordThread mediaVideoRecordThread;
    MediaVideoRecord.Listener mEventListener;

    public RecordGLView(Context context, MediaMuxer mediaMuxer, MediaVideoRecord.Listener mEventListener) {
        super(context);
        this.mEventListener = mEventListener;
        this.mediaMuxer = mediaMuxer;
        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    public RecordGLView(Context context, AttributeSet attrs, MediaMuxer mediaMuxer, MediaVideoRecord.Listener mEventListener) {
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

    MediaAudioRecordThread mediaAudioRecordThread;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCustomDrawer.onSurfaceChanged(width, height);
        try {
            mediaVideoRecordThread = MediaVideoRecordThread.start(getResources(), mCustomDrawer, EGL14.eglGetCurrentContext(), mEventListener, width, height, mediaMuxer, mCustomDrawer.textureCamera);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaVideoRecordThread.start();

        mediaVideoRecordThread.waitForInitialization();
        mediaAudioRecordThread = new MediaAudioRecordThread(new MediaAudioRecord.Listener() {
            @Override
            public void onFinish(int track) {

            }

            @Override
            public boolean onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex) {
                mediaMuxer.start();
                return false;
            }

            @Override
            public void onPrepared(MediaCodec mMediaCodec) {

            }

            @Override
            public void onWriteDataToMuxer(int mTrackIndex, boolean eos, MediaCodec.BufferInfo mBufferInfo) {
                Log.e("AudioThread2", "write");
            }

            @Override
            public void waitForInit() {

            }
        }, mediaMuxer, true);

        synchronized (initLock){
            initLock.notifyAll();
        }

    }
    Object initLock = new Object();

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
        if (!terminated && !paused && started) {
            mediaVideoRecordThread.drawFrame();
            Log.e("AudioThread2", "req write");

        }

    }

    boolean started = false;

    public void start() {
        started = true;
        while (mediaAudioRecordThread == null){
            synchronized (initLock){
                try {
                    initLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        mediaAudioRecordThread.start();
    }

    public Boolean terminated = false;

    public void stop() {
        terminated = true;
        mediaAudioRecordThread.terminate();
        mediaVideoRecordThread.terminate();


    }
}
