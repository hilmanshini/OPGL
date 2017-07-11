package imageogl.view.opengl_x.mediacodec.decode;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import android.view.Surface;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaDecoder extends Thread {
    public static final String AUDIO_RECORDING_FILE_NAME = "qwe.m4a";
    public static final int BUFFER_SIZE = 88200;
    public static final int CODEC_TIMEOUT_IN_MS = 5000;
    public static final int COMPRESSED_AUDIO_FILE_BIT_RATE = 128000;
    public static final String COMPRESSED_AUDIO_FILE_MIME_TYPE = "audio/mp4a-latm";
    public static final String COMPRESSED_AUDIO_FILE_NAME = "compressed.mp4";
    private static final int FRAME_RATE = 30;
    private static final String MIME_TYPE = "video/avc";
    public static final int SAMPLING_RATE = 32000;
    private float BPP = 0.25f;
    String LOGTAG = "CC";
    int mHeight = 768;
    private MediaCodec mMediaCodec;
    private MediaMuxer mMuxer;
    int mWidth = 1024;
    Surface surface;

    public void init() throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, this.mWidth, this.mHeight);
        format.setInteger("color-format", 2130708361);
        format.setInteger("bitrate", calcBitRate());
        format.setInteger("frame-rate", FRAME_RATE);
        format.setInteger("i-frame-interval", 10);
        this.mMediaCodec = MediaCodec.createDecoderByType(MIME_TYPE);
        this.mMediaCodec.configure(format, null, null, 0);
        this.mMediaCodec.start();
        new MediaExtractor().setDataSource("/sdcAR");
    }

    private int calcBitRate() {
        return (int) (((this.BPP * 30.0f) * ((float) this.mWidth)) * ((float) this.mHeight));
    }

    public void run() {
        Process.setThreadPriority(10);
        try {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDING_FILE_NAME);
            FileInputStream fileInputStream = new FileInputStream(file);
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + COMPRESSED_AUDIO_FILE_NAME);
            if (file.exists()) {
                file.delete();
            }
            MediaMuxer mediaMuxer = new MediaMuxer(file.getAbsolutePath(), 0);
            MediaFormat outputFormat = MediaFormat.createAudioFormat(COMPRESSED_AUDIO_FILE_MIME_TYPE, SAMPLING_RATE, 1);
            outputFormat.setInteger("aac-profile", 2);
            outputFormat.setInteger("bitrate", 79000);
            outputFormat.setInteger("sample-rate", SAMPLING_RATE);
            MediaCodec codec = MediaCodec.createEncoderByType(COMPRESSED_AUDIO_FILE_MIME_TYPE);
            codec.configure(outputFormat, null, null, 1);
            codec.start();
            ByteBuffer[] codecInputBuffers = codec.getInputBuffers();
            ByteBuffer[] codecOutputBuffers = codec.getOutputBuffers();
            BufferInfo outBuffInfo = new BufferInfo();
            byte[] tempBuffer = new byte[BUFFER_SIZE];
            boolean hasMoreData = true;
            double presentationTimeUs = 0.0d;
            int audioTrackIdx = 0;
            int totalBytesRead = 0;
            do {
                int inputBufIndex = 0;
                while (inputBufIndex != -1 && hasMoreData) {
                    inputBufIndex = codec.dequeueInputBuffer(5000);
                    if (inputBufIndex >= 0) {
                        ByteBuffer dstBuf = codecInputBuffers[inputBufIndex];
                        dstBuf.clear();
                        int bytesRead = fileInputStream.read(tempBuffer, 0, dstBuf.limit());
                        if (bytesRead == -1) {
                            hasMoreData = false;
                            codec.queueInputBuffer(inputBufIndex, 0, 0, (long) presentationTimeUs, 4);
                        } else {
                            totalBytesRead += bytesRead;
                            dstBuf.put(tempBuffer, 0, bytesRead);
                            codec.queueInputBuffer(inputBufIndex, 0, bytesRead, (long) presentationTimeUs, 0);
                            presentationTimeUs = (double) ((1000000 * ((long) (totalBytesRead / 2))) / 32000);
                        }
                    }
                }
                int outputBufIndex = 0;
                while (outputBufIndex != -1) {
                    outputBufIndex = codec.dequeueOutputBuffer(outBuffInfo, 5000);
                    if (outputBufIndex >= 0) {
                        ByteBuffer encodedData = codecOutputBuffers[outputBufIndex];
                        encodedData.position(outBuffInfo.offset);
                        encodedData.limit(outBuffInfo.offset + outBuffInfo.size);
                        if ((outBuffInfo.flags & 2) == 0 || outBuffInfo.size == 0) {
                            mediaMuxer.writeSampleData(audioTrackIdx, codecOutputBuffers[outputBufIndex], outBuffInfo);
                            codec.releaseOutputBuffer(outputBufIndex, false);
                        } else {
                            codec.releaseOutputBuffer(outputBufIndex, false);
                        }
                    } else if (outputBufIndex == -2) {
                        outputFormat = codec.getOutputFormat();
                        Log.v(this.LOGTAG, "Output format changed - " + outputFormat);
                        audioTrackIdx = mediaMuxer.addTrack(outputFormat);
                        mediaMuxer.start();
                    } else if (outputBufIndex == -3) {
                        Log.e(this.LOGTAG, "Output buffers changed during encode!");
                    } else if (outputBufIndex != -1) {
                        Log.e(this.LOGTAG, "Unknown return code from dequeueOutputBuffer - " + outputBufIndex);
                    }
                }
                int percentComplete = (int) Math.round(((double) (((float) totalBytesRead) / ((float) file.length()))) * 100.0d);
            } while (outBuffInfo.flags != 4);
            fileInputStream.close();
            mediaMuxer.stop();
            mediaMuxer.release();
            Log.v(this.LOGTAG, "Compression done ...");
        } catch (FileNotFoundException e) {
            Log.e(this.LOGTAG, "File not found!", e);
        } catch (IOException e2) {
            Log.e(this.LOGTAG, "IO exception!", e2);
        }
    }
}
