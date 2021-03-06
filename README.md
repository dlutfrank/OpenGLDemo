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

    // ...
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

##### 投影

使用OpenGl绘制的3D图形，需要展示在移动端2D设备上，这就是投影。Android OpenGl ES中有两种投影方式：一种是正交投影，一种是透视投影：

正交投影投影物体的带下不会随观察点的远近而发生变化，我们可以使用下面方法来执行正交投影：

```java
Matrix.orthoM (float[] m,           //接收正交投影的变换矩阵
                int mOffset,        //变换矩阵的起始位置（偏移量）
                float left,         //相对观察点近面的左边距
                float right,        //相对观察点近面的右边距
                float bottom,       //相对观察点近面的下边距
                float top,          //相对观察点近面的上边距
                float near,         //相对观察点近面距离
                float far)          //相对观察点远面距离
```

透视投影：随观察点的距离变化而变化，观察点越远，视图越小，反之越大，我们可以通过如下方法来设置透视投影：

```java
Matrix.frustumM (float[] m,         //接收透视投影的变换矩阵
                int mOffset,        //变换矩阵的起始位置（偏移量）
                float left,         //相对观察点近面的左边距
                float right,        //相对观察点近面的右边距
                float bottom,       //相对观察点近面的下边距
                float top,          //相对观察点近面的上边距
                float near,         //相对观察点近面距离
                float far)          //相对观察点远面距离
```
##### 视图
什么是相机视图？简单来说生活中我们拍照，你站的高度，拿相机的位置，姿势不同，拍出来的照片也就不一样，
相机视图就是来修改相机位置，观察方式以及相机的倾斜角度等属性。我们可以通过下面方法来修改相机视图属性：

```java
Matrix.setLookAtM (float[] rm,      //接收相机变换矩阵
                int rmOffset,       //变换矩阵的起始位置（偏移量）
                float eyeX,float eyeY, float eyeZ,   //相机位置
                float centerX,float centerY,float centerZ,  //观察点位置
                float upX,float upY,float upZ)  //up向量在xyz上的分量
```

##### 转换矩阵

转换矩阵用来做什么的呢？是否记得上面我们绘制的图形坐标需要转换为OpenGl中能处理的小端字节序（LittleEdian），
没错，转换矩阵就是用来将数据转为OpenGl ES可用的数据字节，我们将相机视图和投影设置的数据相乘，便得到一个转换矩阵，
然后我们再讲此矩阵传给顶点着色器，具体使用方法及参数说明如下：

```java
Matrix.multiplyMM (float[] result, //接收相乘结果
                int resultOffset,  //接收矩阵的起始位置（偏移量）
                float[] lhs,       //左矩阵
                int lhsOffset,     //左矩阵的起始位置（偏移量）
                float[] rhs,       //右矩阵
                int rhsOffset)     //右矩阵的起始位置（偏移量）
```

#### 绘制圆形

[cicle](https://img-blog.csdn.net/20161014185118697)

圆形可以理解为正多边形，当边的数量足够多的时候，看上去就是圆形了，这里需要介绍一下绘制方式，OpenGL里面绘制一般是通过
glDrawArrays来进行绘制的,绘制连续的三角形为:

```java
GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCoords.length/3);

```

GLES20.glDrawArrays绘制的方法称为顶点法，还有一个方法进行绘制，称为索引法，是根据索引序列，在顶点序列中找到对应的
顶点，并根据绘制的方式，组成相应的图元进行绘制。


顶点法拥有的绘制方式，索引法也都有。相对于顶点法在复杂图形的绘制中无法避免大量顶点重复的情况，索引法可以相对顶点法减
少很多重复顶点占用的空间。

GL_TRIANGLE_FAN代表以第一个顶点进行扇面绘制，其他类型的绘制含义如下：

```java
int GL_POINTS       //将传入的顶点坐标作为单独的点绘制
int GL_LINES        //将传入的坐标作为单独线条绘制，ABCDEFG六个顶点，绘制AB、CD、EF三条线
int GL_LINE_STRIP   //将传入的顶点作为折线绘制，ABCD四个顶点，绘制AB、BC、CD三条线
int GL_LINE_LOOP    //将传入的顶点作为闭合折线绘制，ABCD四个顶点，绘制AB、BC、CD、DA四条线。
int GL_TRIANGLES    //将传入的顶点作为单独的三角形绘制，ABCDEF绘制ABC,DEF两个三角形
int GL_TRIANGLE_FAN    //将传入的顶点作为扇面绘制，ABCDEF绘制ABC、ACD、ADE、AEF四个三角形
int GL_TRIANGLE_STRIP   //将传入的顶点作为三角条带绘制，ABCDEF绘制ABC,BCD,CDE,DEF四个三角形
```

生成顶点坐标：

```java
    public static final int CPV = 3;
    public static float[] createShape(float radius, float h, int n){
        if(n <= 0){
            throw new InvalidParameterException("n is invalid");
        }
        // 中心点 + 最后的重合点
        final int count = n + 2;
        float[] result = new float[count*3];
        int i;
        for(i = 0; i < CPV; i++){
            result[i] = 0.0f;
        }
        result[2] = h;
        double arc = 2.0*Math.PI /n;
        double arcs = 0.0f;
        for(; i < count * CPV; i+=3){
            result[i] = (float) (radius * Math.sin(arcs));
            result[i+1] = (float)(radius * Math.cos(arcs));
            result[i+2] = h;
            arcs += arc;
        }
        return result;
    }
```
生成顶点坐标的时候，中心点的x,y坐标为0，z轴坐标等于高度，其他点的坐标根据角度求出对应的值即可。

#### 圆锥、圆柱
圆锥的锥面同绘制圆形，圆锥的椎体也可以参考圆形的绘制，除了顶点的坐标不在圆上之外，x,y方向的坐标和圆的坐标相同，因此创
见圆锥体顶点坐标的公式为：

```java
    public static float[] createCone(float radius, float h, int n){
        if(n <= 0){
            throw new InvalidParameterException("n is invalid");
        }
        // 中心点 + 最后的重合点
        final int count = n + 2;
        float[] result = new float[count*3];
        int i;
        for(i = 0; i < CPV; i++){
            result[i] = 0.0f;
        }
        result[2] = h;
        double arc = 2.0*Math.PI /n;
        double arcs = 0.0f;
        for(; i < count * CPV; i+=3){
            result[i] = (float) (radius * Math.sin(arcs));
            result[i+1] = (float)(radius * Math.cos(arcs));
            result[i+2] = 0.0f;
            arcs += arc;
        }
        return result;
    }
```

绘制圆锥体的时候，绘制类型同圆形一样，为GL_TRIANGLE_FAN，绘制了后，由于色彩和深度的原因，可能会看不出来是圆锥，需要
设置锥体的颜色以及开启深度测试。

开启深度测试

```java
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background frame color
        GLES20.glClearColor(0.5f,0.5f,0.5f, 0.5f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        ...
    }
    
   public void onDrawFrame(GL10 gl10) {
        // Redraw background color
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        ...
    }
    
```
深度的概念类似于游戏编程里面zOrder，控制物体显示的关系，深度相同的情况下，后添加的view会显示在最前面，有可能会遮挡前
面添加的view，修改深度可以控制显示的顺序。开启深度测试后，不要忘记在每次绘制之前擦除深度相关的数据。

给锥体添加颜色的时候，一般通过顶点坐标向片元传递颜色。

顶点着色器
```glsl
uniform mat4 vpMatrix;
attribute vec4 vPosition;
varying vec4 vColor;
void main() {
    gl_Position = vpMatrix*vPosition;
    if(vPosition.z > 0.2){
        vColor=vec4(0.0,0.0,0.0,1.0);
    }else{
        vColor=vec4(0.9,0.9,0.9,1.0);
    }
}
```

片元着色器
```glsl
precision mediump float;
varying vec4 vColor;
void main() {
    gl_FragColor = vColor;
}
```

片元着色器定义的时候，需要定义精度，一共有lowp（8位）、medump（10位）、highp（16位）三种可选，varying变量代表从
顶点传递数据到片元。attribute表示可变的变量，uniform表示不可变变量。mat4代表4*4的矩阵，vec4代表4个元素的列向量。

gl_Position: 为顶点坐标，gl_FragColor代表当前片元颜色。

#### 球

球上坐标点的生成会更复杂点，可以根据球面坐标系的公式进行计算，OpenGL的坐标系为右手坐标系：

![ball_coord](images/ball_coord.jpg)


如图所示：$\theta$为圆心到点的向量与z轴正方向的夹角，$\phi$为圆心到点的向量在xoy平
面上的投影与x轴正方向的夹角。可以点(x,y,z)的坐标计算公式为：

```
x = r * sin(\theta) * cos(\phi)
y = r * sin(\theta) * sin(\phi)
z = r * cos(\theta)
```

顶点的生成

```java
    public static float[] createBall(float radius, int n) {
        if( n <= 2 || (n % 2) != 0){
            throw new InvalidParameterException("n is invalid");
        }
        final int count = n * (n+1);
        float[] result = new float[count * CPV];
        float arc = (float)(2.0*Math.PI /n);
        float longitude = 0.0f;
        float latitude = 0.0f;
        final int half = n/2;
        final int vertexPerLongitude = 2 * (n + 1) * CPV;
        for(int i = 0; i < half; i++ ){
            latitude = 0.0f;
            int index;
            int j;
            for( j=0;j < 2*n*CPV; j+=6 ){
                index = i * vertexPerLongitude + j;
                result[index] = (float)(radius * Math.sin(longitude) * Math.sin(latitude));
                result[index + 1]  = (float)(radius * Math.sin(longitude) * Math.cos(latitude));
                result[index + 2]  = (float)(radius * Math.cos(longitude));

                result[index + 3] = (float)(radius * Math.sin(longitude+arc) * Math.sin(latitude));
                result[index + 4]  = (float)(radius * Math.sin(longitude+arc) * Math.cos(latitude));
                result[index + 5]  = (float)(radius * Math.cos(longitude+arc));
                latitude += arc;
            }
            // 最后一个闭合节点
            index = i * vertexPerLongitude + j;
            result[index] = (float)(radius * Math.sin(longitude) * Math.sin(0));
            result[index + 1]  = (float)(radius * Math.sin(longitude) * Math.cos(0));
            result[index + 2]  = (float)(radius * Math.cos(longitude));

            result[index + 3] = (float)(radius * Math.sin(longitude+arc) * Math.sin(0));
            result[index + 4]  = (float)(radius * Math.sin(longitude+arc) * Math.cos(0));
            result[index + 5]  = (float)(radius * Math.cos(longitude+arc));

            longitude += arc;
        }
        return result;
    }
```

生成球后，需要对球体的颜色做一些修改，以便看上去真正的像一个球：

```glsl
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
```

最终的效果图为：

![ball](images/ball.png)
