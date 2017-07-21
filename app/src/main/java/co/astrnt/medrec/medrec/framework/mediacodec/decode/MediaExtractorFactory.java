package co.astrnt.medrec.medrec.framework.mediacodec.decode;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;

/**
 * Created by hill on 7/21/17.
 */

public class MediaExtractorFactory {
    public static class Result{
        public MediaExtractor extractor;
        public MediaFormat format;
        public int trackIndex;
    }
    public static Result getExtractorVideo(String path) {
        Log.e("EXTRR"," "+path);
        try {
            MediaExtractor mMediaExtractor = new MediaExtractor();
            mMediaExtractor.setDataSource(path);
            for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {
                MediaFormat mediaFormat = mMediaExtractor.getTrackFormat(i);
                if (mediaFormat.getString(MediaFormat.KEY_MIME).startsWith("video")) {
                    mMediaExtractor.selectTrack(i);
                    Result result = new Result();
                    result.extractor = mMediaExtractor;
                    result.format = mediaFormat;
                    result.trackIndex = i;
                    return result;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
    public static Result getExtractorAudio(String path) {
        try {
            MediaExtractor mMediaExtractor = new MediaExtractor();
            mMediaExtractor.setDataSource(path);
            for (int i = 0; i < mMediaExtractor.getTrackCount(); i++) {
                MediaFormat mediaFormat = mMediaExtractor.getTrackFormat(i);
                if (mediaFormat.getString(MediaFormat.KEY_MIME).startsWith("audio")) {
                    mMediaExtractor.selectTrack(i);
                    Result result = new Result();
                    result.extractor = mMediaExtractor;
                    result.format = mediaFormat;
                    result.trackIndex = i;
                    return result;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }
}
