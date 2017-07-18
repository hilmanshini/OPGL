package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

/**
 * Created by hill on 7/18/17.
 */

public class MediaAudioRecordThread extends Thread {
    MediaAudioRecord mediaAudioRecord;

    public MediaAudioRecordThread(MediaAudioRecord.Listener mListener, MediaMuxer mediaMuxer) {
        mediaAudioRecord = new MediaAudioRecord(mListener, mediaMuxer);
        mediaAudioRecord.sampling();
    }

    public MediaFormat getNewFormat() {
        return mediaAudioRecord.getNewFormat();
    }

    public int getNewTrackIndex() {
        return mediaAudioRecord.getNewTrackIndex();
    }

    long startTIme;

    @Override
    public void run() {
        long startTIme = System.nanoTime();

        while (!stopped) {
            log("time "+startTIme+" "+(System.nanoTime() - startTIme));
            long i = 600l;

            mediaAudioRecord.encode(System.nanoTime() - startTIme, false);

        }
        log("fin");
        mediaAudioRecord.encode(System.nanoTime() - startTIme, true);

        mediaAudioRecord.release();


    }

    boolean syncing = false;
    long videoTime = 0;
    Object requesSynctLock = new Object();
    Object waitLock = new Object();

    public void syncTime(long presentationTimeUs, boolean b) {
//        if (paused) {
//            log("paused, request sync " + presentationTimeUs + " discarded");
//            return;
//        }
//        if (syncing) {
//            log("still syncing request sync for " + presentationTimeUs + " waiting ");
//            synchronized (requesSynctLock) {
//                try {
//                    requesSynctLock.wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            log("syncing done request sync for " + presentationTimeUs + " resuming ");
//        }
        this.videoTime = presentationTimeUs;
//        stopped = b;
//        syncing = true;
//        synchronized (waitLock) {
//            waitLock.notify();
//        }
    }

    boolean paused;

    public void pause() {
        this.paused = true;
    }

    public void resumePause() {
        this.paused = false;
    }

    boolean stopped = false;

    public void _stop() {
        log("Stopping");
        stopped = true;
    }

    private void log(String s) {
        Log.e("AudioThread", ">" + s);
    }
}
