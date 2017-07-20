package co.astrnt.medrec.medrec.framework.mediacodec.record.manager.test;

import android.content.res.Resources;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecordHandler;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecord.Listener;
import co.astrnt.medrec.medrec.framework.mediacodec.record.manager.CustomDrawer;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;

/**
 * Created by hill on 7/20/17.
 */

public class TestLIstener implements Listener {
    MediaMuxer mediaMuxer;
    Resources mResources;

    public TestLIstener(MediaMuxer mediaMuxer, Resources mResources) {
        this.mediaMuxer = mediaMuxer;
        this.mResources = mResources;
    }

    @Override
    public void onFinish(int track) {
        Log.e("VListener","finish "+track);
    }
    int trackIndex = -1;
    @Override
    public boolean onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex) {
        Log.e("VListener","new format  "+newFormat+" #"+trackIndex);
        trackIndex = mTrackIndex;
        return true;
    }

    @Override
    public void onPrepared(MediaCodec mMediaCodec) {
        Log.e("VListener","prepared  "+mMediaCodec);
    }

    @Override
    public void onWriteDataToMuxer(int mTrackIndex, boolean eos, MediaCodec.BufferInfo mBufferInfo) {
        Log.e("VListener",">time:"+mBufferInfo.presentationTimeUs);
    }

    @Override
    public IDrawer getDrawerMediaCodecInit() {
        CustomDrawer mCustomDrawer = new CustomDrawer(mResources);
        mCustomDrawer.onSurfaceCreated();
        return mCustomDrawer;
    }

    @Override
    public IDrawer getDrawerMediaCodecInitCamera(Object obj) {
        CustomDrawer mCustomDrawer = new CustomDrawer(mResources, ((int[]) obj)[2]);
        mCustomDrawer.onSurfaceCreated();
        return mCustomDrawer;
    }

    @Override
    public void waitForInit() {

    }
    private void log(String s) {
        Log.e("AVListener",">"+s);
    }

}
