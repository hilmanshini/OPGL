precision mediump float;
attribute vec4 aPosition;
attribute vec2 aTexCoord;
uniform mat4 uMVP;
varying vec2 pos;

void main(){
    gl_Position = uMVP*aPosition ;
    pos = aTexCoord;
}