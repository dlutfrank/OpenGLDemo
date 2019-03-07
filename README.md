### 更新记录

#### 最基础api调用 (2019.3.4)
1. AndroidManifest中声明
```xml
    <uses-feature android:glEsVersion="0x00020000" android:required="true"/>
```

如果是3.0则改为0x00030000，3.1改为0x0003001

2. 创建GLSurfaceView，可以通过代码创建，或者直接在布局文件里面编写

```xml
<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
    <android.opengl.GLSurfaceView
        android:id="@+id/gl_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</LinearLayout>
```
3. 创建Render，需要实现GLSurfaceView.Render的接口
```java
public class ShapeRender implements GLSurfaceView.Renderer {
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(1.0f,0.0f,0.0f, 1.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0,0,width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}

```

4. 将Render绑定到GLSurfaceView

```
GLSurfaceView.Renderer render = new ShapeRender();
glSurfaceView.setEGLContextClientVersion(2);
glSurfaceView.setRenderer(render);
```

#### 绘制三角形 (2019.3.5)
1. 定义形状，包含顶点的坐标，颜色等信息

```java
    // 每个顶点包含的坐标个数

    static int COORDS_PER_VERTEX = 3;
    
    static float coords[] = {
            0.0f, 0.618f, 0.0f,
            -0.382f, -0.382f, 0.0f,
            0.382f, -0.382f, 0.0f,
    };
    
    float color[] = { 0.0f, 1.0f, 0.0f, 1.0f};
```

OpenGL中的坐标系以屏幕中心为原点，向右为x正方向，向左为x负方向。向上为y轴正方向，向下为y轴负方向。
垂直屏幕向外为z轴正方向。因此左上角的坐标为(-1,1,0)，右下角的坐标为(1,-1,0)。绘制图像的时候使用
的是逆时针方向绘制。

为了提升效率，会将坐标放入ByteBuffer的缓冲区里面

```java
    private FloatBuffer vertexBuffer;
    
    public Triangle() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(coords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
    }
```

2. 绘制形状

绘制形状前，得准备好顶点着色器和片源着色器以及OpenGL es对象

```java
public class Triangle {

    private final String vertexShaderCode =
        "attribute vec4 vPosition;" +
        "void main() {" +
        "  gl_Position = vPosition;" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "void main() {" +
        "  gl_FragColor = vColor;" +
        "}";

    ...
}
```

`vertexShaderCode`和`fragmentShaderCode`都是OpenGL Shading Language(OGSL)语言，需要先编译，才能使用，
可以在Render方法里面实现一个工具类对shader code进行编译。

```java
public static int loadShader(int type, String shaderCode){

    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
    int shader = GLES20.glCreateShader(type);

    // add the source code to the shader and compile it
    GLES20.glShaderSource(shader, shaderCode);
    GLES20.glCompileShader(shader);

    return shader;
}
```

为了绘制出图形，需要先编辑shader code，然后和OpenGL es的程序对象链接起来，这些工作都只需要做一遍。

```java
    private final int mProgram;

    public Triangle() {
        // ...

        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                        vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                        fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }
```

针对形状进行绘制

```java
private int positionHandle;
private int colorHandle;

private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

public void draw() {
    // Add program to OpenGL ES environment
    GLES20.glUseProgram(mProgram);

    // get handle to vertex shader's vPosition member
    positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

    // Enable a handle to the triangle vertices
    GLES20.glEnableVertexAttribArray(positionHandle);

    // Prepare the triangle coordinate data
    GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                                 GLES20.GL_FLOAT, false,
                                 vertexStride, vertexBuffer);

    // get handle to fragment shader's vColor member
    colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

    // Set color for drawing the triangle
    GLES20.glUniform4fv(colorHandle, 1, color, 0);

    // Draw the triangle
    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

    // Disable vertex array
    GLES20.glDisableVertexAttribArray(positionHandle);
}
```

最后在Render的onDrawFrame方法中调用draw方法即可。

```java
public void onDrawFrame(GL10 unused) {
    // ...

    triangle.draw();
}
```
#### 绘制四边形 (2019.3.7)
Open GL ES中的所有图形都是由点、线、三角形构成，绘制四边形是通过绘制三角形来实现的，所以绘制四边形大部分和绘制三角
形是一样的。这里仅仅介绍和绘制三角形不一样的地方。

1、定义绘制顺序，左上角为0点，沿逆时针递增
```java
    static short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
```

2、将绘制顺序写入缓冲区

```java
private ShortBuffer drawListBuffer;

public Square(float[] coords, float[] color) {
    super(coords, color);
    ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
    dlb.order(ByteOrder.nativeOrder());
    drawListBuffer = dlb.asShortBuffer();
    drawListBuffer.put(drawOrder);
    drawListBuffer.position(0);

    // ...
}
```

3、绘制四边形

```java
GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, 
                      GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
```

#### 投影和相机视图 (2019.3.7)
