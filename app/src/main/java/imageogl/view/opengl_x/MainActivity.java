package imageogl.view.opengl_x;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.widget.Toast;

import co.astrnt.medrec.medrec.framework.opengl.v2.Drawer;
import imageogl.view.opengl_x.mediacodec.VideoCodec;
import imageogl.view.opengl_x.mediacodec.VideoCodec.Listener;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {
    VideoCodec videoCodec;

    class GLS extends GLSurfaceView {
        Mren3 myRenderer;

        public GLS(Context context) {
            super(context);
            init();
        }

        public GLS(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            super.surfaceDestroyed(holder);
        }

        private void init() {
            setEGLContextClientVersion(2);
            this.myRenderer = new Mren3(this);
            setRenderer(this.myRenderer);
        }
    }

    class Mren3 extends TouchableAndRecordableRenderer {
        Drawer drawerImpl;
        float w = 0.0f;

        public Mren3(GLSurfaceView parent) {
            super(parent, "test");
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            super.onSurfaceCreated(gl, config);
            this.drawerImpl = new Drawer(MainActivity.this.getResources());
            try {
                MainActivity.this.videoCodec = new VideoCodec(MainActivity.this.getResources(), EGL14.eglGetCurrentContext(), new Listener() {
                    public void onFinish() {
                        Toast.makeText(MainActivity.this, "Finished", 1).show();
                    }
                }) {

                };
            } catch (IOException e) {
                e.printStackTrace();
            }
            MainActivity.this.videoCodec.start();
            MainActivity.this.videoCodec.sendMessage(VideoCodec.INIT_CAMERA, new int[]{1280, 800, this.drawerImpl.textureCamera});
        }

        public void draw() {
            this.drawerImpl.draw(this.xDelta, this.yDelta);
            MainActivity.this.videoCodec.sendMessage(VideoCodec.DRAW_FRAME, new float[]{this.xDelta, this.yDelta});
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            super.onSurfaceChanged(gl, width, height);
            this.drawerImpl.onSurfaceChanged(width, height);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GLS(this));
    }

    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "Please Wait", 1).show();
        if (videoCodec != null) {
            this.videoCodec.sendMessage(VideoCodec.TERMINATE, new Object());
        }

    }
}
