package co.astrnt.medrec.medrec.framework.widget.camera;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import co.astrnt.medrec.medrec.framework.opengl.programs.RectangleProgram;
import co.astrnt.medrec.medrec.framework.opengl.programs.Utils;

/**
 * Created by hill on 6/28/17.
 */

public class CameraGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    public CameraGLSurfaceView(Context context) {
        super(context);
        setRenderer(this);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRenderer(this);
    }

    private RectangleProgram mRectangleProgram;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mRectangleProgram = new RectangleProgram(getResources());
        Utils.allocate(new float[]{0f, 0f, 0f, 1f, 1f, 1f, 1f, 0f});
        Utils.allocate(new float[]{1f, 0f, 1f, 1f});
        GLES20.glVertexAttribPointer();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
