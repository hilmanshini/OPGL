package co.astrnt.medrec.medrec.framework.mediacodec.decode.test2;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import co.astrnt.medrec.medrec.framework.mediacodec.decode.MediaExtractorFactory;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoDecodeRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.Utils;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;

/**
 * Created by hill on 7/21/17.
 */

public class MyListener implements MediaVideoRecord.Listener {
    MediaMuxer mediaMuxer;
    MediaExtractorFactory.Result audioResult;
    MediaFormat newFormat;

    public MyListener(MediaMuxer mediaMuxer, MediaExtractorFactory.Result audioResult) {
        this.mediaMuxer = mediaMuxer;
        this.audioResult = audioResult;
        aTrack = mediaMuxer.addTrack(audioResult.format);
    }


    @Override
    public void onFinish(int track) {
        if (track == aTrack) {

        }
    }

    int aTrack = -1;
    int vTrack = -1;
    AudioEncoder audioEncoder;
    boolean started = false;
    int atrack;

    @Override
    public boolean onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex) {
        vTrack = mTrackIndex;
        mediaMuxer.start();
        return false;
    }

    @Override
    public void onPrepared(MediaCodec mMediaCodec) {

    }


boolean shouldDoAudio = true;
    @Override
    public void onWriteDataToMuxer(int mTrackIndex, boolean eos, MediaCodec.BufferInfo mBufferInfo) {

//        if(mTrackIndex == atrack){
//            Log.e("EVV", "START ENC " + eos + " " + mTrackIndex+" "+mBufferInfo.presentationTimeUs);
//            audioEncoder.encode(mBufferInfo.presentationTimeUs, eos, false);
//        }

        if(!eos && shouldDoAudio){
            writeTo(mBufferInfo.presentationTimeUs);
        }


    }

    ByteBuffer mByteBuffer = ByteBuffer.allocateDirect(2048);
    MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    private void writeTo(long caps) {
        while (audioResult.extractor.getSampleTime() < caps) {
            int bytesRead = audioResult.extractor.readSampleData(mByteBuffer, 0);

            mBufferInfo.presentationTimeUs = audioResult.extractor.getSampleTime();
            mBufferInfo.size = bytesRead;
            mBufferInfo.offset = 0;
            Log.e("WRITING ", " " + bytesRead + " " + mBufferInfo.flags + " " + mBufferInfo.presentationTimeUs + " " );

            mediaMuxer.writeSampleData(atrack, mByteBuffer, mBufferInfo);
            audioResult.extractor.advance();
        }

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


}
