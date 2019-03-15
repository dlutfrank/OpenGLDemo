uniform mat4 vpMatrix;
attribute vec4 vPosition;
varying vec4 vColor;
void main() {
    gl_Position = vpMatrix*vPosition;
    float color;
    if(vPosition.z > 0.0){
        color=vPosition.z;
    }else{
        color=-vPosition.z;
    }
    vColor=vec4(color,color,color,1.0);
}
