package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.media.MediaCodec;
import android.media.MediaFormat;

/**
 * Created by hill on 7/10/17.
 */

public interface Listener {
    void onFinish();

    void onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex);

    void onPrepared(MediaCodec mMediaCodec);

    void onWriteDataToMuxer(int mTrackIndex, boolean eos, MediaCodec.BufferInfo mBufferInfo);

}