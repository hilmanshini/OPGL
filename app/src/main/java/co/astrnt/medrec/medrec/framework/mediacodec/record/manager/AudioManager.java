package co.astrnt.medrec.medrec.framework.mediacodec.record.manager;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecordHandler;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;

/**
 * Created by hill on 7/18/17.
 */

public class AudioManager implements MediaAudioRecord.Listener {
    MediaMuxer mediaMuxer;

    public AudioManager(MediaMuxer mediaMuxer) {
        this.mediaMuxer = mediaMuxer;
    }

    @Override
    public void onFinish() {

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
        if (eos) {
            mediaMuxer.stop();
            mediaMuxer.release();
        }
    }
}
