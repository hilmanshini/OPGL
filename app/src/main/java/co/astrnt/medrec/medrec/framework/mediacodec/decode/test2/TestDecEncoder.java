package co.astrnt.medrec.medrec.framework.mediacodec.decode.test2;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import co.astrnt.medrec.medrec.framework.mediacodec.decode.MediaExtractorFactory;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoDecodeRecord;

/**
 * Created by hill on 7/21/17.
 */

public class TestDecEncoder extends Activity {
    MyListener mMyListener;
    MyDrawer mMyDrawer;
    MediaMuxer mMediaMuxer;
    ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        testVideo();
//            q.encode(0,true,false);
    }

    private void testAudio() {
        try {

            mMediaMuxer = new MediaMuxer("/sdcard/q12.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            MediaExtractorFactory.Result audioResult = MediaExtractorFactory.getExtractorAudio("/sdcard/test_out.mp4");


            ByteBuffer mByteBuffer = ByteBuffer.allocateDirect(2048);

            int tindex = mMediaMuxer.addTrack(audioResult.format);
            mMediaMuxer.start();
            MediaFormat mediaFormat = audioResult.extractor.getTrackFormat(tindex);
            long duration = mediaFormat.getLong(MediaFormat.KEY_DURATION);
            int bytesRead = 0;
            boolean hasData = true;
            MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
            while (true) {
                bytesRead = audioResult.extractor.readSampleData(mByteBuffer, 0);

                mBufferInfo.presentationTimeUs = audioResult.extractor.getSampleTime();
                //mBufferInfo.flags = audioResult.extractor.getSampleFlags();

                mBufferInfo.offset = 0;
                mBufferInfo.size = bytesRead;
                mBufferInfo.offset = 0;
                Log.e("WRITING ", " " + bytesRead + " " + mBufferInfo.flags + " " + mBufferInfo.presentationTimeUs + " " + duration);
                mMediaMuxer.writeSampleData(tindex, mByteBuffer, mBufferInfo);
                audioResult.extractor.advance();
                if (mBufferInfo.presentationTimeUs >= duration) {
                    break;
                }

            }
            mMediaMuxer.stop();
            mMediaMuxer.release();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void testVideo() {
        long from = 2000;
        long to = 6000;
        try {
            new File("/sdcard/q.mp4").delete();
            mMediaMuxer = new MediaMuxer("/sdcard/q.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            MediaExtractorFactory.Result audioResult = MediaExtractorFactory.getExtractorAudio("/sdcard/test_out.mp4");


            mMyListener = new MyListener(mMediaMuxer, audioResult);
            mMyDrawer = new MyDrawer("/sdcard/test_out.mp4", getResources());
            MediaVideoDecodeRecord mediaVideoDecodeRecord = MediaVideoDecodeRecord.start(getResources(), mMyDrawer, mMyListener, 1024, 768, mMediaMuxer);
            mediaVideoDecodeRecord.init();
            mediaVideoDecodeRecord.sampling();
            int i = 30;
            boolean eos = false;
            long lfrom = from * 1000000;
            long lto = to * 1000000;
            long secondsFade = 1;
            long ctime = 0;
            while (!eos) {
                eos = mMyDrawer.feed();
                mMyDrawer.draw();


                ctime = mMyDrawer.mVideo.getSampleTime() * 1000;
                Log.e("XTIME", " " + ctime + " " + lfrom + " " + lto + "  " + (ctime / 1000000) + " " + (lfrom / 1000000) + " " + (lto / 1000000) + " " + to);
                if (ctime > lfrom && ctime < lto) {

                    mediaVideoDecodeRecord.drawFrame((mMyDrawer.mVideo.getSampleTime() * 1000) - lfrom);
                    float factor = 0;
                    if (ctime >= (lto - (secondsFade * 1000000000))) {
                        factor = (float) ((double)(ctime - (lto - (secondsFade * 1000000000))) / (double)1000000000);
                        mMyDrawer.updateFadeOut1stStep(factor);

                    }
                    Log.e("XTIME", " " + (ctime - from) + " " + factor+" "+(ctime-(lto - (secondsFade * 1000000000))));
                }
                if (ctime > lto) {
                    break;
                }


            }
            long e = 10;
            mMyListener.shouldDoAudio = false;
            mMyDrawer.setPhase(1);
            for (long i1 = 0; i1 < 4; i1++) {
                mMyDrawer.draw();
                mediaVideoDecodeRecord.drawFrame(ctime+e-lfrom);
                e += 1000000000;
            }


            mediaVideoDecodeRecord.terminate();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
