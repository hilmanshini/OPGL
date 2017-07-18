package co.astrnt.medrec.medrec.tesst;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecordHandler;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;

/**
 * Created by hill on 7/11/17.
 */

public class TestAudio extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            new File("/sdcard/ee333.mp4").delete();
            final MediaMuxer mediaMuxer = new MediaMuxer("/sdcard/ee333.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            final MediaAudioRecordHandler mediaAudioRecordHandler = MediaAudioRecordHandler.start(new MediaAudioRecord.Listener() {
                @Override
                public void onFinish() {

                }

                @Override
                public boolean onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex) {
                    mediaMuxer.start();
                    return false;
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
            mediaAudioRecordHandler.Internalinit();
            LinearLayout mLinearLayout = new LinearLayout(this);
            final Looping l = new Looping(mediaAudioRecordHandler);
            l.start();
            setContentView(mLinearLayout);
            mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("DEQWRITE","STOPPING");
                    l.stop = true;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    class Looping extends Thread {
        MediaAudioRecordHandler mediaAudioRecordHandler;
        boolean stop = false;

        public Looping(MediaAudioRecordHandler mediaAudioRecordHandler) {
            this.mediaAudioRecordHandler = mediaAudioRecordHandler;
        }

        @Override
        public void run() {
            long startTIme = System.nanoTime();

            while (!stop) {
                Log.e("DEQWRITE","loop "+stop+" ");;
                long i = 600l;

                mediaAudioRecordHandler.getmMediaAudioRecord().encode(System.nanoTime() - startTIme, false);

            }
            mediaAudioRecordHandler.getmMediaAudioRecord().encode(System.nanoTime() - startTIme, true);

            mediaAudioRecordHandler.getmMediaAudioRecord().release();


        }
    }
}
