package co.astrnt.medrec.medrec.framework.mediacodec.decode.test2;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.Utils;

/**
 * Created by hill on 7/21/17.
 */

public class AudioEncoder {
    private static final long TIMEOUT_US = 10000;
    public static String MIME_TYPE = "audio/mp4a-latm";
    public static final int SAMPLE_RATE = 44100;
    private static final int BIT_RATE = 64000;
    private MediaCodec mMediaCodec;
    public static final int SAMPLES_PER_FRAME = 1024;    // AAC, bytes/frame/channel
    public static final int FRAMES_PER_BUFFER = 25;    // AAC, frame/buffer/sec
    MediaExtractor mediaExtractor;


    private MediaFormat newFormat;
    private boolean hasNewFormat;

    public boolean isHasNewFormat() {
        return hasNewFormat;
    }

    public MediaFormat getNewFormat() {
        return newFormat;
    }

    public int getTrackIndex() {
        return trackIndex;
    }

    MediaAudioRecord.Listener mListener;
    private int trackIndex;
    MediaMuxer mediaMuxer;

    public AudioEncoder(MediaExtractor mediaExtractor, MediaMuxer mediaMuxer, MediaAudioRecord.Listener mListener) {
        this.mediaExtractor = mediaExtractor;
        this.mediaMuxer = mediaMuxer;
        this.mListener = mListener;
        initAudio();
    }


    private void initAudio() {
        final MediaCodecInfo audioCodecInfo = Utils.selectAudioCodec(MIME_TYPE);
        if (audioCodecInfo == null) {
            throw new RuntimeException("No Audio Codec Found");

        }
        final MediaFormat audioFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE, 1);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialized " + MIME_TYPE + " CODEC");
        }
        mMediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();
        mListener.onPrepared(mMediaCodec);

    }

    public void encode( boolean eos) {
        long time = mediaExtractor.getSampleTime();
//        time = time / 1000l;
        ByteBuffer[] buffers = mMediaCodec.getInputBuffers();
        ByteBuffer[] outBuffers = mMediaCodec.getOutputBuffers();
        int index = mMediaCodec.dequeueInputBuffer(TIMEOUT_US);
        MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

        Log.e("DEQINPUT", " " + index);
        if (index >= 0) {
            ByteBuffer usedBuffer = buffers[index];
            usedBuffer.clear();
            int readBytes = mediaExtractor.readSampleData(usedBuffer, 0);
            if (!eos) {
                mMediaCodec.queueInputBuffer(index, 0, readBytes, time, 0);
            } else {
                mMediaCodec.queueInputBuffer(index, 0, readBytes, time, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            }
            mediaExtractor.advance();
        }
        boolean writeToMuxer = false;

        int status = 0;
        while (true) {
            status = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);

            Log.e(">>>>>>>>>>>>@@.DEQFLAG", " " + status + " " + mBufferInfo.presentationTimeUs);
            Log.e("DEQOUTPUT", " " + index + " " + eos);
            if (status == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!eos) {
                    break;
                }
            } else if (status == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outBuffers = mMediaCodec.getOutputBuffers();
            } else if (status == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                newFormat = mMediaCodec.getOutputFormat();
                trackIndex = mediaMuxer.addTrack(newFormat);
                Log.e("XGM"," "+newFormat);
                hasNewFormat = true;
                boolean breakLoop = mListener.onGetFormatToMuxer(newFormat, trackIndex);
                if (breakLoop) {
                    break;
                }
            } else if (status < 0) {
                // ??
            } else {
                ByteBuffer encodedBuffer = outBuffers[status];
                if (mBufferInfo.size != 0) {

                    encodedBuffer.position(mBufferInfo.offset);
                    mBufferInfo.presentationTimeUs = mediaExtractor.getSampleTime();
                    Log.e(">>>>>>>>>>>>>>.DEQFLAG1", " " + time + " " + eos + " " + TimeUnit.NANOSECONDS.toMillis(time) + " " + TimeUnit.NANOSECONDS.toMillis(mBufferInfo.presentationTimeUs));
                    encodedBuffer.limit(mBufferInfo.offset + mBufferInfo.size);
                    Log.e("DEQTIME", " " + TimeUnit.NANOSECONDS.toNanos(mBufferInfo.presentationTimeUs));
                    Log.e("DEQWRITE1", "WRITING " + mBufferInfo.size + " ptime: " + mBufferInfo.presentationTimeUs + " " + eos + " " + mBufferInfo.flags + " " + mBufferInfo.size + " ctime:" + time);
                    mediaMuxer.writeSampleData(trackIndex, encodedBuffer, mBufferInfo);
                }

                mMediaCodec.releaseOutputBuffer(status, false);
                break;
            }
            if (eos) {
                break;
            }
        }
        Log.e(">>>>>>>>>>>>.DEQFLAG", " " + time + " " + eos + " " + TimeUnit.NANOSECONDS.toMillis(time) + " " + status);

    }
}
