package imageogl.view.opengl_x;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.media.MediaPlayer;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

@SuppressLint({"ViewConstructor"})
public class VideoSurfaceView extends GLSurfaceView {
    private MediaPlayer mMediaPlayer = null;
    VideoRender mRenderer;

    private static class VideoRender implements OnFrameAvailableListener, Renderer {
        private static final int FLOAT_SIZE_BYTES = 4;
        private static int GL_TEXTURE_EXTERNAL_OES = 36197;
        private static String TAG = "VideoRender";
        private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
        private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 20;
        private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
        private final String mFragmentShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
        private float[] mMVPMatrix = new float[16];
        private MediaPlayer mMediaPlayer;
        private int mProgram;
        private float[] mSTMatrix = new float[16];
        private SurfaceTexture mSurface;
        private int mTextureID;
        private FloatBuffer mTriangleVertices = ByteBuffer.allocateDirect(this.mTriangleVerticesData.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        private final float[] mTriangleVerticesData = new float[]{-1.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};
        private final String mVertexShader = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
        private int maPositionHandle;
        private int maTextureHandle;
        private int muMVPMatrixHandle;
        private int muSTMatrixHandle;
        private boolean updateSurface = false;

        public VideoRender(Context context) {
            this.mTriangleVertices.put(this.mTriangleVerticesData).position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
            Matrix.setIdentityM(this.mSTMatrix, TRIANGLE_VERTICES_DATA_POS_OFFSET);
        }

        public void setMediaPlayer(MediaPlayer player) {
            this.mMediaPlayer = player;
        }

        public void onDrawFrame(GL10 glUnused) {
            synchronized (this) {
                if (this.updateSurface) {
                    this.mSurface.updateTexImage();
                    this.mSurface.getTransformMatrix(this.mSTMatrix);
                    this.updateSurface = false;
                }
            }
            GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
            GLES20.glClear(16640);
            GLES20.glUseProgram(this.mProgram);
            checkGlError("glUseProgram");
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, this.mTextureID);
            this.mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
            GLES20.glVertexAttribPointer(this.maPositionHandle, TRIANGLE_VERTICES_DATA_UV_OFFSET, 5126, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, this.mTriangleVertices);
            checkGlError("glVertexAttribPointer maPosition");
            GLES20.glEnableVertexAttribArray(this.maPositionHandle);
            checkGlError("glEnableVertexAttribArray maPositionHandle");
            this.mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
            GLES20.glVertexAttribPointer(this.maTextureHandle, TRIANGLE_VERTICES_DATA_UV_OFFSET, 5126, false, TRIANGLE_VERTICES_DATA_STRIDE_BYTES, this.mTriangleVertices);
            checkGlError("glVertexAttribPointer maTextureHandle");
            GLES20.glEnableVertexAttribArray(this.maTextureHandle);
            checkGlError("glEnableVertexAttribArray maTextureHandle");
            Matrix.setIdentityM(this.mMVPMatrix, TRIANGLE_VERTICES_DATA_POS_OFFSET);
            GLES20.glUniformMatrix4fv(this.muMVPMatrixHandle, 1, false, this.mMVPMatrix, TRIANGLE_VERTICES_DATA_POS_OFFSET);
            GLES20.glUniformMatrix4fv(this.muSTMatrixHandle, 1, false, this.mSTMatrix, TRIANGLE_VERTICES_DATA_POS_OFFSET);
            GLES20.glDrawArrays(5, TRIANGLE_VERTICES_DATA_POS_OFFSET, FLOAT_SIZE_BYTES);
            checkGlError("glDrawArrays");
            GLES20.glFinish();
        }

        public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        }

        public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
            this.mProgram = createProgram("uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n", "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n");
            if (this.mProgram != 0) {
                this.maPositionHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
                checkGlError("glGetAttribLocation aPosition");
                if (this.maPositionHandle == -1) {
                    throw new RuntimeException("Could not get attrib location for aPosition");
                }
                this.maTextureHandle = GLES20.glGetAttribLocation(this.mProgram, "aTextureCoord");
                checkGlError("glGetAttribLocation aTextureCoord");
                if (this.maTextureHandle == -1) {
                    throw new RuntimeException("Could not get attrib location for aTextureCoord");
                }
                this.muMVPMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uMVPMatrix");
                checkGlError("glGetUniformLocation uMVPMatrix");
                if (this.muMVPMatrixHandle == -1) {
                    throw new RuntimeException("Could not get attrib location for uMVPMatrix");
                }
                this.muSTMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uSTMatrix");
                checkGlError("glGetUniformLocation uSTMatrix");
                if (this.muSTMatrixHandle == -1) {
                    throw new RuntimeException("Could not get attrib location for uSTMatrix");
                }
                int[] textures = new int[1];
                GLES20.glGenTextures(1, textures, TRIANGLE_VERTICES_DATA_POS_OFFSET);
                this.mTextureID = textures[TRIANGLE_VERTICES_DATA_POS_OFFSET];
                GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, this.mTextureID);
                checkGlError("glBindTexture mTextureID");
                GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, 10241, 9728.0f);
                GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, 10240, 9729.0f);
                this.mSurface = new SurfaceTexture(this.mTextureID);
                this.mSurface.setOnFrameAvailableListener(this);
                Surface surface = new Surface(this.mSurface);
                this.mMediaPlayer.setSurface(surface);
                this.mMediaPlayer.setScreenOnWhilePlaying(true);
                surface.release();
                try {
                    this.mMediaPlayer.prepare();
                } catch (IOException e) {
                    Log.e(TAG, "media player prepare failed");
                }
                synchronized (this) {
                    this.updateSurface = false;
                }
                this.mMediaPlayer.start();
            }
        }

        public synchronized void onFrameAvailable(SurfaceTexture surface) {
            this.updateSurface = true;
        }

        private int loadShader(int shaderType, String source) {
            int shader = GLES20.glCreateShader(shaderType);
            if (shader == 0) {
                return shader;
            }
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, 35713, compiled, TRIANGLE_VERTICES_DATA_POS_OFFSET);
            if (compiled[TRIANGLE_VERTICES_DATA_POS_OFFSET] != 0) {
                return shader;
            }
            Log.e(TAG, "Could not compile shader " + shaderType + ":");
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return TRIANGLE_VERTICES_DATA_POS_OFFSET;
        }

        private int createProgram(String vertexSource, String fragmentSource) {
            int vertexShader = loadShader(35633, vertexSource);
            if (vertexShader == 0) {
                return TRIANGLE_VERTICES_DATA_POS_OFFSET;
            }
            int pixelShader = loadShader(35632, fragmentSource);
            if (pixelShader == 0) {
                return TRIANGLE_VERTICES_DATA_POS_OFFSET;
            }
            int program = GLES20.glCreateProgram();
            if (program == 0) {
                return program;
            }
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, 35714, linkStatus, TRIANGLE_VERTICES_DATA_POS_OFFSET);
            if (linkStatus[TRIANGLE_VERTICES_DATA_POS_OFFSET] == 1) {
                return program;
            }
            Log.e(TAG, "Could not link program: ");
            Log.e(TAG, GLES20.glGetProgramInfoLog(program));
            GLES20.glDeleteProgram(program);
            return TRIANGLE_VERTICES_DATA_POS_OFFSET;
        }

        private void checkGlError(String op) {
            int error = GLES20.glGetError();
            if (error != 0) {
                Log.e(TAG, op + ": glError " + error);
                throw new RuntimeException(op + ": glError " + error);
            }
        }
    }

    public VideoSurfaceView(Context context, MediaPlayer mp) {
        super(context);
        setEGLContextClientVersion(2);
        this.mMediaPlayer = mp;
        this.mRenderer = new VideoRender(context);
        setRenderer(this.mRenderer);
    }

    public void onResume() {
        queueEvent(new Runnable() {
            public void run() {
                VideoSurfaceView.this.mRenderer.setMediaPlayer(VideoSurfaceView.this.mMediaPlayer);
            }
        });
        super.onResume();
    }
}
