package co.astrnt.medrec.medrec.framework.mediacodec.record;

import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by hill on 7/18/17.
 */

public class MediaAudioRecordThread extends Thread {
    MediaAudioRecord mediaAudioRecord;
    MediaAudioRecord.Listener mListener;
    boolean continous = false;

    public MediaAudioRecordThread(MediaAudioRecord.Listener mListener, MediaMuxer mediaMuxer) {
        this.mListener = mListener;
        mediaAudioRecord = new MediaAudioRecord(mListener, mediaMuxer);
        mediaAudioRecord.sampling();
    }

    public MediaAudioRecordThread(MediaAudioRecord.Listener mListener, MediaMuxer mediaMuxer, boolean continous) {
        this.mListener = mListener;
        mediaAudioRecord = new MediaAudioRecord(mListener, mediaMuxer);
        this.continous = continous;
        mediaAudioRecord.sampling();
    }


    public MediaFormat getNewFormat() {
        return mediaAudioRecord.getNewFormat();
    }

    public int getNewTrackIndex() {
        return mediaAudioRecord.getNewTrackIndex();
    }

    long startTIme = -1;
    int o = 0;

    @Override
    public void run() {
        log("start");
        mListener.waitForInit();
        if (!continous) {
            while (true) {
                try {

                    Integer flag = (Integer) mBlockingQueue.take();

                    if (flag == 0) {

                        o++;

                    } else if (flag == 1) {
                        log("terminating");
                        mediaAudioRecord.encode(System.nanoTime() - startTIme, true);
                        mediaAudioRecord.release();
                        break;
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        } else {
            while (!terminated) {


                    if (startTIme == -1) {
                        startTIme = System.nanoTime();
                    }
                    long time = System.nanoTime() - startTIme;
                    log("feeding " + o + " " + time);
                    mediaAudioRecord.encode(time, false);



            }
            log("terminating");
            mediaAudioRecord.encode(System.nanoTime() - startTIme, true);
            mediaAudioRecord.release();

        }

        mListener.onFinish(mediaAudioRecord.getNewTrackIndex());
        log("fin");
    }

    Object waitLock = new Object();
    BlockingQueue mBlockingQueue = new ArrayBlockingQueue(200000);

    public void feed() {
        if (!paused) {
            mBlockingQueue.offer(0);

        }

    }

    boolean terminated = false;

    public void terminate() {
        if (!continous) {
            mBlockingQueue.offer(1);
        } else {
            terminated = true;
        }


    }


    long videoTime = 0;


    public void syncTime(long presentationTimeUs, boolean b) {
        this.videoTime = presentationTimeUs;
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
        terminate();
    }

    private void log(String s) {
        Log.e("AudioThread", ">" + s);
    }
}
