package co.astrnt.medrec.medrec.framework.opengl.v2;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ShortBuffer;

import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.opengl.IDrawer;
import imageogl.view.opengl_x.GLProgram;
import imageogl.view.opengl_x.GLUtils;
import co.astrnt.medrec.medrec.framework.opengl.v2.objects.BitmapGlObject;
import co.astrnt.medrec.medrec.framework.opengl.v2.objects.CameraGlObject;
import co.astrnt.medrec.medrec.framework.opengl.v2.objects.SimpleGlObject;

public class Drawer implements IDrawer{
    private BitmapGlObject bitmapGlObject;
    private GLProgram bitmapProgram;
    private CameraGlObject cameraGlObject;
    private GLProgram cameraProgram;
    private GLProgram glProgram;
    //private SimpleGlObject simpleGlObject;
    public int textureCamera;
    ShortBuffer mShortBuffer;

    public Drawer(Resources resources) {
        mShortBuffer = GLUtils.allocateShort(new short[]{2, 0, 1});
        this.glProgram = new GLProgram(R.raw.test1_v, R.raw.test1_f, resources);
        this.glProgram.allocate("sampleVertice1", 0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 0.0f);

//        this.simpleGlObject = new SimpleGlObject(this.glProgram);
//        this.glProgram.addObject(this.simpleGlObject);
        this.bitmapProgram = new GLProgram(R.raw.test1_v1, R.raw.test1_f1, resources);
        this.bitmapProgram.allocate("positionVertex", 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f);
        this.bitmapProgram.allocate("coordVertex", 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        this.bitmapGlObject = new BitmapGlObject(this.bitmapProgram, resources, 17301504);
        this.bitmapProgram.addObject(this.bitmapGlObject);
        this.cameraProgram = new GLProgram(R.raw.cam_v, R.raw.cam_f, resources);
        this.cameraProgram.allocate("cameraVertex", -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f);
        this.cameraProgram.allocate("textureVertex", 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
        this.cameraGlObject = new CameraGlObject(this.cameraProgram);
        this.textureCamera = this.cameraGlObject.createTexture();
        this.cameraGlObject.applyCamera(this.textureCamera);
        this.cameraProgram.addObject(this.cameraGlObject);

    }


    public Drawer(Resources resources, int textureCamera) {
        mShortBuffer = GLUtils.allocateShort(new short[]{0, 1, 2});
        this.glProgram = new GLProgram(R.raw.test1_v, R.raw.test1_f, resources);
        this.glProgram.allocate("sampleVertice1", 0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 0.0f);
//        this.simpleGlObject = new SimpleGlObject(this.glProgram);
//        this.glProgram.addObject(this.simpleGlObject);
        this.bitmapProgram = new GLProgram(R.raw.test1_v1, R.raw.test1_f1, resources);
        this.bitmapProgram.allocate("positionVertex", 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f);
        this.bitmapProgram.allocate("coordVertex", 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        this.bitmapGlObject = new BitmapGlObject(this.bitmapProgram, resources, 17301504);
        this.bitmapProgram.addObject(this.bitmapGlObject);
        this.cameraProgram = new GLProgram(R.raw.cam_v, R.raw.cam_f, resources);
        this.cameraProgram.allocate("cameraVertex", -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f);

        this.cameraProgram.allocate("textureVertex", 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
        this.cameraGlObject = new CameraGlObject(this.cameraProgram);
        this.textureCamera = textureCamera;
        this.cameraGlObject.applyCamera(textureCamera, false);
        this.cameraProgram.addObject(this.cameraGlObject);
    }

    public void onSurfaceChanged(int width, int height) {
        this.glProgram.onSurfaceChanged(width, height);
        this.bitmapProgram.onSurfaceChanged(width, height);
        this.cameraGlObject.onSurfaceChanged(width, height);
    }

    @Override
    public void draw(Object... params) {
        float xDelta = (float) params[0];
        float yDelta = (float) params[1];
        this.glProgram.clear();
        this.glProgram.clear(1.0f, 1.0f, 1.0f);
        this.glProgram.onDrawFrame(xDelta, yDelta,1,1,1);
        this.cameraProgram.beginDraw();
        this.cameraGlObject.onDrawFrame();
        float[] q = new float[16];
        Matrix.setIdentityM(q, 0);
        float[] e = cameraGlObject.getTransMatrix();

        this.cameraGlObject.fillUniformMatrix("uMVP", q);


        //log(e);
        this.cameraGlObject.fillUniformMatrix("uSTm", e);
        this.cameraGlObject.enableVertices("cameraVertex", "vPosition");
        this.cameraGlObject.enableVertices("textureVertex", "vTexCoord");
        this.cameraGlObject.fillUniform("sTexture", 0.0f);

        this.cameraGlObject.activeTexture(this.textureCamera);
//        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 3, GLES20.GL_UNSIGNED_SHORT, mShortBuffer);
        this.cameraGlObject.draw(4);
        this.glProgram.endDraw();

//        this.glProgram.beginDraw();
//        this.simpleGlObject.fillUniformMatrix("uMVP", this.simpleGlObject.mMVPMatrix);
//        this.simpleGlObject.fillUniform("uColor", 0.3f, 0.3f, 0.3f, 1.0f);
//        this.simpleGlObject.enableVertices("sampleVertice1", "aPosition");
//        this.simpleGlObject.draw(3);
//        this.glProgram.endDraw();
        this.bitmapProgram.onDrawFrame(d, yDelta,glProgram.translatePixelWidth(400),glProgram.translatePixelHeight(300),1);
        this.bitmapProgram.beginDraw();
        this.bitmapGlObject.fillUniformMatrix("uMVP", this.bitmapGlObject.mMVPMatrix);
        this.bitmapGlObject.enableVertices("positionVertex", "aPosition");
        this.bitmapGlObject.enableVertices("coordVertex", "aTexCoord");
        this.bitmapGlObject.draw(4);
        this.bitmapProgram.endDraw();
        d+= 0.02f;
    }

    private void log(float[] data) {
        Log.e("VERTEX> ", " >>>>>>>>>>>>>>>>>>>>>>>>>> ");
        for (int i = 0; i < data.length; i += 4) {

            Log.e("VERTEX> ", " " + data[i] + " " + data[i + 1] + " " + data[i + 2] + " " + data[i + 3]);

        }
        Log.e("VERTEX> ", " >>>>>>>>>>>>>>>>>>>>>>>>>> ");
    }

    float d = -1;




    public void clear() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void onSurfaceCreated() {

    }

    @Override
    public void release() {

    }
}
