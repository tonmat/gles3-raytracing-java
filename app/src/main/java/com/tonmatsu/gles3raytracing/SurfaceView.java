package com.tonmatsu.gles3raytracing;

import android.content.*;
import android.opengl.*;

import com.tonmatsu.gles3raytracing.commons.*;
import com.tonmatsu.gles3raytracing.core.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.*;

public class SurfaceView extends GLSurfaceView {
    private final Callback callback;
    private final Ticker ticker;
    private final Timer timer;
    private final Scene scene;

    public SurfaceView(Context context, Callback callback) {
        super(context);
        this.callback = callback;
        ticker = new Ticker();
        timer = new Timer(this::handleTimer, 1);
        scene = new Scene();
        setEGLContextClientVersion(3);
        setRenderer(new DefaultRenderer());
        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    private class DefaultRenderer implements Renderer {
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            scene.onCreate();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            scene.onViewportResized(width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            ticker.tick();
            timer.update(ticker.delta);
            scene.onUpdate(ticker);
            scene.onRender();
        }
    }

    private void handleTimer(int id) {
        if (id != 0)
            return;
        final float averageDelta = ticker.getAverageDelta();
        if (averageDelta <= 0.0f)
            return;
        final float averageFPS = 1.0f / averageDelta;
        callback.onAverageFPS(averageFPS);
    }

    public interface Callback {
        void onAverageFPS(float averageFPS);
    }
}
