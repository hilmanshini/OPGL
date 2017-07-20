package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.content.res.Resources;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import co.astrnt.medrec.medrec.framework.opengl.IDrawer;
import co.astrnt.medrec.medrec.framework.opengl.v2.Drawer;
import imageogl.view.opengl_x.mediacodec.VideoCodec;

/**
 * Created by hill on 7/10/17.
 */

public class MediaVideoRecordHandler extends Handler {
    private int frame;
    private MediaVideoRecord mMediaVideoRecord;
    private IDrawer mDrawer;
    Resources mResources;
    MediaVideoRecord.Listener mListener;
    public HandlerThread thread;

    public MediaVideoRecordHandler(Looper looper, MediaVideoRecord mMediaVideoRecord, IDrawer mDrawer, Resources mResources, MediaVideoRecord.Listener mListener) {
        super(looper);
        this.mListener = mListener;
        this.mMediaVideoRecord = mMediaVideoRecord;
        this.mDrawer = mDrawer;
        this.mResources = mResources;
    }

    public static CompressedMediaVideoRecordHandler start(Resources mResources, IDrawer mDrawer, MediaVideoRecord.Listener mListener, int width, int height, MediaMuxer mediaMuxer) throws IOException {
        HandlerThread handlerThread = new HandlerThread("VCodec");
        handlerThread.start();
        MediaVideoRecord mMediaVideoRecord = new MediaVideoRecord(mResources, mListener, width, height, mediaMuxer);
        CompressedMediaVideoRecordHandler self = new CompressedMediaVideoRecordHandler(handlerThread.getLooper(), mMediaVideoRecord, mDrawer, mResources, mListener);
        return self;
    }

    public static CompressedMediaVideoRecordHandler start(Resources mResources, IDrawer mDrawer, EGLContext eglContext, MediaVideoRecord.Listener mListener, int width, int height, MediaMuxer mediaMuxer) throws IOException {
        HandlerThread handlerThread = new HandlerThread("VCodec");
        handlerThread.start();
        MediaVideoRecord mMediaVideoRecord = new MediaVideoRecord(mResources, eglContext, mListener, width, height, mediaMuxer);

        CompressedMediaVideoRecordHandler self = new CompressedMediaVideoRecordHandler(handlerThread.getLooper(), mMediaVideoRecord, mDrawer, mResources, mListener);
        self.thread = handlerThread;
        return self;
    }

    long startTime = -1;


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if(mMediaVideoRecord.stop){
            return;
        }
        if (msg != null) {
            if (msg.what == VideoCodec.DRAW_FRAME) {
                long time = 0;
                if (startTime == -1) {
                    startTime = System.nanoTime();
                } else {
                    time = System.nanoTime() - startTime;
                }
                if (isValidTiming(time, startTime)) {
                    float[] xy = (float[]) msg.obj;
                    Log.e("VideoThread", "handleMessage: vidtime: " + time);
                    mMediaVideoRecord.drain(false);
                    mDrawer.clear();
                    mDrawer.draw((float) xy[0], (float) xy[1]);


                    mMediaVideoRecord.swapDisplay(time);
                    frame++;
                }


            } else if (msg.what == VideoCodec.TERMINATE) {
                mMediaVideoRecord.drain(true);
                mMediaVideoRecord.release();
                mDrawer.release();
            } else if (msg.what == VideoCodec.INIT) {
                mMediaVideoRecord.makeCurrent();
                int[] xy = (int[]) msg.obj;
//                mDrawer = new Drawer(mResources);
                mDrawer = mListener.getDrawerMediaCodecInit();
                mDrawer.onSurfaceChanged(xy[0], xy[1]);
            } else if (msg.what == VideoCodec.INIT_CAMERA) {
                mMediaVideoRecord.makeCurrent();
                int[] xy = (int[]) msg.obj;
                mDrawer = mListener.getDrawerMediaCodecInitCamera(msg.obj);
                mDrawer.onSurfaceChanged(xy[0], xy[1]);
            } else if (msg.what == VideoCodec.SAMPLING) {
                mMediaVideoRecord.sampling();
            }
        }
    }

    protected boolean isValidTiming(long time, long startTime) {
        return true;
    }

    public void sendMessage(int msg, Object object) {
        Message message = new Message();
        message.what = msg;
        message.obj = object;
        sendMessage(message);
    }



    public void initCamera(int width, int height, int textureCamera) {
        sendMessage(VideoCodec.INIT_CAMERA, new int[]{width, height, textureCamera});
    }

    public void drawFrame(float x, float y) {
        sendMessage(VideoCodec.DRAW_FRAME, new float[]{x, y});
    }

    public void terminate() {
        mMediaVideoRecord.stop();
        if (thread != null) {
            thread.interrupt();
        }

    }

}
