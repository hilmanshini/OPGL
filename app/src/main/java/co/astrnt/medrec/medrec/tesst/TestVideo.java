package co.astrnt.medrec.medrec.tesst;

import android.app.Activity;
import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import co.astrnt.medrec.medrec.framework.mediacodec.record.CompressedMediaVideoRecordHandler;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecordHandler;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;
import co.astrnt.medrec.medrec.framework.opengl.v2.Drawer;
import imageogl.view.opengl_x.mediacodec.VideoCodec;

/**
 * Created by hill on 7/10/17.
 */

public class TestVideo extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GLS(this));
    }

    MediaVideoRecordHandler mMediaVideoRecordHandler;

    class GLS extends GLSurfaceView implements GLSurfaceView.Renderer {


        Drawer mDrawerDisplay;
        MediaMuxer mediaMuxer;

        public GLS(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            setRenderer(this);

        }

        public GLS(Context context, AttributeSet attrs) {
            super(context, attrs);
            setEGLContextClientVersion(2);
            setRenderer(this);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            try {
                mediaMuxer = new MediaMuxer("/sdcard/qwe.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mDrawerDisplay = new Drawer(getResources());
            try {
                mMediaVideoRecordHandler = CompressedMediaVideoRecordHandler.start(getResources(), mDrawerDisplay, EGL14.eglGetCurrentContext(), new MediaVideoRecordHandler.Listener() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex) {
                        mediaMuxer.start();
                    }

                    @Override
                    public void onPrepared(MediaCodec mMediaCodec) {

                    }

                    @Override
                    public void onWriteDataToMuxer(int mTrackIndex, boolean eos, MediaCodec.BufferInfo mBufferInfo) {

                    }

                    @Override
                    public IDrawer getDrawerMediaCodecInit() {
                        return new Drawer(getResources());
                    }

                    @Override
                    public IDrawer getDrawerMediaCodecInitCamera(Object obj) {
                        return new Drawer(getResources(), ((int[]) obj)[2]);
                    }
                }, 600, 800, mediaMuxer);
            } catch (IOException e) {
                Log.e("STATEX", "FAILED TO START CODEDC");
            }
            mMediaVideoRecordHandler.sendMessage(VideoCodec.INIT_CAMERA, new int[]{600, 800, mDrawerDisplay.textureCamera});
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mDrawerDisplay.onSurfaceChanged(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            mDrawerDisplay.draw(0f, 0f);
            mMediaVideoRecordHandler.sendMessage(VideoCodec.DRAW_FRAME, new float[]{0f, 0f});
        }
    }

    protected void onPause() {
        super.onPause();

        if (mMediaVideoRecordHandler != null) {
            Toast.makeText(this, "Please Wait", Toast.LENGTH_LONG).show();
            mMediaVideoRecordHandler.sendMessage(VideoCodec.TERMINATE, new Object());
        }

    }
}
