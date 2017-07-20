package imageogl.view.opengl_x;

import android.content.res.Resources;
import android.opengl.Matrix;

import co.astrnt.medrec.medrec.R;
import co.astrnt.medrec.medrec.framework.opengl.v2.objects.SimpleGlObject;

public class DrawerTest {
    int frame = 0;
    private final GLProgram glProgram;
    private final SimpleGlObject simpleGlObject;

    public DrawerTest(int width, int height, Resources resources) {
        this.glProgram = new GLProgram(R.raw.script_test1_v, R.raw.script_test1_f, resources);
        this.glProgram.allocate("sampleVertice1", 0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 0.0f);
        this.simpleGlObject = new SimpleGlObject(this.glProgram);
        this.glProgram.addObject(this.simpleGlObject);
    }

    public void draw(int q) {
        this.frame++;
        this.glProgram.clear();
        if (q % 2 == 0) {
            this.glProgram.clear(1.0f, 1.0f, 1.0f);
        } else {
            this.glProgram.clear(0.0f, 0.0f, 0.0f);
        }
        this.glProgram.onDrawFrame(0.0f, 0.0f,1,1,1);
        this.glProgram.beginDraw();
        Matrix.setIdentityM(this.simpleGlObject.mMVPMatrix, 0);
        this.simpleGlObject.fillUniformMatrix("uMVP", this.simpleGlObject.mMVPMatrix);
        this.simpleGlObject.fillUniform("uColor", 0.3f, 0.3f, 0.3f, 1.0f);
        this.simpleGlObject.enableVertices("sampleVertice1", "aPosition");
        this.simpleGlObject.draw(3);
        this.glProgram.endDraw();
    }
}
