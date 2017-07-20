package co.astrnt.medrec.medrec.framework.mediacodec.record.manager.test;

import android.app.Activity;
import android.media.MediaMuxer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.File;
import java.io.IOException;

import co.astrnt.medrec.medrec.framework.mediacodec.record.manager.AudioVideoRecordManager2;
import co.astrnt.medrec.medrec.framework.mediacodec.record.manager.RecordGLView;

/**
 * Created by hill on 7/18/17.
 */

public class Test extends Activity implements View.OnClickListener {
    private AudioVideoRecordManager2 mAudioVideoRecordManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            new File("/sdcard/au.mp4").delete();
            final MediaMuxer mediaMuxer = new MediaMuxer("/sdcard/au.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            final RecordGLView mRecordGLView = new RecordGLView(this,mediaMuxer,new TestLIstener(mediaMuxer,getResources()));
            setContentView(mRecordGLView);
            mRecordGLView.setPaused(false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecordGLView.start();
                }
            },2000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mRecordGLView.stop();
                }
            },12000);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            new File("/sdcard/au.mp4").delete();
//            final MediaMuxer mediaMuxer = new MediaMuxer("/sdcard/au.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
//            MediaAudioRecordThread mediaAudioRecordThread = new MediaAudioRecordThread(new TestAudioListener(mediaMuxer),mediaMuxer);
//            mediaAudioRecordThread.start();
//
//            int i = 300;
//            for (int i1 = 0; i1 < i; i1++) {
//                mediaAudioRecordThread.feed();
//            }
//            mediaAudioRecordThread.terminate();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    @Override
    public void onClick(View v) {
        mAudioVideoRecordManager.stop();
    }

}
