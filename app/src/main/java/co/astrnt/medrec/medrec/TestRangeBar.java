package co.astrnt.medrec.medrec;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;

import co.astrnt.medrec.medrec.tesst.CustomVideoView;
import co.astrnt.medrec.medrec.tesst.RangeBarThumbAdapter;
import co.astrnt.medrec.medrec.tesst.rangebar.widget.RangeBar;

/**
 * Created by hill on 7/21/17.
 */

public class TestRangeBar extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private CustomVideoView mCustomVideoView;
    private RangeBar mRangeBar;
    private RangeBar.OnMeasureVideoChange mOnMeasureVideoChange = new RangeBar.OnMeasureVideoChange() {
        @Override
        public void onGetValueMeasue(int layoutW, int layoutHeight, int capacity, int perSeconds) {
            RangeBarThumbAdapter rangeBarThumbAdapter = new RangeBarThumbAdapter(TestRangeBar.this, "/sdcard/axuu.mp4", capacity, layoutW, layoutHeight, perSeconds);
            mRecyclerView.setAdapter(rangeBarThumbAdapter);
        }
    };
    long tickStart;
    long tickEnd;
    private RangeBar.OnRangeBarChangeListener mOnRangeChange = new RangeBar.OnRangeBarChangeListener() {
        @Override
        public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
            tickStart = leftPinIndex;
            tickEnd = rightPinIndex;

        }

        long lastTickStart, lastTickEnd;
        boolean first;

        @Override
        public void onTouchUp() {
            boolean swipeLeft = false;
            if (first) {
                this.lastTickStart = tickStart;
                this.lastTickEnd = tickEnd;
                first = false;
            } else {
                if (lastTickStart != tickStart) {
                    swipeLeft = true;
                } else {
                    swipeLeft = false;
                }
                this.lastTickStart = tickStart;
                this.lastTickEnd = tickEnd;
            }
            Log.e("TOUP", "STATE " + mCustomVideoView.getState()+" "+tickStart+" "+tickEnd);

            mCustomVideoView.seekTo((int) lastTickStart);
            playBtn.setImageResource(android.R.drawable.ic_media_play);


        }

        @Override
        public void onTouchDown() {
            if(mCustomVideoView.getState() == CustomVideoView.END){
                mCustomVideoView.stopPlayback();
                mCustomVideoView.setVideoPath(videoPath);
            } else {
                mCustomVideoView.pause();
            }

        }
    };
    String videoPath = "/sdcard/axuu.mp4";
    private CustomVideoView.OnProgressListener mOnProgress = new CustomVideoView.OnProgressListener() {
        @Override
        public void onProgress(int cpos) {

            if (cpos == 0 || mCustomVideoView.getState() == 2) {
                return;
            }
            mProgressBar.setProgress(cpos+500);
            if (cpos+500 >= tickEnd && mCustomVideoView.getState() == CustomVideoView.PLAYING) {
                mCustomVideoView.stopPlayback();
                mCustomVideoView.setVideoPath(videoPath);
                playBtn.setImageResource(android.R.drawable.ic_media_play);
            }


        }
    };
    private MediaPlayer.OnErrorListener mOnError = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return true;
        }
    };
    ProgressBar mProgressBar;
    private View.OnClickListener mOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e("XCLICK ", "state " + mCustomVideoView.getState());
            if (mCustomVideoView.getState() == CustomVideoView.END) {
                mCustomVideoView.stopPlayback();

                mCustomVideoView.setVideoPathNoMeasure(videoPath);
                mCustomVideoView.seekTo((int) tickStart);
                mCustomVideoView.start();
                playBtn.setImageResource(android.R.drawable.ic_media_pause);
            } else if (mCustomVideoView.getState() == CustomVideoView.PLAYING) {
                mCustomVideoView.pause();
                playBtn.setImageResource(android.R.drawable.ic_media_play);
            } else if (mCustomVideoView.getState() == CustomVideoView.PAUSED) {
                mCustomVideoView.start();
                playBtn.setImageResource(android.R.drawable.ic_media_pause);
            }
        }
    };
    private CustomVideoView.OnVideoEndListener mOnVideoEndListener = new CustomVideoView.OnVideoEndListener() {
        @Override
        public void onVideoEnd() {
            playBtn.setImageResource(android.R.drawable.ic_media_play);
        }
    };
    Toolbar mToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trim);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Trim video");
        mRecyclerView = (RecyclerView) findViewById(R.id.thumbContainer);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mAdapter);
        mRangeBar = (RangeBar) findViewById(R.id.rangebar);

        playBtn = (ImageView) findViewById(R.id.play_btn);
        playBtn.setOnClickListener(mOnClick);
        mRangeBar.setVideoPath(videoPath);
        mProgressBar.setMax((int) mRangeBar.getDuration());
        mProgressBar.setProgress(0);
        mRangeBar.setOnRangeBarChangeListener(mOnRangeChange);
        mRangeBar.setOnMeasureVideoChange(mOnMeasureVideoChange);
        mCustomVideoView = (CustomVideoView) findViewById(R.id.vidContainer);
        mCustomVideoView.setVideoPath("/sdcard/axuu.mp4");
        mCustomVideoView.setmOnVideoEndListener(mOnVideoEndListener);
        mCustomVideoView.setOnErrorListener(mOnError);
        mCustomVideoView.start(mOnProgress);
    }

    ImageView playBtn;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,1,"save");
        getMenuInflater().inflate(R.menu.m,menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 1){
            Log.e("qwe","qwe");
        }
        return super.onOptionsItemSelected(item);
    }
}
