package co.astrnt.medrec.medrec.framework.mediacodec.record.manager;

import android.content.res.Resources;
import android.media.MediaCodec;
import android.media.MediaFormat;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecordHandler;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecordHandler;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;

/**
 * Created by hill on 7/18/17.
 */

public class EventListener implements MediaAudioRecord.Listener, MediaVideoRecordHandler.Listener  {
    Resources mResources;

    public EventListener(Resources mResources) {
        this.mResources = mResources;
    }

    @Override
    public void onFinish() {

    }

    @Override
    public boolean onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex) {

        return false;
    }

    @Override
    public void onPrepared(MediaCodec mMediaCodec) {

    }

    @Override
    public void onWriteDataToMuxer(int mTrackIndex, boolean eos, MediaCodec.BufferInfo mBufferInfo) {

    }

    @Override
    public IDrawer getDrawerMediaCodecInit() {
        CustomDrawer mCustomDrawer = new CustomDrawer(mResources);
        mCustomDrawer.onSurfaceCreated();
        return mCustomDrawer;
    }

    @Override
    public IDrawer getDrawerMediaCodecInitCamera(Object obj) {
        CustomDrawer mCustomDrawer = new CustomDrawer(mResources,((int[]) obj)[2]);
        mCustomDrawer.onSurfaceCreated();
        return mCustomDrawer;
    }
}
