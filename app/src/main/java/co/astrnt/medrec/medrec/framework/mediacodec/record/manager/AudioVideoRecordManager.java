package co.astrnt.medrec.medrec.framework.mediacodec.record.manager;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecordHandler;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecordHandler;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;

/**
 * Created by hill on 7/12/17.
 */

public class AudioVideoRecordManager implements MediaAudioRecord.Listener, MediaVideoRecordHandler.Listener {

    private MediaMuxer mediaMuxer;
    private Resources mResources;
    private ViewGroup container;
    private CustomGLSurfaceView mCustomGLSurfaceView;
    private String outputPath;
    private Context mContext;

    public AudioVideoRecordManager(Context mContext, ViewGroup container, String outputPath) {
        new File(outputPath).delete();
        this.mContext = mContext;
        this.container = container;
        this.mResources = container.getResources();
        this.outputPath = outputPath;
    }

    MediaFormat aFormat;

    public void start() {
        initMuxer(outputPath);

        mediaAudioRecordHandler = MediaAudioRecordHandler.start(this, mediaMuxer);
        mediaAudioRecordHandler.waitForItsReady();
        aFormat = mediaAudioRecordHandler.getNewFormat();
        aTrackIndex = mediaAudioRecordHandler.getNewTrackIndex();
        log("get audio format " + aFormat + " ");


        mCustomGLSurfaceView = new CustomGLSurfaceView(mContext, mediaMuxer, this);

        container.addView(mCustomGLSurfaceView);
    }

    public CustomGLSurfaceView getmCustomGLSurfaceView() {
        return mCustomGLSurfaceView;
    }

    private void initMuxer(String path) {
        log("init muxer");
        try {
            this.mediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFinish() {

    }


    int vTrackIndex;
    int aTrackIndex;
    boolean ready = false;

    @Override
    public boolean onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex) {
        if (newFormat.getString(MediaFormat.KEY_MIME).startsWith("video")) {
            log("get format video " + newFormat);
            vTrackIndex = mTrackIndex;
            mediaMuxer.start();
            ready = true;
            return false;
        } else {
            return true;
        }

    }

    private MediaAudioRecordHandler mediaAudioRecordHandler;

    @Override
    public void onPrepared(MediaCodec mMediaCodec) {

    }


    long lastAudioTime = 0;
    Object writeLock = new Object();

    @Override
    public void onWriteDataToMuxer(int mTrackIndex, boolean eos, MediaCodec.BufferInfo mBufferInfo) {

        log("writing for time " + mBufferInfo.presentationTimeUs + " " + eos + " " + mTrackIndex + " vtrack: " + vTrackIndex + " " + aTrackIndex + " ");
        if(!ready){
            return;
        }
        mediaAudioRecordHandler.capture(mBufferInfo.presentationTimeUs, eos);

    }

    @Override
    public IDrawer getDrawerMediaCodecInit() {
        CustomDrawer mCustomDrawer = new CustomDrawer(mResources);
        mCustomDrawer.onSurfaceCreated();
        return mCustomDrawer;
    }

    @Override
    public IDrawer getDrawerMediaCodecInitCamera(Object obj) {
        CustomDrawer mCustomDrawer = new CustomDrawer(mResources, ((int[]) obj)[2]);
        mCustomDrawer.onSurfaceCreated();
        return mCustomDrawer;
    }

    private void log(String s) {
        Log.e("HILGL_MANAGER", "> " + s);
    }

    public void stop() {
        log("stopping");
//        mediaAudioRecordHandler.terminate();
        mCustomGLSurfaceView.stop();
    }
}
