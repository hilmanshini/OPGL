package co.astrnt.medrec.medrec.framework.mediacodec.decode;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by hill on 7/21/17.
 */

public class MediaVideoDecoder {
    private MediaFormat format;
    private Surface surface;
    private MediaCodec decoder;

    MediaExtractor mediaExtractor;

    public MediaVideoDecoder(MediaFormat format, Surface surface, MediaExtractor mediaExtractor) {
        this.mediaExtractor = mediaExtractor;
        this.format = format;
        this.surface = surface;
        try {
            decoder = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME));
            decoder.configure(format, surface, null, 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        decoder.start();
    }

    private int inputBufferIndex;

    public boolean readSampleData() {
        Log.e("VIDTIME_READ", "READING SAMPLES");
        inputBufferIndex = decoder.dequeueInputBuffer(10000);
        if (inputBufferIndex >= 0) {
            ByteBuffer[] allInputBuffers = decoder.getInputBuffers();
            ByteBuffer activeBuffer = allInputBuffers[inputBufferIndex];
            int sampleSize = mediaExtractor.readSampleData(activeBuffer, 0);
            Log.e("STARTOVER_VCAL", "SAMPLES " + mediaExtractor.getSampleTime() + " " + sampleSize);
            if (sampleSize > 0) {

                decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, mediaExtractor.getSampleTime(), 0);
                mediaExtractor.advance();
            } else {
                decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                return true;
            }

        }
        return false;
    }

    protected MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

    public void writeSampleData() {
        int outputBufferIndex = decoder.dequeueOutputBuffer(bufferInfo, 0);
        Log.e("MCODEC"," "+outputBufferIndex);
        if (outputBufferIndex >= 0) {
            ByteBuffer[] allOutputBuffer = decoder.getOutputBuffers();
            decoder.releaseOutputBuffer(outputBufferIndex, true);
        }

    }
    public long getSampleTime(){
        return mediaExtractor.getSampleTime();
    }
}
