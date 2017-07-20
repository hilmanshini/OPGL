package co.astrnt.medrec.medrec.framework.mediacodec.record.manager;

import android.content.Context;
import android.media.MediaMuxer;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecordThread;
import co.astrnt.medrec.medrec.framework.mediacodec.record.manager.test.TestAudioVideoListener;

/**
 * Created by hill on 7/18/17.
 */

public class RecordGLView4 extends RecordGLView {
    TestAudioVideoListener listener;
    public RecordGLView4(Context context, MediaMuxer mediaMuxer, TestAudioVideoListener mEventListener) {
        super(context, mediaMuxer, mEventListener);
        this.listener = mEventListener;
    }

    public RecordGLView4(Context context, AttributeSet attrs, MediaMuxer mediaMuxer, TestAudioVideoListener mEventListener) {
        super(context, attrs, mediaMuxer, mEventListener);
        this.listener = mEventListener;
    }
    MediaAudioRecordThread mediaAudioRecordThread;
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
        Log.e("AVListener","starting audio thread");
        mediaAudioRecordThread = new MediaAudioRecordThread(listener,mediaMuxer);
        mediaAudioRecordThread.start();

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        mediaAudioRecordThread.feed();
    }

    @Override
    public void stop() {
        super.stop();
        mediaAudioRecordThread.terminate();
    }
}
