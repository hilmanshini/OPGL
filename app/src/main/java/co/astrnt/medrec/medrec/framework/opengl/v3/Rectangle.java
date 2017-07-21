package co.astrnt.medrec.medrec.framework.opengl.v3;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.opengl.v3.type.ScriptedObject2D;

/**
 * Created by hill on 7/22/17.
 */

public class Rectangle extends ScriptedObject2D {
    public Rectangle(Resources mResources, int a, int r, int g, int b) {
        super(mResources);
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
        Matrix.setIdentityM(ump, 0);
    }

    float[] ump = new float[16];

    @Override
    public void draw() {
        beginDraw();
        enableVertices("simplev", "aPosition");
        enableAlphaChannel();
        Log.e("XCOLOR"," "+a+" "+r+" "+g+" "+b);
        fillUniform4("uColor", (float) r / 100, (float) g / 100f, (float) b / 100, (float) a / 100);
        fillMatrix("uMVP", ump);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,6,GLES20.GL_UNSIGNED_SHORT,getBuffer("order"));
        r++;
        endDraw();
    }

    @Override
    public void onUpdateViewPort(Scene scene, int width, int height, int orientation) {
        addBuffer("simplev", new float[]{1f, 1f, 1f, -1f, -1f, -1f, -1f, 1f});
        addBuffer("order", new short[]{0, 1, 2, 0, 2, 3});

    }

    @Override
    public int getVertexSource() {
        return R.raw.script_test1_v;
    }

    @Override
    public int getFragmentSource() {
        return R.raw.script_test1_f;
    }

    int a, r, g, b;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
