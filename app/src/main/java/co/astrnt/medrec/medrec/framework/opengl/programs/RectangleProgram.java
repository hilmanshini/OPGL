package co.astrnt.medrec.medrec.framework.opengl.programs;

import android.content.res.Resources;

import co.astrnt.medrec.medrec.R;

/**
 * Created by hill on 6/28/17.
 */

public class RectangleProgram extends ScriptedProgram {

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
}
