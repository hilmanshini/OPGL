package co.astrnt.medrec.medrec.framework.opengl.v1;

import android.content.res.Resources;
import android.opengl.GLES20;

import java.nio.Buffer;
import java.nio.ShortBuffer;

import co.astrnt.medrec.medrec.R;

/**
 * Created by hill on 6/28/17.
 */

public class RectangleProgram extends BufferedProgram implements GLDrawable{

    public RectangleProgram(Resources mResources) {
        super(mResources);
        if(getLinkStatus() == LINK_SUCCESS){

        }
    }

    @Override
    public int getVertexShaderResourceInt() {
        return R.raw.simple_v;
    }

    @Override
    public int getFragmentShaderResourceInt() {
        return R.raw.simple_f;
    }



    @Override
    public void draw(int size) {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,size);
    }

    @Override
    public void draw(String bufferName,int size) {
        Buffer buffer = bufferMap.get(bufferName);;
        if(buffer instanceof  ShortBuffer){
            ShortBuffer mShortBuffer = (ShortBuffer) bufferMap.get(bufferName);
            GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,size,GLES20.GL_UNSIGNED_SHORT,mShortBuffer);
        }

    }
}
