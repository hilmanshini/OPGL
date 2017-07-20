attribute vec2 vPosition;
attribute vec2 vTexCoord;
attribute vec4 vColor;
uniform mat4 uMVP;
varying vec2 texCoord;
varying vec4 color;
void main() {
  texCoord = vTexCoord;
  color = vColor;
  gl_Position = uMVP*vec4 ( vPosition.x, vPosition.y, 0.0, 1.0 );
}