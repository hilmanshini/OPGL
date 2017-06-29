package co.astrnt.medrec.medrec.framework.mediacodec.decoder;

import android.media.MediaCodec;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by hill on 6/29/17.
 */

public class VideoDecoderThread extends Thread {
    VideoDecoder mVideoDecoder;

    public VideoDecoderThread(VideoDecoder mVideoDecoder) {
        this.mVideoDecoder = mVideoDecoder;
    }

    public boolean readSampleData() {
        int inputBufferIndex = mVideoDecoder.decoder.dequeueInputBuffer(10000);


        if (inputBufferIndex >= 0) {
            ByteBuffer[] allInputBuffers = mVideoDecoder.decoder.getInputBuffers();
            ByteBuffer activeBuffer = allInputBuffers[inputBufferIndex];
            int sampleSize = mVideoDecoder.mediaExtractor.readSampleData(activeBuffer, 0);
            Log.e("STARTOVER_VCAL", "SAMPLES " + mVideoDecoder.mediaExtractor.getSampleTime() + " " + sampleSize);
            if (sampleSize > 0) {

                mVideoDecoder.decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, mVideoDecoder.mediaExtractor.getSampleTime(), 0);
                mVideoDecoder.mediaExtractor.advance();
            } else {
                mVideoDecoder.decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                return true;
            }

        }
        return false;
    }

    protected MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
    protected long time;

    public long writeSampleData() {
        int outputBufferIndex = mVideoDecoder.decoder.dequeueOutputBuffer(bufferInfo, 0);
        if (outputBufferIndex >= 0) {
            mVideoDecoder.decoder.releaseOutputBuffer(outputBufferIndex, true);
            return time;
        }
        return -1;
    }

    boolean eos = false;

    @Override
    public void run() {
        super.run();
        while (!eos) {
            eos = readSampleData();
            writeSampleData();
        }
    }
}
