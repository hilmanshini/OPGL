precision mediump float;
uniform vec4 uColor;
uniform sampler2D sTexture;
varying vec2 pos;
void main(){
    gl_FragColor = texture2D(sTexture,pos);
}