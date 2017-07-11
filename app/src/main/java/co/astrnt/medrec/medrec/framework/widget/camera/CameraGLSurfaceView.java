package co.astrnt.medrec.medrec.framework.widget.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import co.astrnt.medrec.medrec.framework.opengl.v1.BitmapProgram;
import co.astrnt.medrec.medrec.framework.opengl.v1.GLScene;
import co.astrnt.medrec.medrec.framework.opengl.v1.RectangleProgram;

/**
 * Created by hill on 6/28/17.
 */

public class CameraGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    GLScene mGlScene;

    public CameraGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(this);
    }

    private RectangleProgram mRectangleProgram;
    private BitmapProgram mBitmapProgram;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mGlScene = new GLScene(1f, 1f, 1f);

//        mRectangleProgram = new RectangleProgram(getResources());
//        mRectangleProgram.addBuffer("location", new float[]{-1f, -1f, -1f, 1f, 1f, 1f, 1f, -1f});
//        mRectangleProgram.addBuffer("color", new float[]{1f, 0f, 1f, 1f});
//        mRectangleProgram.addBuffer("order", new short[]{0, 1, 2, 0, 2, 3});
//        Log.e("STATUSGL", " " + mRectangleProgram.toString());
//        mGlScene.addProgram("rect1", mRectangleProgram);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_play);
        mBitmapProgram = new BitmapProgram(getResources(), bitmap);
        mBitmapProgram.addBuffer("bitmapv", new float[]{0.5f, 0.5f,
                0.5f, -0.5f,
                -0.5f, -0.5f,
                -0.5f, 0.5f});
        mBitmapProgram.addBuffer("bitmapf",new float[]{1, 1, 1, 0, 0, 0, 0, 1});
        mBitmapProgram.addBuffer("order",new short[]{0,1,2,0,2,3});

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mGlScene.viewport(width, height);
        mBitmapProgram.viewport(width,height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mGlScene.clear();


//        mRectangleProgram.use();
//        mRectangleProgram.enableVertexBuffer("location", "aPosition", 2);
//        mRectangleProgram.enableUniform("color", "color", 4);
//        mRectangleProgram.draw("order", 6);

        mBitmapProgram.use();
        mBitmapProgram.setXpos((int) (mBitmapProgram.getXpos()+1));
        mBitmapProgram.flushTransform();
        mBitmapProgram.fillMatrix("uMVP");
        mBitmapProgram.enableVertexBuffer("bitmapf","aTexCoord",2);
        mBitmapProgram.enableVertexBuffer("bitmapv","aPosition",2);
        mBitmapProgram.setUniform("alpha",1f);
        mBitmapProgram.enableAlphaChannel();
        mBitmapProgram.draw("order",6);
    }
}
