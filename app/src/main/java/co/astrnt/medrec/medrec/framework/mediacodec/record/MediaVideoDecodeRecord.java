package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.content.res.Resources;
import android.media.MediaMuxer;
import android.opengl.EGLContext;
import android.util.Log;

import java.io.IOException;

import co.astrnt.medrec.medrec.framework.opengl.IDrawer;

/**
 * Created by hill on 7/21/17.
 */

public class MediaVideoDecodeRecord {
    public MediaVideoRecord mMediaVideoRecord;
    public IDrawer mDrawer;
    public Resources mResources;
    public MediaVideoRecord.Listener mListener;
    int width;
    int height;

    public MediaVideoDecodeRecord(MediaVideoRecord mMediaVideoRecord, IDrawer mDrawer, Resources mResources, MediaVideoRecord.Listener mListener, int width, int height) {
        this.mMediaVideoRecord = mMediaVideoRecord;
        this.mDrawer = mDrawer;
        this.mResources = mResources;
        this.mListener = mListener;
        this.width = width;
        this.height = height;
    }

    public void init() {
        mMediaVideoRecord.makeCurrent();
        mDrawer.onSurfaceChanged(width, height);

    }

    public void drawFrame(long time) {
        Log.e("VideoThread", "handleMessage: vidtime: " + time);
        mMediaVideoRecord.drain(false);
        mDrawer.clear();
        mDrawer.draw(0, 0);
        mMediaVideoRecord.swapDisplay(time);

    }
    public void sampling() {
        Log.e("VideoThread", "sampling");
        while (!mMediaVideoRecord.hasNewFormat) {
            drawFrame(0);
        }

        Log.e("VideoThread", "sampling done");
    }
    public void terminate() {
        Log.e("VideoThread", "terminating");
        mMediaVideoRecord.drain(true);
        mMediaVideoRecord.release();
        mDrawer.release();
        Log.e("VideoThread", "terminating done");
        mListener.onFinish(mMediaVideoRecord.getmTrackIndex());
    }
    public static MediaVideoDecodeRecord start(Resources mResources, IDrawer mDrawer, MediaVideoRecord.Listener mListener, int width, int height, MediaMuxer mediaMuxer) throws IOException {
        MediaVideoRecord mMediaVideoRecord = new MediaVideoRecord(mResources, mListener, width, height, mediaMuxer);
        MediaVideoDecodeRecord mediaVideoDecodeRecord = new MediaVideoDecodeRecord(mMediaVideoRecord,mDrawer,mResources,mListener,width,height);
        return mediaVideoDecodeRecord;

    }
}
