package co.astrnt.medrec.medrec.framework.mediacodec.decode;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import co.astrnt.medrec.medrec.framework.mediacodec.record.MediaVideoRecordThread;


/**
 * Created by hill on 7/21/17.
 */

public class TestDisplay extends AppCompatActivity implements View.OnClickListener {
    TestRenderer mTestRenderer;
    private GLSurfaceView mGlSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGlSurfaceView = new GLSurfaceView(this);
        mGlSurfaceView.setOnClickListener(this);
        mGlSurfaceView.setEGLContextClientVersion(2);
mGlSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mGlSurfaceView.setEGLConfigChooser(8,8,8,8,16,0);
        mTestRenderer = new TestRenderer(getResources(),mGlSurfaceView);
        mGlSurfaceView.setRenderer(mTestRenderer);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setContentView(mGlSurfaceView);


    }

    @Override
    public void onClick(View v) {

        mTestRenderer.mVideo.readSampleData();
        mTestRenderer.mVideo.writeSampleData();
    }
}
