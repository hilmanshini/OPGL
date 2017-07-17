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
    Listener mListener;
    public MediaVideoRecordHandler(Looper looper, MediaVideoRecord mMediaVideoRecord, IDrawer mDrawer, Resources mResources, Listener mListener) {
        super(looper);
        this.mListener = mListener;
        this.mMediaVideoRecord = mMediaVideoRecord;
        this.mDrawer = mDrawer;
        this.mResources = mResources;
    }

    public static CompressedMediaVideoRecordHandler start(Resources mResources, IDrawer mDrawer, Listener mListener, int width, int height, MediaMuxer mediaMuxer) throws IOException {
        HandlerThread handlerThread = new HandlerThread("VCodec");
        handlerThread.start();
        MediaVideoRecord mMediaVideoRecord = new MediaVideoRecord(mResources, mListener, width, height, mediaMuxer);
        CompressedMediaVideoRecordHandler self = new CompressedMediaVideoRecordHandler(handlerThread.getLooper(), mMediaVideoRecord, mDrawer, mResources,mListener);
        return self;
    }

    public static CompressedMediaVideoRecordHandler start(Resources mResources, IDrawer mDrawer, EGLContext eglContext, Listener mListener, int width, int height, MediaMuxer mediaMuxer) throws IOException {
        HandlerThread handlerThread = new HandlerThread("VCodec");
        handlerThread.start();
        MediaVideoRecord mMediaVideoRecord = new MediaVideoRecord(mResources, eglContext, mListener, width, height, mediaMuxer);
        CompressedMediaVideoRecordHandler self = new CompressedMediaVideoRecordHandler(handlerThread.getLooper(), mMediaVideoRecord, mDrawer, mResources, mListener);
        return self;
    }

    long startTime = -1;


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
                if(isValidTiming(time,startTime)){
                    float[] xy = (float[]) msg.obj;
                    mMediaVideoRecord.drain(false);
                    mDrawer.clear();
                    mDrawer.draw((float) xy[0], (float) xy[1]);


                    mMediaVideoRecord.swapDisplay(time);
                    frame++;
                }



            } else if (msg.what == VideoCodec.TERMINATE) {
                mMediaVideoRecord.drain(true);
                mMediaVideoRecord.release();
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
    public interface Listener {
        void onFinish();

        void onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex);

        void onPrepared(MediaCodec mMediaCodec);

        void onWriteDataToMuxer(int mTrackIndex, boolean eos, MediaCodec.BufferInfo mBufferInfo);

        IDrawer getDrawerMediaCodecInit();

        IDrawer getDrawerMediaCodecInitCamera(Object obj);

    }
}
