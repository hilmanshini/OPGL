package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.content.res.Resources;
import android.media.MediaMuxer;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.astrnt.medrec.medrec.framework.opengl.v2.Drawer;
import imageogl.view.opengl_x.mediacodec.VideoCodec;

/**
 * Created by hill on 7/10/17.
 */

public class MediaVideoRecordHandler extends Handler {
    private int frame;
    private MediaVideoRecord mMediaVideoRecord;
    private Drawer mDrawer;
    Resources mResources;

    public MediaVideoRecordHandler(Looper looper, MediaVideoRecord mMediaVideoRecord, Drawer mDrawer, Resources mResources) {
        super(looper);
        this.mMediaVideoRecord = mMediaVideoRecord;
        this.mDrawer = mDrawer;
        this.mResources = mResources;
    }

    public static MediaVideoRecordHandler start(Resources mResources, Drawer mDrawer, Listener mListener, int width, int height, MediaMuxer mediaMuxer) throws IOException {
        HandlerThread handlerThread = new HandlerThread("VCodec");
        handlerThread.start();
        MediaVideoRecord mMediaVideoRecord = new MediaVideoRecord(mResources, mListener, width, height, mediaMuxer);
        MediaVideoRecordHandler self = new MediaVideoRecordHandler(handlerThread.getLooper(), mMediaVideoRecord, mDrawer, mResources);
        return self;
    }

    public static MediaVideoRecordHandler start(Resources mResources, Drawer mDrawer, EGLContext eglContext, Listener mListener, int width, int height, MediaMuxer mediaMuxer) throws IOException {
        HandlerThread handlerThread = new HandlerThread("VCodec");
        handlerThread.start();
        MediaVideoRecord mMediaVideoRecord = new MediaVideoRecord(mResources, eglContext, mListener, width, height, mediaMuxer);
        MediaVideoRecordHandler self = new MediaVideoRecordHandler(handlerThread.getLooper(), mMediaVideoRecord, mDrawer, mResources);
        return self;
    }

    long startTime = -1;
    int currentTime = 0;
    int currentTimeCoumt = 0;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg != null) {
            if (msg.what == VideoCodec.DRAW_FRAME) {
                long time = 0;
                if (startTime == -1) {
                    startTime = System.nanoTime();
                } else {
                    time = System.nanoTime() - startTime;
                }
                long secTime = TimeUnit.NANOSECONDS.toSeconds(time);
                if (secTime == currentTime) {

                    currentTimeCoumt++;
                    if (currentTimeCoumt > 5) {
                        currentTime++;
                        currentTimeCoumt = 0;
                    }

                    Log.e("FRAMEBLIT", " " + currentTime+" "+currentTimeCoumt);

                }
                float[] xy = (float[]) msg.obj;
                mMediaVideoRecord.drain(false);
                mDrawer.clear();
                mDrawer.draw((float) xy[0], (float) xy[1]);


                mMediaVideoRecord.swapDisplay(time);
                frame++;


            } else if (msg.what == VideoCodec.TERMINATE) {
                mMediaVideoRecord.drain(true);
                mMediaVideoRecord.release();
            } else if (msg.what == VideoCodec.INIT) {
                mMediaVideoRecord.makeCurrent();
                int[] xy = (int[]) msg.obj;
                mDrawer = new Drawer(mResources);
                mDrawer.onSurfaceChanged(xy[0], xy[1]);
            } else if (msg.what == VideoCodec.INIT_CAMERA) {
                mMediaVideoRecord.makeCurrent();
                int[] xy = (int[]) msg.obj;
                mDrawer = new Drawer(mResources, ((int[]) msg.obj)[2]);
                mDrawer.onSurfaceChanged(xy[0], xy[1]);
            }
        }
    }

    public void sendMessage(int msg, Object object) {
        Message message = new Message();
        message.what = msg;
        message.obj = object;
        sendMessage(message);
    }

}
