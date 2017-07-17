package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecordHandler.Listener;

/**
 * Created by hill on 7/11/17.
 */

public class MediaAudioRecord {
    private static final long TIMEOUT_US = 10000;
    public static String MIME_TYPE = "audio/mp4a-latm";
    public static final int SAMPLE_RATE = 44100;
    private static final int BIT_RATE = 64000;
    private final MediaCodec mMediaCodec;
    public static final int SAMPLES_PER_FRAME = 1024;    // AAC, bytes/frame/channel
    public static final int FRAMES_PER_BUFFER = 25;    // AAC, frame/buffer/sec
    private Listener mListener;
    private AudioRecord audioRecord = null;
    private MediaMuxer mediaMuxer;

    public MediaAudioRecord(Listener mListener, MediaMuxer mediaMuxer) {
        this.mListener = mListener;
        this.mediaMuxer = mediaMuxer;
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
        final int min_buffer_size = AudioRecord.getMinBufferSize(
                SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        int buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
        if (buffer_size < min_buffer_size)
            buffer_size = ((min_buffer_size / SAMPLES_PER_FRAME) + 1) * SAMPLES_PER_FRAME * 2;


        for (final int source : AUDIO_SOURCES) {
            try {
                audioRecord = new AudioRecord(
                        source, SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffer_size);
                if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED)
                    audioRecord = null;
            } catch (final Exception e) {
                audioRecord = null;
            }
            if (audioRecord != null) break;
        }
        bufferRecord = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
        int readBytes;
        audioRecord.startRecording();
    }

    final ByteBuffer bufferRecord;

    private static final int[] AUDIO_SOURCES = new int[]{
            MediaRecorder.AudioSource.MIC,
            MediaRecorder.AudioSource.DEFAULT,
            MediaRecorder.AudioSource.CAMCORDER,
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
    };
    int trackIndex;

    public void encode(long time, boolean eos) {
        Log.e(">>>>>>>>>>>>.DEQFLAG", " " + time + " " + eos);
        ByteBuffer[] buffers = mMediaCodec.getInputBuffers();
        ByteBuffer[] outBuffers = mMediaCodec.getOutputBuffers();
        int index = mMediaCodec.dequeueInputBuffer(TIMEOUT_US);
        MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

        Log.e("DEQINPUT", " " + index);
        if (index >= 0) {
            ByteBuffer usedBuffer = buffers[index];
            usedBuffer.clear();
            bufferRecord.clear();

            int readBytes = audioRecord.read(bufferRecord, SAMPLES_PER_FRAME);

            // set audio data to encoder
            Log.e("AUDIORX", "READ = " + readBytes);
            bufferRecord.position(readBytes);
            bufferRecord.flip();
            usedBuffer.put(bufferRecord);
            if (!eos) {
                mMediaCodec.queueInputBuffer(index, 0, readBytes, time, 0);
            } else {
                mMediaCodec.queueInputBuffer(index, 0, readBytes, time, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            }
        }

        int status = 0;
        while (true) {
            status = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);
            Log.e("DEQOUTPUT", " " + index);
            if (status == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!eos) {
                    break;
                }
            } else if (status == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                outBuffers = mMediaCodec.getOutputBuffers();
            } else if (status == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat newFormat = mMediaCodec.getOutputFormat();
                trackIndex = mediaMuxer.addTrack(newFormat);
                mListener.onGetFormatToMuxer(newFormat, trackIndex);
            } else if (status < 0) {
                // ??
            } else {
                ByteBuffer encodedBuffer = outBuffers[status];
                if (mBufferInfo.size != 0) {
                    encodedBuffer.position(mBufferInfo.offset);

                    encodedBuffer.limit(mBufferInfo.offset + mBufferInfo.size);
                    Log.e("DEQWRITE", "WRITING " + mBufferInfo.size + " " + mBufferInfo.presentationTimeUs + " " + eos + " " + mBufferInfo.flags);
                    mediaMuxer.writeSampleData(trackIndex, encodedBuffer, mBufferInfo);
                    mListener.onWriteDataToMuxer(trackIndex, eos, mBufferInfo);
                }
                mMediaCodec.releaseOutputBuffer(status, false);
            }
            if (eos) {
                break;
            }
        }


    }


    public void release() {
        if (audioRecord != null) {
            audioRecord.release();
        }
        if (mMediaCodec != null) {
            mMediaCodec.release();
        }


    }


}
