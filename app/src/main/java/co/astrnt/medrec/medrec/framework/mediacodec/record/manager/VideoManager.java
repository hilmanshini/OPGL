package co.astrnt.medrec.medrec.framework.mediacodec.record.manager;

import android.media.MediaCodec;
import android.media.MediaFormat;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecordHandler;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;

/**
 * Created by hill on 7/18/17.
 */

public class VideoManager implements MediaVideoRecordHandler.Listener {
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
        return null;
    }

    @Override
    public IDrawer getDrawerMediaCodecInitCamera(Object obj) {
        return null;
    }
}
