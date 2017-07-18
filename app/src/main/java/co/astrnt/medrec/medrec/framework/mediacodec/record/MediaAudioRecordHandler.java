package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaAudioRecord.Listener;

import co.astrnt.medrec.medrec.framework.opengl.IDrawer;

/**
 * Wont work with handler
 * Created by hill on 7/11/17.
 */
@Deprecated
public class MediaAudioRecordHandler extends Handler {
    public static final int INIT = 0;
    public static final int CAPTURE = 1;
    public static final int TERMINATE = 2;
    public static final int SAMPLING = 3;
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
        Log.e("HANDLEM", " " + msg.what);
        switch (msg.what) {
            case INIT:
                _init(msg);
                break;
            case SAMPLING:
                _sampling();
                break;
            case CAPTURE:
                _capture(msg);
                break;
            case TERMINATE:
                _terminate(msg);
                break;
            default:
                break;
        }
    }

    private void _sampling() {
        mMediaAudioRecord.sampling();
    }

    MediaAudioRecord mMediaAudioRecord;

    private void _init(Message msg) {
        mMediaAudioRecord = new MediaAudioRecord(mListener, mediaMuxer);
    }
    public void Internalinit(){
        mMediaAudioRecord = new MediaAudioRecord(mListener, mediaMuxer);
    }

    private void _capture(Message msg) {
        Object[] data = (Object[]) msg.obj;
        Log.e("DEQWRITE_MSG"," "+data[0]+" "+data[1]);
        mMediaAudioRecord.encode((long) data[0], (boolean) data[1]);
    }

    public MediaAudioRecord getmMediaAudioRecord() {
        return mMediaAudioRecord;
    }

    private void _terminate(Message msg) {
        mMediaAudioRecord.release();
    }


    public static MediaAudioRecordHandler start(Listener mListener, MediaMuxer mediaMuxer) {
        HandlerThread mHandlerThread = null;
        mHandlerThread = new HandlerThread("Audio");
        mHandlerThread.start();
        MediaAudioRecordHandler maMediaAudioRecordHandler = new MediaAudioRecordHandler(mHandlerThread.getLooper(), mListener, mediaMuxer);
        return maMediaAudioRecordHandler;
    }

    public void waitForItsReady() {
        mMediaAudioRecord = new MediaAudioRecord(mListener, mediaMuxer);
        sendEmptyMessage(SAMPLING);
    }

    public int getNewTrackIndex() {
        return mMediaAudioRecord.getNewTrackIndex();
    }


    /**
     * Created by hill on 7/10/17.
     */


    public void init(){
        sendEmptyMessage(INIT);
    }
    public void capture(long time,boolean eos){
        Object[] data = new Object[]{
                time, eos
        };
        sendMessage(obtainMessage(MediaAudioRecordHandler.CAPTURE, data));
    }
    public void terminate(){
        sendEmptyMessage(MediaAudioRecordHandler.TERMINATE);
    }
    public MediaFormat getNewFormat(){
        return mMediaAudioRecord.getNewFormat();
    }
}
