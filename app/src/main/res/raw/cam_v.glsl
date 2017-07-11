attribute vec2 vPosition;
attribute vec2 vTexCoord;
varying vec2 texCoord;
uniform mat4 uMVP;
uniform mat4 uSTm;
void main() {
  texCoord = (uSTm*vec4(vTexCoord.x,vTexCoord.y,0.0,1.0)).xy;
  gl_Position = vec4 ( vPosition.x, vPosition.y, 0.0, 1.0 );
}