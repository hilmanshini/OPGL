package imageogl.view.opengl_x.mediacodec;

import android.content.res.Resources;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;



import java.io.IOException;
import java.nio.ByteBuffer;

import co.astrnt.medrec.medrec.framework.opengl.v2.CodecInputSurface;
import co.astrnt.medrec.medrec.framework.opengl.v2.Drawer;

public class VideoCodec {
    public static int DRAW_FRAME = 0;
    private static final int FRAME_RATE = 30;
    public static int INIT = 2;
    public static int INIT_CAMERA = 3;
    private static final String MIME_TYPE = "video/avc";
    public static int TERMINATE = 1;
    private static long prevOutputPTSUs = 0;
    private float BPP = 0.25f;
    String TAG = "VideoCodec";
    boolean VERBOSE = true;
    CodecInputSurface codecInputSurface;
    Drawer d;

    int e = 0;
    int frame = 0;
    boolean init = false;
    Listener listener;
    private BufferInfo mBufferInfo = new BufferInfo();
    int mHeight = 768;
    private MediaCodec mMediaCodec;
    private MediaMuxer mMuxer;
    private boolean mMuxerStarted = false;
    private int mTrackIndex;
    int mWidth = 1024;
    boolean processing = false;
    public Resources resources;
    Object sLock = new Object();
    Surface surface;
    VidHandler vidHandler;

    public interface Listener {
        void onFinish();
    }

    class VidHandler extends Handler {
        public VidHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                if (msg.what == VideoCodec.DRAW_FRAME) {
                    float[] xy = (float[]) msg.obj;
                    int f = VideoCodec.this.frame;
                    VideoCodec.this.drainEncoder(false);
                    GLES20.glClear(16384);
                    GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                    VideoCodec.this.d.draw(xy[0], xy[1]);
                    long q = (((long) f) * 1000000000) / 60;
                    
                    if (VideoCodec.this.codecInputSurface != null) {
                        VideoCodec.this.codecInputSurface.setPresentationTime(q);
                        VideoCodec.this.codecInputSurface.swapBuffers();
                    }
                    VideoCodec videoCodec = VideoCodec.this;
                    videoCodec.frame++;
                } else if (msg.what == VideoCodec.TERMINATE) {
                    VideoCodec.this.drainEncoder(true);
                    VideoCodec.this.releaseEncoder();
                } else if (msg.what == VideoCodec.INIT) {
                    VideoCodec.this.codecInputSurface.makeCurrent();
                    VideoCodec.this.d = new Drawer(resources);
                    int [] xy = (int[]) msg.obj;
                    VideoCodec.this.d.onSurfaceChanged(xy[0], xy[1]);
                } else if (msg.what == VideoCodec.INIT_CAMERA) {
                    VideoCodec.this.codecInputSurface.makeCurrent();
                    int [] xy = (int[]) msg.obj;
                    VideoCodec.this.d = new Drawer(resources,((int[])msg.obj)[2]);
                    VideoCodec.this.d.onSurfaceChanged(xy[0], xy[1]);
                }
            }
        }
    }


    public VideoCodec(Resources resources) throws IOException {
        this.resources = resources;
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, this.mWidth, this.mHeight);
        format.setInteger("color-format", 2130708361);
        format.setInteger("bitrate", calcBitRate());
        format.setInteger("frame-rate", FRAME_RATE);
        format.setInteger("rotation-degrees", 270);
        format.setInteger("i-frame-interval", 10);
        this.mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        this.mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        this.surface = this.mMediaCodec.createInputSurface();
        this.codecInputSurface = new CodecInputSurface(this.surface);
        this.mMediaCodec.start();
        this.mMuxer = new MediaMuxer("/sdcard/ee123.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        this.mMuxer.setOrientationHint(270);
    }


    public VideoCodec(Resources resources, EGLContext eglContext, Listener listener) throws IOException {
        this.resources = resources;
        this.listener = listener;
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, this.mWidth, this.mHeight);
        format.setInteger("color-format", 2130708361);
        format.setInteger("bitrate", calcBitRate());
        format.setInteger("frame-rate", FRAME_RATE);
        format.setInteger("i-frame-interval", 10);
        format.setInteger("rotation-degrees", 270);
        this.mMediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
        this.mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        this.surface = this.mMediaCodec.createInputSurface();
        this.codecInputSurface = new CodecInputSurface(this.surface, eglContext);
        this.mMediaCodec.start();
        this.mMuxer = new MediaMuxer("/sdcard/ee123.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        this.mMuxer.setOrientationHint(0);
    }

    private int calcBitRate() {
        int bitrate = (int) (((this.BPP * 30.0f) * ((float) this.mWidth)) * ((float) this.mHeight));
        Log.i(this.TAG, String.format("bitrate=%5.2f[Mbps]", new Object[]{Float.valueOf((((float) bitrate) / 1024.0f) / 1024.0f)}));
        return bitrate;
    }

    protected void releaseEncoder() {
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

    protected void drainEncoder(boolean endOfStream) {
        if (this.processing) {
            synchronized (this.sLock) {
                try {
                    this.sLock.wait();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        this.processing = true;
        Log.e("EGLX_REQ", "DRAINING.");
        this.processing = true;
        if (this.VERBOSE) {
            Log.d(this.TAG, "drainEncoder(" + endOfStream + ")");
        }
        if (this.mMediaCodec != null) {
            if (endOfStream) {
                if (this.VERBOSE) {
                    Log.d(this.TAG, "sending EOS to encoder");
                }
                Log.e("EGLX", "EOOOS");
                this.mMediaCodec.signalEndOfInputStream();
            }
            ByteBuffer[] encoderOutputBuffers = this.mMediaCodec.getOutputBuffers();
            while (true) {
                int encoderStatus = this.mMediaCodec.dequeueOutputBuffer(this.mBufferInfo, 10000);
                if (encoderStatus == -1) {
                    Log.e("EGLX", "BUFFERING .. TRY AGAIN " + endOfStream);
                    if (!endOfStream) {
                        break;
                    } else if (this.VERBOSE) {
                        Log.d(this.TAG, "no output available, spinning to await EOS");
                    }
                } else if (encoderStatus == -3) {
                    Log.e("EGLX", "BUFFERING .. IO BUFF CHANGED");
                    encoderOutputBuffers = this.mMediaCodec.getOutputBuffers();
                } else if (encoderStatus == -2) {
                    Log.e("EGLX", "BUFFERING .. IO FORMAT CHANGED");
                    if (this.mMuxerStarted) {
                        throw new RuntimeException("format changed twice");
                    }
                    MediaFormat newFormat = this.mMediaCodec.getOutputFormat();
                    Log.d(this.TAG, "encoder output format changed: " + newFormat);
                    this.mTrackIndex = this.mMuxer.addTrack(newFormat);
                    this.mMuxer.start();
                    this.mMuxerStarted = true;
                } else if (encoderStatus < 0) {
                    Log.e("EGLX", "BUFFERING .. STATUS = " + encoderStatus);
                    Log.w(this.TAG, "unexpected result from encoder.dequeueOutputBuffer: " + encoderStatus);
                } else {
                    Log.e("EGLX", "BUFFERING .. ENCODING ");
                    ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                    if (encodedData == null) {
                        throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                    }
                    Log.e("EGLX", "BUFFERING .. > " + this.mBufferInfo.flags + " " + 2);
                    if ((this.mBufferInfo.flags & 2) != 0) {
                        Log.e("EGLX", "BUFFERING .. >  BUFFER IO CHANGEED");
                        if (this.VERBOSE) {
                            Log.d(this.TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                        }
                        this.mBufferInfo.size = 0;
                    }
                    Log.e("EGLX", "BUFFERING .. WRITING " + this.mBufferInfo.size);
                    if (this.mBufferInfo.size != 0) {
                        if (this.mMuxerStarted) {
                            encodedData.position(this.mBufferInfo.offset);
                            encodedData.limit(this.mBufferInfo.offset + this.mBufferInfo.size);
                            this.mMuxer.writeSampleData(this.mTrackIndex, encodedData, this.mBufferInfo);
                            if (this.VERBOSE) {
                                Log.d(this.TAG, "sent " + this.mBufferInfo.size + " bytes to muxer");
                            }
                        } else {
                            throw new RuntimeException("muxer hasn't started");
                        }
                    }
                    this.mMediaCodec.releaseOutputBuffer(encoderStatus, false);
                    if ((this.mBufferInfo.flags & 4) != 0) {
                        break;
                    }
                }
            }
            Log.e("EGLX", "BUFFERING .. WRITING EOS  " + this.mBufferInfo.flags + " " + endOfStream);
            if (!endOfStream) {
                Log.w(this.TAG, "reached end of stream unexpectedly");
            } else if (this.VERBOSE) {
                Log.d(this.TAG, "end of stream reached");
            }
            //this.listener.onFinish();
            this.processing = false;
            synchronized (this.sLock) {
                this.sLock.notify();
            }
        }
    }

    public void makeCurrent() {
        this.codecInputSurface.makeCurrent();
    }

    public void makeDefault() {
        this.codecInputSurface.makeDefault();
    }

    public void start() {
        HandlerThread handlerThread = new HandlerThread("VCodec");
        handlerThread.start();
        this.vidHandler = new VidHandler(handlerThread.getLooper());
    }

    public void sendMessage(int msg, Object object) {
        Message message = new Message();
        message.what = msg;
        message.obj = object;
        this.vidHandler.sendMessage(message);
    }



    public void draw() {
    }

    public void draw(long f) {
    }
}
