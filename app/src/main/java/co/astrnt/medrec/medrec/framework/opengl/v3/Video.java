package co.astrnt.medrec.medrec.framework.opengl.v3;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.mediacodec.decode.MediaExtractorFactory;
import co.astrnt.medrec.medrec.framework.mediacodec.decode.MediaVideoDecoder;
import co.astrnt.medrec.medrec.framework.opengl.v3.type.ScriptedObject2D;

/**
 * Created by hill on 7/21/17.
 */

public class Video extends ScriptedObject2D {
    SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener;
    String path;
    private MediaVideoDecoder mediaVideoDecoder;

    public Video(Resources mResources, SurfaceTexture.OnFrameAvailableListener mOnFrameAvailableListener, String path) {
        super(mResources);
        this.path = path;
        this.mOnFrameAvailableListener = mOnFrameAvailableListener;
    }
    public static final String  VERTEX_VAR = "vert";
    public static final String FRAGMENT_VAR = "fragment";
    @Override
    public void draw() {
        updateTexture();
        beginDraw();
        float[] id = new float[16];
        Matrix.setIdentityM(id, 0);
        fillMatrix("uMVPMatrix", id);
        fillMatrix("uTexMatrix", id);
        enableVertices(VERTEX_VAR, "aPosition");
        enableVertices(FRAGMENT_VAR, "aTextureCoord");
        activeTexture0();
        bind();
        draw(4);
        unbind();
        endDraw();
    }

    int textureId;
    SurfaceTexture mSurfaceTexture;
    Surface mSurface;

    @Override
    public void onUpdateViewPort(Scene scene, int width, int height, int orientation) {

        createSurfaceTexture();
        Log.e("DECEND"," tod: "+textureId);
        Surface mSurface = getmSurface();
        getmSurfaceTexture().setDefaultBufferSize(width, height);
        getmSurfaceTexture().setOnFrameAvailableListener(mOnFrameAvailableListener);
        MediaExtractorFactory.Result mResult = MediaExtractorFactory.getExtractorVideo(path);
        mediaVideoDecoder = new MediaVideoDecoder(mResult.format, mSurface, mResult.extractor);
    }

    public boolean readSampleData() {
        return mediaVideoDecoder.readSampleData();
    }
    public long getSampleTime(){
        return mediaVideoDecoder.getSampleTime();
    }

    public void writeSampleData() {
        mediaVideoDecoder.writeSampleData();
    }

    public int getTextureId() {
        return textureId;
    }

    public SurfaceTexture getmSurfaceTexture() {
        return mSurfaceTexture;
    }

    public Surface getmSurface() {
        return mSurface;
    }

    public int createSurfaceTexture() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        textureId = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        mSurfaceTexture = new SurfaceTexture(textureId);
        mSurface = new Surface(mSurfaceTexture);
        return textureId;
    }

    @Override
    public int getVertexSource() {
        return R.raw.test_texture_v;
    }

    @Override
    public int getFragmentSource() {
        return R.raw.test_texture_f;
    }

    public void activeTexture0() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    }

    public void bind() {
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
    }

    public void draw(int size) {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, size);
    }

    public void unbind() {
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }

    public void updateTexture() {
        mSurfaceTexture.updateTexImage();
    }

}
