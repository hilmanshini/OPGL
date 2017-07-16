package co.astrnt.medrec.medrec.framework.opengl.v3.test;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by hill on 7/16/17.
 */

public class Test extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView mGlSurfaceView = new GLSurfaceView(this);
        mGlSurfaceView.setEGLContextClientVersion(2);
        mGlSurfaceView.setRenderer(new TestDrawer(getResources()));
        setContentView(mGlSurfaceView);
    }
}
