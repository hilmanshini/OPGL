package co.astrnt.medrec.medrec.framework.opengl.v3;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import co.astrnt.medrec.medrec.BuildConfig;
import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.opengl.v1.Utils;
import co.astrnt.medrec.medrec.framework.opengl.v3.type.ScriptedObject2D;

/**
 * Created by hill on 7/16/17.
 */

public class Picture extends ScriptedObject2D {
    private final int textureActiveId;
    float[] mMVPMatrix = new float[16];

    public Picture(Resources mResources, int bitmap) {
        super(mResources);
        addBuffer("positionVertex", 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f);
        addBuffer("coordVertex", 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        addBuffer("order",new short[]{0,1,2,0,2,3});
        this.textureActiveId = loadTexture(bitmap);
    }

    public int getVertexSource() {
        return R.raw.test1_v1;
    }

    public int getFragmentSource() {
        return R.raw.test1_f1;
    }

    @Override
    public void draw() {
        beginDraw();
        Matrix.setIdentityM(mMVPMatrix,0);
        fillMatrix("uMVP", mMVPMatrix);
        enableVertices("positionVertex", "aPosition");
        enableVertices("coordVertex", "aTexCoord");
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,6,GLES20.GL_UNSIGNED_SHORT,getBuffer("order"));
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        endDraw();
    }




}
