package co.astrnt.medrec.medrec.framework.mediacodec.record.manager.test;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecord;

/**
 * Created by hill on 7/20/17.
 */

public class TestAudioListener implements MediaAudioRecord.Listener {
    MediaMuxer mediaMuxer;

    public TestAudioListener(MediaMuxer mediaMuxer) {
        this.mediaMuxer = mediaMuxer;
    }

    @Override
    public void onFinish(int track) {
        mediaMuxer.release();
    }

    @Override
    public boolean onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex) {
        mediaMuxer.start();
        return true;
    }

    @Override
    public void onPrepared(MediaCodec mMediaCodec) {

    }

    @Override
    public void onWriteDataToMuxer(int mTrackIndex, boolean eos, MediaCodec.BufferInfo mBufferInfo) {

    }

    @Override
    public void waitForInit() {

    }
}
