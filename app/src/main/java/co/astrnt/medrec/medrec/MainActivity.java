 package co.astrnt.medrec.medrec;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import co.astrnt.medrec.medrec.framework.widget.camera.CameraGLSurfaceView;
import co.astrnt.medrec.medrec.framework.widget.camera.TestGLSurface;

 public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TestGLSurface mCameraGLSurfaceView = new TestGLSurface(this);
//        setContentView(mCameraGLSurfaceView);
        TestGLSurface mCameraGLSurfaceView = new TestGLSurface(this);
        setContentView(mCameraGLSurfaceView);
    }

}
