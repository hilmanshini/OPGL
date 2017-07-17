package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.content.res.Resources;
import android.media.MediaMuxer;
import android.opengl.EGLContext;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.astrnt.medrec.medrec.framework.opengl.IDrawer;
import co.astrnt.medrec.medrec.framework.opengl.v2.Drawer;

/**
 * Created by hill on 7/12/17.
 */

public class CompressedMediaVideoRecordHandler extends MediaVideoRecordHandler {

    public CompressedMediaVideoRecordHandler(Looper looper, MediaVideoRecord mMediaVideoRecord, IDrawer mDrawer, Resources mResources, Listener mListener) {
        super(looper, mMediaVideoRecord, mDrawer, mResources, mListener);
    }


    public static CompressedMediaVideoRecordHandler start(Resources mResources, Drawer mDrawer, Listener mListener, int width, int height, MediaMuxer mediaMuxer) throws IOException {
        HandlerThread handlerThread = new HandlerThread("VCodec");
        handlerThread.start();
        MediaVideoRecord mMediaVideoRecord = new MediaVideoRecord(mResources, mListener, width, height, mediaMuxer);
        CompressedMediaVideoRecordHandler self = new CompressedMediaVideoRecordHandler(handlerThread.getLooper(), mMediaVideoRecord, mDrawer, mResources,mListener);
        return self;
    }

    public static CompressedMediaVideoRecordHandler start(Resources mResources, Drawer mDrawer, EGLContext eglContext, Listener mListener, int width, int height, MediaMuxer mediaMuxer) throws IOException {
        HandlerThread handlerThread = new HandlerThread("VCodec");
        handlerThread.start();
        MediaVideoRecord mMediaVideoRecord = new MediaVideoRecord(mResources, eglContext, mListener, width, height, mediaMuxer);
        CompressedMediaVideoRecordHandler self = new CompressedMediaVideoRecordHandler(handlerThread.getLooper(), mMediaVideoRecord, mDrawer, mResources,mListener);
        return self;
    }

    long lastValidTime = -1;
    int frameRate = 5;
    long lastCurrentSeconds = 0;
    long lastCurrentSecondsTime = 0;

    @Override
    protected boolean isValidTiming(long time, long startTime) {
        long currentSeconds = TimeUnit.NANOSECONDS.toSeconds(time);
        long oneNanos = TimeUnit.SECONDS.toNanos(1);
        if (currentSeconds == lastCurrentSeconds) {
            lastCurrentSecondsTime++;
            if(lastCurrentSecondsTime == (frameRate+1) ){
                lastCurrentSecondsTime = 1;
                lastCurrentSeconds++;
            }
            Log.e("TIMING", "CURRENT SECONDS TIME = " + lastCurrentSeconds+" "+lastCurrentSecondsTime+" currentTime="+time+" nextTargetTime="+((oneNanos*lastCurrentSeconds)+((oneNanos/frameRate)*lastCurrentSecondsTime)));

        }

        return true;
    }
}
