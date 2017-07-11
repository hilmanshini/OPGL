/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.astrnt.medrec.medrec.framework.opengl.v2;

/**
 *
 * @author hill
 */
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;

import android.util.Log;
import android.view.Surface;

public class CodecInputSurface {
    private static final int EGL_RECORDABLE_ANDROID = 12610;
    private EGLContext mEGLContext = EGL14.EGL_NO_CONTEXT;
    private EGLDisplay mEGLDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLSurface mEGLSurface = EGL14.EGL_NO_SURFACE;
    private Surface mSurface;
    EGLContext sharedContext;

    public CodecInputSurface(Surface surface) {
        if (surface == null) {
            throw new NullPointerException();
        }
        this.mSurface = surface;
        eglSetup();
    }

    public CodecInputSurface(Surface surface, EGLContext eglContext) {
        if (surface == null) {
            throw new NullPointerException();
        }
        this.mSurface = surface;
        this.sharedContext = eglContext;
        eglSetup();
    }

    private void eglSetup() {
        Log.e("EGLX", "SETUP");
        this.mEGLDisplay = EGL14.eglGetDisplay(0);
        if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        }
        int[] version = new int[2];
        if (EGL14.eglInitialize(this.mEGLDisplay, version, 0, version, 1)) {
            EGLConfig[] configs = new EGLConfig[1];
            int i = 0;
            EGL14.eglChooseConfig(this.mEGLDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, EGL_RECORDABLE_ANDROID, 1, 12344}, 0, configs, i, configs.length, new int[1], 0);
            checkEglError("eglCreateContext RGB888+recordable ES2");
            int[] attrib_list = new int[]{12440, 2, 12344};
            if (this.sharedContext == null) {
                this.mEGLContext = EGL14.eglCreateContext(this.mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT, attrib_list, 0);
            } else {
                this.mEGLContext = EGL14.eglCreateContext(this.mEGLDisplay, configs[0], this.sharedContext, attrib_list, 0);
            }
            checkEglError("eglCreateContext");
            this.mEGLSurface = EGL14.eglCreateWindowSurface(this.mEGLDisplay, configs[0], this.mSurface, new int[]{12344}, 0);
            checkEglError("eglCreateWindowSurface");
            return;
        }
        throw new RuntimeException("unable to initialize EGL14");
    }


    public void release() {
        Log.e("EGLX", "RELEASE");
        if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(this.mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(this.mEGLDisplay, this.mEGLSurface);
            EGL14.eglDestroyContext(this.mEGLDisplay, this.mEGLContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(this.mEGLDisplay);
        }
        this.mSurface.release();
        this.mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        this.mEGLContext = EGL14.EGL_NO_CONTEXT;
        this.mEGLSurface = EGL14.EGL_NO_SURFACE;
        this.mSurface = null;
    }

    public void makeCurrent() {
        Log.e("EGLX", "MAKE CURRENT");
        EGL14.eglMakeCurrent(this.mEGLDisplay, this.mEGLSurface, this.mEGLSurface, this.mEGLContext);
        checkEglError("eglMakeCurrent");
    }

    public void makeDefault() {
        Log.e("EGLX", "MAKE DEFAULT");
        EGL14.eglMakeCurrent(EGL14.EGL_NO_DISPLAY, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
        checkEglError("eglMakeCurrent");
    }

    public boolean swapBuffers() {
        Log.e("EGLX", "SWAP");
        boolean result = EGL14.eglSwapBuffers(this.mEGLDisplay, this.mEGLSurface);
        checkEglError("eglSwapBuffers");
        return result;
    }

    public void setPresentationTime(long nsecs) {
        Log.e("EGLX", "SETPERST");
        EGLExt.eglPresentationTimeANDROID(this.mEGLDisplay, this.mEGLSurface, nsecs);
        checkEglError("eglPresentationTimeANDROID");
    }

    private void checkEglError(String msg) {
        if (EGL14.eglGetError() == 12288) {
        }
    }
}

