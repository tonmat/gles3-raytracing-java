package com.tonmatsu.gles3raytracing;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

import com.tonmatsu.gles3raytracing.commons.Ticker;
import com.tonmatsu.gles3raytracing.commons.Timer;
import com.tonmatsu.gles3raytracing.core.Scene;

import org.joml.Quaternionf;
import org.joml.Vector2f;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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

        final SensorManager sensorManager = context.getSystemService(SensorManager.class);
        final Sensor rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        sensorManager.registerListener(new SensorEventListener() {
            private final Quaternionf rotation = new Quaternionf();

            @Override
            public void onSensorChanged(SensorEvent event) {
                rotation.set(event.values[1], -event.values[0], event.values[2], event.values[3]);
                scene.onRotationChanged(rotation);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);

        setOnTouchListener(new OnTouchListener() {
            private final Vector2f position = new Vector2f();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                position.set(event.getX(), event.getY());
                scene.onTouch(event);
                return true;
            }
        });
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
}
