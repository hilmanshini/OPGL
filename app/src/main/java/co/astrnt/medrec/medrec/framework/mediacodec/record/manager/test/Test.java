package co.astrnt.medrec.medrec.framework.mediacodec.record.manager.test;

import android.app.Activity;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecord;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecordHandler;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecordThread;
import co.astrnt.medrec.medrec.framework.mediacodec.record.manager.AudioManager;
import co.astrnt.medrec.medrec.framework.mediacodec.record.manager.AudioVideoRecordManager;
import co.astrnt.medrec.medrec.framework.mediacodec.record.manager.AudioVideoRecordManager2;

/**
 * Created by hill on 7/18/17.
 */

public class Test extends Activity implements View.OnClickListener {
    private AudioVideoRecordManager2 mAudioVideoRecordManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        try {
//            final MediaMuxer mediaMuxer = new MediaMuxer("/sdcard/au.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//            final MediaAudioRecordThread thread =
//                    new MediaAudioRecordThread(new MediaAudioRecord.Listener() {
//                        @Override
//                        public void onFinish() {
//
//                        }
//
//                        @Override
//                        public boolean onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex) {
//                            mediaMuxer.start();
//                            return false;
//                        }
//
//                        @Override
//                        public void onPrepared(MediaCodec mMediaCodec) {
//
//                        }
//
//                        @Override
//                        public void onWriteDataToMuxer(int mTrackIndex, boolean eos, MediaCodec.BufferInfo mBufferInfo) {
//                            if (eos) {
//                                mediaMuxer.stop();
//                                mediaMuxer.release();
//                            }
//                            Log.e("ATIME", "> " + mBufferInfo.presentationTimeUs + " " + TimeUnit.NANOSECONDS.toSeconds(mBufferInfo.presentationTimeUs * 1000000));
//                        }
//                    }, mediaMuxer);
//            thread.start();
//            SystemClock.sleep(3000);
//            thread._stop();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        View v = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        ViewGroup mViewGroup = (ViewGroup) v.findViewById(R.id.container);
        new File("/sdcard/test.mp4").delete();
        mAudioVideoRecordManager = new AudioVideoRecordManager2(this, mViewGroup, "/sdcard/test.mp4");

        mAudioVideoRecordManager.start();
        mAudioVideoRecordManager.getmCustomGLSurfaceView().setOnClickListener(this);
        setContentView(v);


    }

    //    AudioVideoRecordManager mAudioVideoRecordManager;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        View v = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
//        ViewGroup mViewGroup = (ViewGroup) v.findViewById(R.id.container);
//        mAudioVideoRecordManager = new AudioVideoRecordManager(this, mViewGroup, "/sdcard/test.mp4");
//
//        mAudioVideoRecordManager.start();
//        mAudioVideoRecordManager.getmCustomGLSurfaceView().setOnClickListener(this);
//        setContentView(v);
//    }
//
    @Override
    public void onClick(View v) {
        mAudioVideoRecordManager.stop();
    }
}
