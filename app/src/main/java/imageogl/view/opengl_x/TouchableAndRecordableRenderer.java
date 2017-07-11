package imageogl.view.opengl_x;

import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import java.lang.ref.WeakReference;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import co.astrnt.medrec.medrec.BuildConfig;

public abstract class TouchableAndRecordableRenderer implements Renderer, OnTouchListener {
    int height;
    float lastX;
    float lastY;
     EGLConfig mEGLConfig;
    String recordPathl;
    WeakReference<GLSurfaceView> weakParent;
    int width;
    float xDelta = 0.0f;
    float yDelta = 0.0f;

    public abstract void draw();

    public TouchableAndRecordableRenderer(final GLSurfaceView parent, String recordPath) {
        this.weakParent = new WeakReference(parent);
        this.recordPathl = recordPath;
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                TouchableAndRecordableRenderer.this.width = parent.getMeasuredWidth();
                TouchableAndRecordableRenderer.this.height = parent.getMeasuredHeight();
                parent.setOnTouchListener(TouchableAndRecordableRenderer.this);
                parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.mEGLConfig = config;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e("REC", "START");
    }

    public final void onDrawFrame(GL10 gl) {
        draw();
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == 2) {
            if (!(this.lastX == 0.0f || this.lastY == 0.0f)) {
                float yTouched = event.getY() / ((float) this.height);
                float lastYTouched = this.lastY / ((float) this.height);
                this.xDelta += (event.getX() / ((float) this.width)) - (this.lastX / ((float) this.width));
                this.yDelta += yTouched - lastYTouched;
                Log.e("PROG", BuildConfig.FLAVOR + this.xDelta + " " + this.yDelta);
            }
            this.lastX = event.getX();
            this.lastY = event.getY();
        } else if (event.getAction() == 0) {
            Log.e("PROGR", "DOWN");
            this.lastX = event.getX();
            this.lastY = event.getY();
        } else if (event.getAction() == 1) {
            this.lastX = 0.0f;
            this.lastY = 0.0f;
        }
        return true;
    }
}
