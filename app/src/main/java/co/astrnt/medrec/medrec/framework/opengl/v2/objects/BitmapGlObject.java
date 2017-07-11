package co.astrnt.medrec.medrec.framework.opengl.v2.objects;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import co.astrnt.medrec.medrec.BuildConfig;
import imageogl.view.opengl_x.GLProgram;

public class BitmapGlObject extends SimpleGlObject {
    Resources resources;
    int textureActiveId;

    public BitmapGlObject(GLProgram glProgram, Resources resources, int resourcesId) {
        super(glProgram);
        this.resources = resources;
        this.textureActiveId = loadTexture(resourcesId);
    }

    public void release() {
        GLES20.glBindTexture(3553, 0);
    }

    private int loadTexture(int resourceId) {
        int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        Log.e("TEXTT2", BuildConfig.FLAVOR + textureHandle[0]);
        if (textureHandle[0] != 0) {
            Options options = new Options();
            options.inScaled = false;
            Bitmap bitmap = BitmapFactory.decodeResource(this.resources, resourceId, options);
            GLES20.glBindTexture(3553, textureHandle[0]);
            GLES20.glTexParameteri(3553, 10241, 9728);
            GLES20.glTexParameteri(3553, 10240, 9728);
            GLES20.glTexParameterf(3553, 10242, 33071.0f);
            GLES20.glTexParameterf(3553, 10243, 33071.0f);
            GLUtils.texImage2D(3553, 0, bitmap, 0);
            bitmap.recycle();
        }
        if (textureHandle[0] != 0) {
            return textureHandle[0];
        }
        throw new RuntimeException("Error loading texture.");
    }

    public void unbind() {
        GLES20.glBindTexture(3553, 0);
    }
}
