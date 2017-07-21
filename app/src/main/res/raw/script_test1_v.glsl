precision mediump float;
attribute vec4 aPosition;
uniform mat4 uMVP;
uniform vec4 uColor;
void main(){
    gl_Position = uMVP*aPosition ;
}