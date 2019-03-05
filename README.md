### 更新记录

#### 最基础api调用
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

#### 绘制三角形


#### 绘制四边形
