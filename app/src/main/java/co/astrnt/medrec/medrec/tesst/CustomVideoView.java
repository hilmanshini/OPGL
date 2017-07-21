package co.astrnt.medrec.medrec.tesst;

/**
 * Created by hill on 7/21/17.
 */


import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.File;
import java.util.Map;

import co.astrnt.medrec.medrec.R;


/**
 * Created by hill on 4/21/17.
 */

public class CustomVideoView extends VideoView implements MediaPlayer.OnInfoListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, ViewTreeObserver.OnGlobalLayoutListener {
    public static final int BUFFERING = 5;
    public static final int PREPARED = 6;

    public CustomVideoView(Context context) {
        super(context);
        setBackgroundResource(android.R.color.transparent);
        super.setOnInfoListener(this);
        setOnCompletionListener(this);
        setOnPreparedListener(this);
    }


    Uri uri;

    @Override
    public void setVideoURI(Uri uri) {
        super.setVideoURI(uri);
        this.uri = uri;

    }

    public void setVideoURL(String uri) {

        super.setVideoURI(Uri.parse(uri));
        this.uri = Uri.parse(uri);
        ;

    }

    public Uri getUri() {
        return uri;
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(android.R.color.transparent);
        super.setOnInfoListener(this);
        setOnCompletionListener(this);
        setOnPreparedListener(this);

    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(android.R.color.transparent);
        super.setOnInfoListener(this);
        setOnCompletionListener(this);
        setOnPreparedListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setBackgroundResource(android.R.color.transparent);
        super.setOnInfoListener(this);
        setOnCompletionListener(this);
        setOnPreparedListener(this);
    }

    public static final int PLAYING = 0;
    public static final int PAUSED = 1;
    public static final int END = 2;
    public static final int INIT = 3;
    public static final int RESTARTING = 4;
    int state = INIT;
    private int vwidth, vheight;


    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {

        Log.e("VideoState", ">PLAYING > " + what + "  > " + extra);
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            if (callback != null) {
                callback.onChangeState(PLAYING);
            }
            Log.e("VideoState", ">PLAYING");
            state = PLAYING;

        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            if (callback != null) {
                callback.onChangeState(PLAYING);
            }
            Log.e("VideoState", ">PLAYING END");
            state = PLAYING;
        }

        return false;
    }

    @Override
    public void pause() {
        Log.e("VIDX", "PAUSED");
        state = PAUSED;

        super.pause();
    }

    @Override
    public void start() {
        Log.e("VideoState", "START " + state);
        if (state == INIT) {
            if (callback != null) {
                callback.onChangeState(BUFFERING);
            }
        }
        if (state == BUFFERING) {
            return;
        }
        if (!prepared) {
            state = BUFFERING;
        }
        if (state == END) {
            state = RESTARTING;
        } else {
            state = PLAYING;
        }
        Log.e("VIDXXX", "RESUMING");
        super.start();
    }

    boolean videoPathNeedToSet = true;

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("VIDX", "END");
        Log.e("VideoState", "END");
        state = END;
        prepared = false;
        if (callback != null) {
            callback.onChangeState(END);
        }
        if (mOnVideoEndListener != null) {
            mOnVideoEndListener.onVideoEnd();
        }
        videoPathNeedToSet = true;
        mp.reset();
    }

    OnVideoEndListener mOnVideoEndListener;

    public void setmOnVideoEndListener(OnVideoEndListener mOnVideoEndListener) {
        this.mOnVideoEndListener = mOnVideoEndListener;
    }

    @Override
    public void onGlobalLayout() {

        float factor = (float) getHeight() / (float) vheight;
        int mustHaveWidth = (int) ((float) getWidth() * factor);
        int mustHaveHegiht = getHeight();
        Log.e("XMEZZ", "" + getWidth() + " " + getHeight() + " " + vheight + " " + mustHaveWidth + " " + mustHaveHegiht + " " + factor);
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(mustHaveWidth, mustHaveHegiht);
        rp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rp.addRule(RelativeLayout.CENTER_VERTICAL);
        rp.addRule(RelativeLayout.BELOW, R.id.appbar);
        setLayoutParams(rp);

        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        requestLayout();
        invalidate();
    }

    public interface OnVideoEndListener {
        public void onVideoEnd();
    }

    boolean prepared;

    @Override
    public void stopPlayback() {
        state = END;
        videoPathNeedToSet = true;

        Log.e("VideoState", "END");
        prepared = false;
        super.stopPlayback();
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //mp.setLooping(true);
        prepared = true;
        state = PLAYING;

        if (callback != null) {
            callback.onChangeState(PREPARED);
        }
        Log.e("VideoState", "PREPARED");

    }

    public static interface Callback {
        public void onChangeState(int id);
    }

    MediaPlayer.OnInfoListener l;

    @Override
    public void setOnInfoListener(MediaPlayer.OnInfoListener l) {
        this.l = l;

        super.setOnInfoListener(this);
    }

    public void wrapPlayButton(ImageView playBtn) {
        VideoIndicatorWrapper.wrap(this, playBtn);
    }

    public void wrapPlayButtonCircled(ImageView playBtn) {
        VideoIndicatorWrapper.wrapCircled(this, playBtn);
    }

    public void wrapPlayButton(ImageView playBtn, View thumb) {
        VideoIndicatorWrapper.wrap(this, playBtn, thumb);
    }

    public void wrapPlayButtonCircled(ImageView playBtn, View thumb) {
        VideoIndicatorWrapper.wrapCircled(this, playBtn, thumb);
    }

    public static boolean isVideoDownloaded(Context context, long videoId) {
        File root = context.getFilesDir();
        File dirs = new File(root, "cache_vids");

        if (!dirs.exists()) {
            dirs.mkdirs();
        }
        File videoFile = new File(dirs, videoId + ".mp4");
        return videoFile.exists();

    }

    public void playWithId(Context mContext, long videoId) {
        File root = mContext.getFilesDir();
        File dirs = new File(root, "cache_vids");

        if (!dirs.exists()) {
            dirs.mkdirs();
        }
        File videoFile = new File(dirs, videoId + ".mp4");
        if (videoPathNeedToSet) {
            setVideoPath(videoFile.getAbsolutePath());
        }

        videoPathNeedToSet = false;

    }

    public void setVideoPathNoMeasure(String path) {
        if (videoPathNeedToSet) {
            super.setVideoPath(path);

        }
        videoPathNeedToSet = false;
    }

    boolean measured = false;

    @Override
    public void setVideoPath(String path) {
        if (videoPathNeedToSet) {
            super.setVideoPath(path);
        }
        if(!measured){
            videoPathNeedToSet = false;
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(path);
            vwidth = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            vheight = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));

            mediaMetadataRetriever.release();
            getViewTreeObserver().addOnGlobalLayoutListener(this);
            requestLayout();
            invalidate();
        }
        measured = true;


    }

    static class VideoIndicatorWrapper {
        public static void wrap(final CustomVideoView videoView, final ImageView playBtn) {
            videoView.setCallback(new CustomVideoView.Callback() {
                @Override
                public void onChangeState(int id) {
                    if (id == PLAYING) {


                        playBtn.setImageResource(R.drawable.ic_pause);
                    } else if (id == CustomVideoView.PAUSED) {
                        playBtn.setImageResource(R.drawable.ic_play);
                    } else if (id == CustomVideoView.END) {
                        playBtn.setImageResource(R.drawable.ic_play);
                    } else if (id == CustomVideoView.PREPARED) {
                        playBtn.setVisibility(View.VISIBLE);
                        playBtn.setImageResource(R.drawable.ic_hourglass);
                    } else if (id == CustomVideoView.BUFFERING) {
                        playBtn.setImageResource(R.drawable.ic_hourglass);
                    }
                    Log.e("VIDX_STATE", id + "");
                }
            });

            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("VIDX_STATEC", videoView.getState() + "");
                    if (videoView.getState() == CustomVideoView.RESTARTING) {
                        return;
                    }
                    if (videoView.getState() == PLAYING) {
                        videoView.pause();
                        playBtn.setImageResource(R.drawable.ic_play);

                    } else if (videoView.getState() == CustomVideoView.PAUSED) {
                        videoView.start();
                        playBtn.setImageResource(R.drawable.ic_pause);
                    } else if (videoView.getState() == CustomVideoView.END) {
                        playBtn.setImageResource(R.drawable.ic_hourglass);
                        videoView.setVideoURI(videoView.getUri());
                        videoView.start();
                    }
                }
            });
        }

        public static void wrapCircled(final CustomVideoView videoView, final ImageView playBtn) {
            videoView.setCallback(new CustomVideoView.Callback() {
                @Override
                public void onChangeState(int id) {
                    if (id == PLAYING) {


                        playBtn.setImageResource(R.drawable.ic_pause_circle);
                    } else if (id == CustomVideoView.PAUSED) {
                        playBtn.setImageResource(R.drawable.ic_play_circle);
                    } else if (id == CustomVideoView.END) {
                        playBtn.setImageResource(R.drawable.ic_play_circle);
                    } else if (id == CustomVideoView.PREPARED) {
                        playBtn.setVisibility(View.VISIBLE);
                        playBtn.setImageResource(R.drawable.ic_hourglass_circle);
                    } else if (id == CustomVideoView.BUFFERING) {
                        playBtn.setImageResource(R.drawable.ic_hourglass_circle);
                    }
                    Log.e("VIDX_STATE", id + "");
                }
            });

            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("VIDX_STATEC", videoView.getState() + "");
                    if (videoView.getState() == CustomVideoView.RESTARTING) {
                        return;
                    }
                    if (videoView.getState() == PLAYING) {
                        videoView.pause();
                        playBtn.setImageResource(R.drawable.ic_play_circle);

                    } else if (videoView.getState() == CustomVideoView.PAUSED) {
                        videoView.start();
                        playBtn.setImageResource(R.drawable.ic_pause_circle);
                    } else if (videoView.getState() == CustomVideoView.END) {
                        playBtn.setImageResource(R.drawable.ic_hourglass_circle);
                        videoView.setVideoURI(videoView.getUri());
                        videoView.start();
                    }
                }
            });
        }

        public static void wrap(final CustomVideoView videoView, final ImageView playBtn, final View view) {

            videoView.setCallback(new CustomVideoView.Callback() {
                boolean firstTime = false;

                @Override
                public void onChangeState(int id) {
                    Log.e("VIDEOSTATE_CHANGE", videoView.getState() + " ");
                    if (id == PLAYING) {
                        playBtn.setImageResource(R.drawable.ic_pause);
                    } else if (id == END) {
                        playBtn.setImageResource(R.drawable.ic_play);
                    } else if (id == CustomVideoView.BUFFERING) {
                        playBtn.setImageResource(R.drawable.ic_hourglass);
                    }

                }
            });

            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("VIDEOSTATE ", videoView.getState() + " ");
                    if (videoView.getState() == CustomVideoView.INIT) {
                        view.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.start();
                    } else if (videoView.getState() == PLAYING) {
                        playBtn.setImageResource(R.drawable.ic_play);
                        videoView.pause();
                    } else if (videoView.getState() == PAUSED) {
                        playBtn.setImageResource(R.drawable.ic_pause);
                        videoView.start();
                    } else if (videoView.getState() == END) {
                        playBtn.setImageResource(R.drawable.ic_hourglass);
                        videoView.setVideoURI(videoView.getUri());
                        videoView.start();
                    }
                }
            });
        }


        public static void wrapCircled(final CustomVideoView videoView, final ImageView playBtn, final View view) {
            videoView.setCallback(new CustomVideoView.Callback() {
                boolean firstTime = false;

                @Override
                public void onChangeState(int id) {
                    Log.e("VIDEOSTATE_CHANGE", videoView.getState() + " ");
                    if (id == PLAYING) {
                        playBtn.setImageResource(R.drawable.ic_pause_circle);
                    } else if (id == END) {
                        playBtn.setImageResource(R.drawable.ic_play_circle);
                    } else if (id == CustomVideoView.BUFFERING) {
                        playBtn.setImageResource(R.drawable.ic_hourglass_circle);
                    }

                }
            });

            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("VIDEOSTATE ", videoView.getState() + " ");
                    if (videoView.getState() == CustomVideoView.INIT) {
                        view.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.start();
                    } else if (videoView.getState() == PLAYING) {
                        playBtn.setImageResource(R.drawable.ic_play_circle);
                        videoView.pause();
                    } else if (videoView.getState() == PAUSED) {
                        playBtn.setImageResource(R.drawable.ic_pause_circle);
                        videoView.start();
                    } else if (videoView.getState() == END) {
                        playBtn.setImageResource(R.drawable.ic_hourglass_circle);
                        videoView.setVideoURI(videoView.getUri());
                        videoView.start();
                    }
                }
            });
        }
    }

    class MonitorThread extends Thread {
        boolean stop;
        OnProgressListener mOnProgressListener;

        public MonitorThread(OnProgressListener mOnProgressListener) {
            this.mOnProgressListener = mOnProgressListener;
        }

        @Override
        public void run() {
            super.run();
            while (!stop) {

                try {
                    int cpos = getCurrentPosition();
                    SystemClock.sleep(100);
                    if (mOnProgressListener != null) {
                        mOnProgressListener.onProgress(cpos);
                    }
                } catch (Exception e) {
                }

            }

        }
    }

    public interface OnProgressListener {
        public void onProgress(int cpos);
    }

    OnProgressListener mOnProgressListener;
    MonitorThread mMonitorThread;

    public void start(OnProgressListener mOnProgressListener) {
        this.mOnProgressListener = mOnProgressListener;
        mMonitorThread = new MonitorThread(mOnProgressListener);
        mMonitorThread.start();
        start();
    }

    public void release() {
        if (mMonitorThread != null) {
            mMonitorThread.stop = true;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (mMonitorThread != null) {
            mMonitorThread.stop = true;
        }
        super.finalize();
    }

}
