package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.media.MediaMuxer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by hill on 7/11/17.
 */

public class MediaAudioRecordHandler extends Handler {
    public static final int INIT = 0;
    public static final int CAPTURE = 1;
    public static final int TERMINATE = 2;
    Listener mListener;
    MediaMuxer mediaMuxer;

    public MediaAudioRecordHandler(Looper looper, Listener mListener, MediaMuxer mediaMuxer) {
        super(looper);
        this.mListener = mListener;
        this.mediaMuxer = mediaMuxer;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Log.e("HANDLEM"," "+msg.what);
        switch (msg.what) {
            case INIT:
                init(msg);
                break;
            case CAPTURE:
                capture(msg);
                break;
            case TERMINATE:
                terminate(msg);
                break;
            default:
                break;
        }
    }

    MediaAudioRecord mMediaAudioRecord;

    private void init(Message msg) {
        mMediaAudioRecord = new MediaAudioRecord(mListener,mediaMuxer);
    }

    private void capture(Message msg) {
        Object[] data = (Object[]) msg.obj;
        mMediaAudioRecord.encode((long) data[0], (boolean) data[1]);
    }

    private void terminate(Message msg) {
        mMediaAudioRecord.release();
    }


    public static MediaAudioRecordHandler start(Listener mListener,MediaMuxer mediaMuxer) {
        HandlerThread mHandlerThread = null;
        mHandlerThread = new HandlerThread("Audio");
        mHandlerThread.start();
        MediaAudioRecordHandler maMediaAudioRecordHandler = new MediaAudioRecordHandler(mHandlerThread.getLooper(), mListener,mediaMuxer);
        return maMediaAudioRecordHandler;
    }
}
