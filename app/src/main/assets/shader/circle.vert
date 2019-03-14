uniform mat4 vpMatrix;
attribute vec4 vPosition;
void main() {
    gl_Position = vpMatrix * vPosition;
}
