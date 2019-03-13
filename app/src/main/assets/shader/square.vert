attribute vec4 vPosition;
uniform mat4 vpMatrix;
void main() {
    gl_Position = vpMatrix * vPosition;
}
