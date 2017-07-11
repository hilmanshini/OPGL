package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * Created by hill on 7/11/17.
 */

public class TestAudio extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            final MediaMuxer mediaMuxer = new MediaMuxer("/sdcard/ee333.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            MediaAudioRecordHandler mediaAudioRecordHandler = MediaAudioRecordHandler.start(new Listener() {
                @Override
                public void onFinish() {

                }

                @Override
                public void onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex) {
                    mediaMuxer.start();
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
            }, mediaMuxer);
            long i = 500l;
            long startTIme = System.nanoTime();
            mediaAudioRecordHandler.sendEmptyMessage(MediaAudioRecordHandler.INIT);
            for (int i1 = 0; i1 < i; i1++) {
                Object[] data = new Object[]{
                        System.nanoTime() - startTIme, false
                };
                mediaAudioRecordHandler.sendMessage(mediaAudioRecordHandler.obtainMessage(MediaAudioRecordHandler.CAPTURE, data));
            }
            Object[] data = new Object[]{
                    System.nanoTime() - startTIme, true
            };
            mediaAudioRecordHandler.sendMessage(mediaAudioRecordHandler.obtainMessage(MediaAudioRecordHandler.CAPTURE, data));
            mediaAudioRecordHandler.sendEmptyMessage(MediaAudioRecordHandler.TERMINATE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
