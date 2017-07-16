package co.astrnt.medrec.medrec.framework.opengl.v3.type;

/**
 * Created by hill on 7/16/17.
 */

public abstract class Object2D {
    int x,y,width,height;

    public Object2D() {
    }

    public void setProgramPointer(int programPointer) {
        this.programPointer = programPointer;
    }

    public Object2D(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    int programPointer;

    public int getProgramPointer() {
        return programPointer;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    int vertexSource;
    int fragmentSource;

    public Object2D(int x, int y, int width, int height, int vertexSource, int fragmentSource) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.vertexSource = vertexSource;
        this.fragmentSource = fragmentSource;
    }

    public int getVertexSource() {
        return vertexSource;
    }

    public int getFragmentSource() {
        return fragmentSource;
    }
    int vertexShaderPointer;
    int fragmentShaderPointer;

    public int getVertexShaderPointer() {
        return vertexShaderPointer;
    }

    public void setVertexShaderPointer(int vertexShaderPointer) {
        this.vertexShaderPointer = vertexShaderPointer;
    }

    public int getFragmentShaderPointer() {
        return fragmentShaderPointer;
    }

    public void setFragmentShaderPointer(int fragmentShaderPointer) {
        this.fragmentShaderPointer = fragmentShaderPointer;
    }
    public abstract void draw();
}
