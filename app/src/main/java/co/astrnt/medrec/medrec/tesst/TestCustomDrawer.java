package co.astrnt.medrec.medrec.tesst;

import android.app.Activity;
import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.mediacodec.record.CompressedMediaVideoRecordHandler;
import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecordHandler;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;
import co.astrnt.medrec.medrec.framework.opengl.v3.Camera;
import co.astrnt.medrec.medrec.framework.opengl.v3.Picture;
import co.astrnt.medrec.medrec.framework.opengl.v3.Scene;
import imageogl.view.opengl_x.mediacodec.VideoCodec;

/**
 * Created by hill on 7/17/17.
 */

public class TestCustomDrawer extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final MyGLView mMyGLView = new MyGLView(this);
        Log.e("TEST", "TEST");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setContentView(mMyGLView);
            }
        }, 500);

    }

    class CustomDrawer implements IDrawer {
        Camera mCamera;
        Scene mScene;
        int textureCamera = -1;
        boolean open = false;
        Picture mPicture;

        public CustomDrawer() {
            open = true;
        }

        public CustomDrawer(int textureCamera) {
            this.textureCamera = textureCamera;
        }

        @Override
        public void onSurfaceChanged(int width, int height) {
            int rotation = Surface.ROTATION_90;
            if (open) {
                mCamera.startPreview(width, height, rotation);
            }
            mScene.updateViewPort(width, height, rotation);
        }

        @Override
        public void draw(Object... params) {
            mCamera.draw();
//            mPicture.enableAlphaChannel();
            mPicture.draw();
        }

        @Override
        public void clear() {
            mScene.clear();
        }

        @Override
        public void onSurfaceCreated() {
            mScene = new Scene(getResources(), 1, 1, 1, 1);
            mPicture = new Picture(getResources(), R.drawable.logointro);
            if (textureCamera >= 0) {
                mCamera = new Camera(getResources(), textureCamera, mScene);
            } else {
                mCamera = new Camera(getResources(), mScene);
                this.textureCamera = mCamera.getTextureCamera();
            }
            mScene.addOBject(mPicture);
            mScene.addOBject(mCamera);
            mScene.pack();
        }

    }

    private CompressedMediaVideoRecordHandler mMediaVideoRecordHandler;

    class MyGLView extends GLSurfaceView implements GLSurfaceView.Renderer {


        public MyGLView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            setRenderer(this);
        }

        MediaMuxer mediaMuxer;
        CustomDrawer mCustomDrawer;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mCustomDrawer = new CustomDrawer();
            mCustomDrawer.onSurfaceCreated();
            try {
                new File("/sdcard/asd.mp4").delete();
                mediaMuxer = new MediaMuxer("/sdcard/asd.mp4", MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                mediaMuxer.setOrientationHint(90);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            mCustomDrawer.onSurfaceChanged(width, height);
            try {
                mMediaVideoRecordHandler = CompressedMediaVideoRecordHandler.start(getResources(), mCustomDrawer, EGL14.eglGetCurrentContext(), new MediaVideoRecordHandler.Listener() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public boolean onGetFormatToMuxer(MediaFormat newFormat, int mTrackIndex) {
                        mediaMuxer.start();
                        return false;
                    }

                    @Override
                    public void onPrepared(MediaCodec mMediaCodec) {

                    }

                    @Override
                    public void onWriteDataToMuxer(int mTrackIndex, boolean eos, MediaCodec.BufferInfo mBufferInfo) {

                    }

                    @Override
                    public IDrawer getDrawerMediaCodecInit() {
                        CustomDrawer mCustomDrawer = new CustomDrawer();
                        mCustomDrawer.onSurfaceCreated();
                        return mCustomDrawer;
                    }

                    @Override
                    public IDrawer getDrawerMediaCodecInitCamera(Object obj) {
                        CustomDrawer mCustomDrawer = new CustomDrawer(((int[]) obj)[2]);
                        mCustomDrawer.onSurfaceCreated();
                        return mCustomDrawer;
                    }
                }, width, height, mediaMuxer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaVideoRecordHandler.sendMessage(VideoCodec.INIT_CAMERA, new int[]{width, height, mCustomDrawer.mCamera.getTextureCamera()});
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            mCustomDrawer.draw(new float[]{0, 0});
            mMediaVideoRecordHandler.sendMessage(VideoCodec.DRAW_FRAME, new float[]{0f, 0f});
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaVideoRecordHandler != null) {
            Toast.makeText(this, "Please Wait", Toast.LENGTH_LONG).show();
            mMediaVideoRecordHandler.sendMessage(VideoCodec.TERMINATE, new Object());
        }
    }
}