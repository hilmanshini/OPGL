#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES sTexture;
varying vec2 texCoord;

void main() {

    vec4 texture = texture2D(sTexture,texCoord);

    gl_FragColor = texture;

}
