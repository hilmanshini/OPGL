package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.content.res.Resources;
import android.media.MediaMuxer;
import android.opengl.EGLContext;
import android.os.HandlerThread;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import co.astrnt.medrec.medrec.framework.opengl.IDrawer;

/**
 * Created by hill on 7/20/17.
 */

public class MediaVideoRecordThread extends Thread {
    private MediaVideoRecord mMediaVideoRecord;
    private IDrawer mDrawer;
    private Resources mResources;
    private MediaVideoRecord.Listener mListener;

    public MediaVideoRecordThread(MediaVideoRecord mMediaVideoRecord, IDrawer mDrawer, Resources mResources, MediaVideoRecord.Listener mListener) {
        this.mMediaVideoRecord = mMediaVideoRecord;
        this.mDrawer = mDrawer;
        this.mResources = mResources;
        this.mListener = mListener;
    }


    int cameraWidth, cameraHeight, textureCamera;

    public void setCameraProperties(int cameraWidth, int cameraHeight, int textureCamera) {
        this.cameraWidth = cameraWidth;

        this.cameraHeight = cameraHeight;
        this.textureCamera = textureCamera;
    }

    public void setWidthAndHeight(int width, int height) {
        this.cameraWidth = width;
        this.cameraHeight = height;

    }

    public void _sampling() {
        Log.e("VideoThread", "sampling");
        while (!mMediaVideoRecord.hasNewFormat) {
            _drawFrame();
        }

        Log.e("VideoThread", "sampling done");
    }

    private void _terminate() {
        Log.e("VideoThread", "terminating");
        mMediaVideoRecord.drain(true);
        mMediaVideoRecord.release();
        mDrawer.release();
        Log.e("VideoThread", "terminating done");
    }

    long startTime = -1;

    public void drawFrame() {
        Log.e("VideoThread", "draw frame");
        queue.offer(0);

    }

    public void terminate() {
        Log.e("VideoThread", "terminate frame");
        queue.offer(1);

    }


    private void _drawFrame() {
        long time = 0;
        if (startTime == -1) {
            startTime = System.nanoTime();
        } else {
            time = System.nanoTime() - startTime;
        }
        if (isValidTiming(time, startTime)) {

            Log.e("VideoThread", "handleMessage: vidtime: " + time);
            mMediaVideoRecord.drain(false);
            mDrawer.clear();
            mDrawer.draw(0, 0);


            mMediaVideoRecord.swapDisplay(time);
        }
    }

    long nextTargetTime;

    private boolean isValidTiming(long time, long startTime) {
        long oneNanos = TimeUnit.SECONDS.toNanos(1) / frameRate;
        if (nextTargetTime < time) {
            nextTargetTime = time + oneNanos;
            lastCurrentSecondsTime = 0;
            Log.e("TIMING", " " + time + " " + nextTargetTime + " " + lastCurrentSecondsTime + " " + frameRate + " " + true);
            return true;
        } else {
            Log.e("TIMING", " " + time + " " + nextTargetTime + " " + lastCurrentSecondsTime + " " + frameRate + " " + false);

            return false;
        }

    }

    @Override
    public void run() {

        super.run();

        init();
        _sampling();
        synchronized (initLock) {
            initLock.notifyAll();
        }
        initialized = true;
        mListener.waitForInit();
        while (true) {
            try {
                Integer flag = (Integer) queue.take();
                Log.e("VideoThread", "get flag " + flag);
                if (flag == 0) {
                    _drawFrame();
                } else if (flag == 1) {
                    _terminate();
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mListener.onFinish(mMediaVideoRecord.getmTrackIndex());

    }


    public void init() {
        mMediaVideoRecord.makeCurrent();
        int[] xy = null;
        if (textureCamera != -1) {
            xy = new int[]{cameraWidth, cameraHeight, textureCamera};
            mDrawer = mListener.getDrawerMediaCodecInitCamera(xy);
        } else {
            xy = new int[]{cameraWidth, cameraHeight};
            mDrawer = mListener.getDrawerMediaCodecInit();
        }

        mDrawer.onSurfaceChanged(xy[0], xy[1]);

    }

    boolean initialized = false;
    Object initLock = new Object();

    public void waitForInitialization() {
        Log.e("VideoThread", "wait init ");
        if (!initialized) {
            synchronized (initLock) {
                try {
                    initLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.e("VideoThread", "wait init done");
    }

    long lastValidTime = -1;
    int frameRate = 10;
    long lastCurrentSeconds = 0;
    long lastCurrentSecondsTime = 0;
    BlockingQueue queue = new ArrayBlockingQueue(100000);

    public static MediaVideoRecordThread start(Resources mResources, IDrawer mDrawer, MediaVideoRecord.Listener mListener, int width, int height, MediaMuxer mediaMuxer) throws IOException {

        MediaVideoRecord mMediaVideoRecord = new MediaVideoRecord(mResources, mListener, width, height, mediaMuxer);
        MediaVideoRecordThread mediaVideoRecordThread = new MediaVideoRecordThread(mMediaVideoRecord, mDrawer, mResources, mListener);
        mediaVideoRecordThread.setWidthAndHeight(width, height);
        return mediaVideoRecordThread;

    }

    public static MediaVideoRecordThread start(Resources mResources, IDrawer mDrawer, EGLContext mEglContext, MediaVideoRecord.Listener mListener, int width, int height, MediaMuxer mediaMuxer, int textureCamera) throws IOException {

        MediaVideoRecord mMediaVideoRecord = new MediaVideoRecord(mResources, mEglContext, mListener, width, height, mediaMuxer);
        MediaVideoRecordThread mediaVideoRecordThread = new MediaVideoRecordThread(mMediaVideoRecord, mDrawer, mResources, mListener);
        mediaVideoRecordThread.setCameraProperties(width, height, textureCamera);
        return mediaVideoRecordThread;

    }

}


