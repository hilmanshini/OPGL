package co.astrnt.medrec.medrec.framework.opengl.v1;

import android.content.res.Resources;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Scanner;

/**
 * Created by hill on 6/28/17.
 */

public class Utils {

    public static String loadShaderSource(Resources resources, int id) {
        InputStream is = resources.openRawResource(id);
        Scanner scanner = new Scanner(is);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            String s = scanner.nextLine();
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();

    }
    static public FloatBuffer allocate(float... x) {
        FloatBuffer verticFB = ByteBuffer.allocateDirect(x.length * (Float.SIZE / 8)).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticFB.put(x);
        verticFB.position(0);
        return verticFB;
    }
    static public IntBuffer allocate(int... x) {
        IntBuffer verticFB = ByteBuffer.allocateDirect(x.length * (Integer.SIZE / 8)).order(ByteOrder.nativeOrder()).asIntBuffer();
        verticFB.put(x);
        verticFB.position(0);
        return verticFB;
    }
    static public ShortBuffer allocate(short... drawOrder) {
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2); // (# of coordinate values * 2 bytes per short)
        dlb.order(ByteOrder.nativeOrder());
        ShortBuffer drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);
        return drawListBuffer;
    }
}
