package co.astrnt.medrec.medrec.framework.opengl.v3.type;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import co.astrnt.medrec.medrec.BuildConfig;
import co.astrnt.medrec.medrec.framework.opengl.v1.Utils;

/**
 * Created by hill on 7/16/17.
 */

public abstract class BufferedObject2D extends Object2D {
    Resources mResources;

    public BufferedObject2D(Resources mResources) {
        this.mResources = mResources;
    }

    public BufferedObject2D(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    private void log(String s) {
        Log.e("HILGL_PictureGL", "> " + s);
    }

    Map<String, Buffer> bufferMap = new HashMap<>();

    public void addBuffer(String name, float... data) {
        log("adding buffer " + name + " " + data);
        if (bufferMap.containsKey(name)) {
            bufferMap.get(name).clear();
        }
        Buffer mBuffer = Utils.allocate(data);
        bufferMap.put(name, mBuffer);
    }

    public void addBuffer(String name, int... data) {
        log("adding buffer " + name + " " + data);

        if (bufferMap.containsKey(name)) {
            bufferMap.get(name).clear();
        }
        Buffer mBuffer = Utils.allocate(data);
        bufferMap.put(name, mBuffer);
    }

    public void addBuffer(String name, short... data) {
        log("adding buffer " + name + " " + data);
        if (bufferMap.containsKey(name)) {
            bufferMap.get(name).clear();
        }
        Buffer mBuffer = Utils.allocate(data);
        bufferMap.put(name, mBuffer);
    }

    protected int originalTextureW, originalTextureH;

    public int loadTexture(int resourceId) {
        log("loading texture for " + resourceId);
        int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        Log.e("TEXTT2", BuildConfig.FLAVOR + textureHandle[0]);
        if (textureHandle[0] != 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            Bitmap bitmap = BitmapFactory.decodeResource(mResources, resourceId, options);
            originalTextureW = bitmap.getWidth();
            originalTextureH = bitmap.getHeight();
            GLES20.glBindTexture(3553, textureHandle[0]);
            GLES20.glTexParameteri(3553, 10241, 9728);
            GLES20.glTexParameteri(3553, 10240, 9728);
            GLES20.glTexParameterf(3553, 10242, 33071.0f);
            GLES20.glTexParameterf(3553, 10243, 33071.0f);
            GLUtils.texImage2D(3553, 0, bitmap, 0);
            bitmap.recycle();
        }
        if (textureHandle[0] != 0) {
            log("have result " + textureHandle[0]);
            return textureHandle[0];
        }
        throw new RuntimeException("Error loading texture.");
    }

    public Resources getResources() {
        return mResources;
    }

    public void enableAlphaChannel() {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
    }

    public void disableAlphaChannel() {
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public void fillMatrix(String name, float[] mMVPMatrix) {
        log("getting uniform forname :" + name + "   result: " + GLES20.glGetUniformLocation(this.getProgramPointer(), name) + " " + " for program " + getProgramPointer());
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(this.getProgramPointer(), name), 1, false, mMVPMatrix, 0);
    }

    public void enableVertices(String name, String var) {
        log("enabling vertice " + name + " " + var + " avail?" + bufferMap.containsKey(name));
        FloatBuffer verticFB2 = (FloatBuffer) bufferMap.get(name);
        if (verticFB2 == null) {
            log("!!!! BUFFER NULL possible result: ");
            for (String s : bufferMap.keySet()) {
                log("!!!! >>>>>>>>> " + s);
            }
            return;
        }
        int posLoc = GLES20.glGetAttribLocation(this.getProgramPointer(), var);
        log("enabling vertice " + name + " result: " + posLoc);
        GLES20.glVertexAttribPointer(posLoc, 2, 5126, false, 0, verticFB2);
        GLES20.glEnableVertexAttribArray(posLoc);
    }

    public Buffer getBuffer(String name) {
        return bufferMap.get(name);
    }

}
