package co.astrnt.medrec.medrec.framework.opengl;

/**
 * Created by hill on 7/17/17.
 */

public interface IDrawer {

    public void onSurfaceChanged(int width, int height);
    public void draw(Object... params);
    public void clear();
    public void onSurfaceCreated();

    void release();
}
