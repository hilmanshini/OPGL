package co.astrnt.medrec.medrec.framework.mediacodec.record.manager.test;

import android.content.res.Resources;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecordThread;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.manager.CustomDrawer;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;

/**
 * Created by hill on 7/20/17.
 */

public class TestAudioVideoListener implements MediaAudioRecord.Listener, MediaVideoRecord.Listener {
    MediaMuxer mediaMuxer;
    Resources mResources;

    public TestAudioVideoListener(MediaMuxer mediaMuxer, Resources mResources) {
        this.mediaMuxer = mediaMuxer;
        this.mResources = mResources;
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

    boolean audioDone, videoDone;

    @Override
    public void onFinish(int track) {
        if (track == aTrack) {
            log("audio done");
            audioDone = true;
        }
        if (track == vTrack) {
            log("video done");
            videoDone = true;
        }
        if (audioDone && videoDone) {
            log("both done");
            mediaMuxer.release();
        }

    }

    private void log(String s) {
        Log.e("AVListener",">"+s);
    }

    int vTrack = -1, aTrack = -1;

    @Override
    public boolean onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex) {
        if (newFormat.getString(MediaFormat.KEY_MIME).startsWith("video")) {
            vTrack = mTrackIndex;
            log("get new vformat");
            if (aTrack != -1) {
                log("start mux");
                mediaMuxer.start();
            }
        } else if (newFormat.getString(MediaFormat.KEY_MIME).startsWith("audio")) {
            aTrack = mTrackIndex;
            log("get new aformat");
            if (vTrack != -1) {
                log("start mux");
                mediaMuxer.start();
            }
        }
        synchronized (waitLock){
            waitLock.notify();
        }
        return true;
    }

    @Override
    public void onPrepared(MediaCodec mMediaCodec) {

    }

    @Override
    public void onWriteDataToMuxer(int mTrackIndex, boolean eos, MediaCodec.BufferInfo mBufferInfo) {
        if(mTrackIndex ==aTrack){
            log("write audio "+mBufferInfo.presentationTimeUs+" "+mBufferInfo.size+" "+eos);
        } else if(mTrackIndex == vTrack){
            log("write video "+mBufferInfo.presentationTimeUs+" "+mBufferInfo.size+" "+eos);
        }
    }

    Object waitLock = new Object();

    @Override
    public void waitForInit() {
        while (vTrack == -1 || aTrack == -1) {
            log("waiting for ready");
            synchronized (waitLock) {
                try {
                    waitLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        log("waiting for ready done");
    }
}
