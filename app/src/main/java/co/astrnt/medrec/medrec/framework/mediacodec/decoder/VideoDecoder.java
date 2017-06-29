package co.astrnt.medrec.medrec.framework.mediacodec.decoder;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by hill on 6/29/17.
 */

public class VideoDecoder implements SurfaceTexture.OnFrameAvailableListener {
    SurfaceTexture mSurfaceTexture;
    Resources mResources;
    MediaExtractor mediaExtractor;
    String videoPath;
    MediaFormat activeVideoFormat;
    MediaCodec decoder;
    Surface mSurface;

    public VideoDecoder(SurfaceTexture mSurfaceTexture, String videoPath) {
        this.mSurfaceTexture = mSurfaceTexture;
        this.mSurfaceTexture.setOnFrameAvailableListener(this);
        mSurface = new Surface(mSurfaceTexture);
        this.videoPath = videoPath;
        initExtractor();
        if (activeVideoFormat == null) {
            state = NO_VIDEO_TRACK;
        }
    }

    public static final int UNINITIALIZED = 0;
    public static final int NO_VIDEO_TRACK = 1;
    public static final int HAS_VIDEO_TRACK = 2;
    int state = UNINITIALIZED;

    public int getState() {
        return state;
    }


    void initExtractor() {
        mediaExtractor = new MediaExtractor();
        try {
            mediaExtractor.setDataSource(videoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
            activeVideoFormat = mediaExtractor.getTrackFormat(i);
            if (activeVideoFormat.getString(MediaFormat.KEY_MIME).startsWith("video")) {
                mediaExtractor.selectTrack(i);
                state = HAS_VIDEO_TRACK;
                break;
            }
        }
        Log.e("HILGL", "VIDEOSTATE: " + state);
    }

    public void createDecoder() {
        try {
            decoder = MediaCodec.createDecoderByType(activeVideoFormat.getString(MediaFormat.KEY_MIME));
        } catch (IOException e) {
            e.printStackTrace();
        }

        decoder.configure(activeVideoFormat, mSurface, null, 0 /* 0:decoder 1:encoder */);
        decoder.start();
    }


    public void start() {
        new VideoDecoderThread(this).start();
    }

    public boolean drawIfAny() {
        synchronized (drawLock){
            if (needToDraw) {
                mSurfaceTexture.updateTexImage();
                needToDraw = false;
                return true;
            }
        }

        return false;

    }

    public Object drawLock = new Object();
    boolean needToDraw = false;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        synchronized (drawLock){
            needToDraw = true;
        }

        Log.e("HILGL","FRAMEAVAIL");
    }
}
