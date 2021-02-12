package com.tonmatsu.gles3raytracing;

import android.content.*;
import android.hardware.*;
import android.opengl.*;

import com.tonmatsu.gles3raytracing.commons.*;
import com.tonmatsu.gles3raytracing.core.*;

import org.joml.*;

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

        final SensorManager sensorManager = context.getSystemService(SensorManager.class);
        final Sensor rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(new SensorEventListener() {
            private final float[] r = new float[16];
            private final float[] rm = new float[16];
            private final Matrix4f rotationMatrix = new Matrix4f();

            @Override
            public void onSensorChanged(SensorEvent event) {
                SensorManager.getRotationMatrixFromVector(r, event.values);
                SensorManager.remapCoordinateSystem(r,
                        SensorManager.AXIS_MINUS_Y,
                        SensorManager.AXIS_X,
                        rm);
                rotationMatrix.set(
                        rm[0], rm[4], rm[8], rm[12],
                        rm[1], rm[5], rm[9], rm[13],
                        rm[2], rm[6], rm[10], rm[14],
                        rm[3], rm[7], rm[11], rm[15]);
                scene.onRotationMatrixChanged(rotationMatrix);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        }, rotationVectorSensor, SensorManager.SENSOR_DELAY_GAME);
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
