package co.astrnt.medrec.medrec.framework.mediacodec.decode;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.IOException;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoDecodeRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecord;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;

/**
 * Created by hill on 7/21/17.
 */

public class TestDecEncode extends Activity {
    private MediaVideoRecord.Listener mListener = new MediaVideoRecord.Listener() {
        @Override
        public void onFinish(int track) {

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

        @Override
        public void waitForInit() {

        }
    };
    MediaMuxer mediaMuxer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mediaMuxer = new MediaMuxer("/sdcard/y.mp4",MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            MediaVideoDecodeRecord mediaVideoDecodeRecord = MediaVideoDecodeRecord.start(getResources(),new Renderer(),mListener,1024,768,mediaMuxer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    class Renderer implements IDrawer{

        @Override
        public void onSurfaceChanged(int width, int height) {

        }

        @Override
        public void draw(Object... params) {

        }

        @Override
        public void clear() {

        }

        @Override
        public void onSurfaceCreated() {

        }

        @Override
        public void release() {

        }
    }
}
