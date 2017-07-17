package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.content.res.Resources;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGLContext;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecordHandler.Listener;
import co.astrnt.medrec.medrec.framework.mediacodec.MediaFormatFactory;
import co.astrnt.medrec.medrec.framework.opengl.v2.CodecInputSurface;

/**
 * Created by hill on 7/10/17.
 */

public class MediaVideoRecord {
    private Surface surface;
    private CodecInputSurface codecInputSurface;
    private MediaMuxer mMuxer;
    private Listener listener;
    private MediaCodec mMediaCodec;
    private Resources mResources;
    private MediaFormat outputFormat;
    private MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private int mTrackIndex;

    public MediaVideoRecord(Resources mResources, Listener listener, int width, int height,MediaMuxer mMuxer) throws IOException {
        this.mResources = mResources;
        this.listener = listener;
        MediaFormat format = MediaFormat.createVideoFormat("video/avc", width, height);
        format.setInteger("color-format", 2130708361);
        format.setInteger("bitrate", MediaFormatFactory.calcBitRate(width, height));
        format.setInteger("frame-rate", 5);
        format.setInteger("rotation-degrees", 270);
        format.setInteger("i-frame-interval", 15);
        this.mMediaCodec = MediaCodec.createEncoderByType("video/avc");
        this.mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        this.surface = this.mMediaCodec.createInputSurface();
        this.codecInputSurface = new CodecInputSurface(this.surface);
        this.mMediaCodec.start();
        listener.onPrepared(mMediaCodec);
        this.mMuxer = mMuxer;
        this.mMuxer.setOrientationHint(270);
    }

    public MediaVideoRecord(Resources resources, EGLContext eglContext, Listener listener, int width, int height,MediaMuxer mMuxer) throws IOException {
        this.mResources = resources;
        this.listener = listener;
        this.outputFormat = MediaFormatFactory.createStandardFormat(width, height);
        this.mMediaCodec = MediaCodec.createEncoderByType(outputFormat.getString(MediaFormat.KEY_MIME));
        this.mMediaCodec.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        this.surface = this.mMediaCodec.createInputSurface();
        this.codecInputSurface = new CodecInputSurface(this.surface, eglContext);
        this.mMediaCodec.start();
        listener.onPrepared(mMediaCodec);
        this.mMuxer = mMuxer;
        this.mMuxer.setOrientationHint(0);
    }

    protected void release() {
        if (this.mMediaCodec != null) {
            this.mMediaCodec.stop();
            this.mMediaCodec.release();
            this.mMediaCodec = null;
        }
        if (this.codecInputSurface != null) {
            this.codecInputSurface.release();
            this.codecInputSurface = null;
        }
        if (this.mMuxer != null) {
            this.mMuxer.stop();
            this.mMuxer.release();
            this.mMuxer = null;
        }
    }



    public void drain(boolean eos) {
        if (mMediaCodec == null) {
            return;
        }
        ByteBuffer[] encoderOutputBuffers = this.mMediaCodec.getOutputBuffers();
        while (true) {
            int encoderStatus = this.mMediaCodec.dequeueOutputBuffer(this.mBufferInfo, 10000);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!eos) {
                    break;
                } else {
                    //keep looping
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                encoderOutputBuffers = this.mMediaCodec.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat newFormat = this.mMediaCodec.getOutputFormat();
                Log.e("VDECFOR", " " + newFormat);
                this.mTrackIndex = this.mMuxer.addTrack(newFormat);
                listener.onGetFormatToMuxer(newFormat,mTrackIndex);


            } else if (encoderStatus < 0) {
                //fail?
            } else {
                Log.e("VDEC", "WRITING " + mBufferInfo.size);
                // has data
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if ((mBufferInfo.flags & 2) != 0) {
                    //mBufferInfo.size = 0;
                }
                if (mBufferInfo.size != 0) {
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);

                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                    listener.onWriteDataToMuxer(mTrackIndex, eos,mBufferInfo);
                }
                mMediaCodec.releaseOutputBuffer(encoderStatus, false);
            }
            if (eos) {
                break;
            }
        }
    }

    public void swapDisplay(long time) {
        Log.e("STIMZ", " " + time);
        if (codecInputSurface != null) {
            codecInputSurface.setPresentationTime(time);
            codecInputSurface.swapBuffers();
        }
    }


    public void swapDisplay(int frame) {
        long time = (((long) frame) * 1000000000) / 60;
        Log.e("STIMZ", " " + time);
        if (codecInputSurface != null) {
            codecInputSurface.setPresentationTime(time);
            codecInputSurface.swapBuffers();
        }
    }

    public void makeCurrent() {
        this.codecInputSurface.makeCurrent();
    }

}
