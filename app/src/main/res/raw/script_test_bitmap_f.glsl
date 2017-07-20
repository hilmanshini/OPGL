precision mediump float;
uniform vec4 uColor;

uniform sampler2D sTexture;
varying vec2 pos;

void main(){
    vec4 tex = texture2D(sTexture,pos);

    gl_FragColor = tex;
}