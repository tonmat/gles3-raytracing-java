package com.tonmatsu.gles3raytracing.core;

import android.view.*;

import com.tonmatsu.gles3raytracing.commons.*;
import com.tonmatsu.gles3raytracing.gles.*;
import com.tonmatsu.gles3raytracing.utils.*;

import org.joml.Math;
import org.joml.*;

import java.nio.*;

import static android.opengl.GLES31.*;
import static com.tonmatsu.gles3raytracing.gles.VertexArrayAttribute.*;

public class Scene {
    private static final int RESOLUTION = 2;
    private final Vector2i viewport = new Vector2i();
    private final Quaternionf rotation = new Quaternionf();
    private final Vector2f velocity = new Vector2f();
    private final Vector3f position = new Vector3f();
    private final Matrix4f view = new Matrix4f();
    private final Vector3f forward = new Vector3f();
    private final Vector3f right = new Vector3f();

    private Sphere sphere;
    private PrimitiveManager primitiveManager;
    private Light light;
    private LightManager lightManager;

    private Texture rayTracingTexture;
    private ShaderProgram rayTracingShaderProgram;

    private VertexBuffer simpleVertexBuffer;
    private VertexArray simpleVertexArray;
    private ShaderProgram simpleShaderProgram;

    public void onCreate() {
        createPrimitiveManager();
        createLightManager();

        createRayTracingTexture();
        createRayTracingShaderProgram();

        createSimpleVertexBuffer();
        createSimpleVertexArray();
        createSimpleShaderProgram();

        glClearColor(0.1f, 0.12f, 0.14f, 1.0f);
    }

    public void onRotationChanged(Quaternionf rotation) {
        synchronized (this.rotation) {
            this.rotation.identity()
                    .rotationX(3.14159265358979323846f / 2.0f)
                    .mul(rotation);
        }
    }

    public void onTouch(MotionEvent event) {
        synchronized (velocity) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    this.velocity.x = (2.0f * event.getX() - viewport.x) / viewport.x;
                    this.velocity.y = -(2.0f * event.getY() - viewport.y) / viewport.y;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    this.velocity.zero();
                    break;
            }
        }
    }

    public void onUpdate(Ticker ticker) {
        forward.set(0, 0, 1).mulDirection(view);
        right.set(1, 0, 0).mulDirection(view);
        synchronized (velocity) {
            final float f = velocity.y;
            final float r = velocity.x;
            if (Math.abs(f) > 0.2f)
                this.position.fma(32.0f * f * ticker.delta, forward);
            if (Math.abs(r) > 0.2f)
                this.position.fma(8.0f * r * ticker.delta, right);
        }

        this.view.identity();
        this.view.translate(position);
        synchronized (rotation) {
            this.view.rotate(rotation);
        }

        //

        light.position.x = (float) Math.sin(ticker.elapsedTime) * 10.0f;
        light.position.z = (float) Math.cos(ticker.elapsedTime) * 10.0f;

        sphere.position.y = (float) Math.sin(ticker.elapsedTime * 0.77f) * 2.0f;

        lightManager.update();
        primitiveManager.update();
    }

    public void onViewportResized(int width, int height) {
        glViewport(0, 0, width, height);
        viewport.set(width, height);
        rayTracingTexture.setStorage(1, GL_RGBA16F, viewport.x / RESOLUTION, viewport.y / RESOLUTION);
        rayTracingTexture.bindImageTexture(0, GL_WRITE_ONLY, GL_RGBA16F);
    }

    public void onRender() {
        glClear(GL_COLOR_BUFFER_BIT);

        renderRayTracingShaderProgram();
        renderSimpleShaderProgram();
    }

    private void createPrimitiveManager() {
        primitiveManager = new PrimitiveManager(6);

        final Box floor = new Box();
        floor.position.set(0.0f, -2.0f, 0.0f);
        floor.size.set(100.0f, 1.0f, 100.0f);
        floor.color.set(1.0f, 1.0f, 1.0f);
        floor.reflection = 0.1f;

        final Box box1 = new Box();
        box1.position.set(0.0f, 0.0f, -10.0f);
        box1.size.set(10.0f, 10.0f, 1.0f);
        box1.color.set(0.0f, 0.0f, 0.0f);
        box1.reflection = 0.9f;

        final Box box2 = new Box();
        box2.position.set(0.0f, 0.0f, 20.0f);
        box2.size.set(10.0f, 10.0f, 1.0f);
        box2.color.set(0.0f, 0.0f, 0.0f);
        box2.reflection = 0.9f;

        final Sphere sphere1 = new Sphere();
        sphere1.position.set(-2.0f, -0.1f, 7.0f);
        sphere1.radius = 1.0f;
        sphere1.color.set(1.0f, 0.1f, 0.1f);
        sphere1.reflection = 0.5f;

        final Sphere sphere2 = new Sphere();
        sphere2.position.set(0.0f, 0.1f, 8.0f);
        sphere2.radius = 1.0f;
        sphere2.color.set(0.1f, 1.0f, 0.1f);
        sphere2.reflection = 0.5f;

        final Sphere sphere3 = new Sphere();
        sphere3.position.set(2.0f, 0.1f, 9.0f);
        sphere3.radius = 1.0f;
        sphere3.color.set(0.1f, 0.1f, 0.9f);
        sphere3.reflection = 0.5f;

        sphere = sphere2;

        primitiveManager.add(floor);
        primitiveManager.add(box1);
        primitiveManager.add(box2);
        primitiveManager.add(sphere1);
        primitiveManager.add(sphere2);
        primitiveManager.add(sphere3);
    }

    private void createLightManager() {
        lightManager = new LightManager(4);

        final Light light1 = new Light();
        light1.position.set(-8.0f, 4.0f, 4.0f);
        light1.color.set(0.9f, 0.5f, 0.1f);
        light1.attenuation.set(0.09f, 0.032f);

        final Light light2 = new Light();
        light2.position.set(0.0f, 4.0f, 0.0f);
        light2.color.set(0.9f, 0.9f, 0.9f);
        light2.attenuation.set(0.09f, 0.032f);

        final Light light3 = new Light();
        light3.position.set(8.0f, 4.0f, 4.0f);
        light3.color.set(0.1f, 0.5f, 0.9f);
        light3.attenuation.set(0.09f, 0.032f);

        light = light2;

        lightManager.add(light1);
        lightManager.add(light2);
        lightManager.add(light3);
    }

    private void createRayTracingTexture() {
        rayTracingTexture = new Texture();
    }

    private void createRayTracingShaderProgram() {
        rayTracingShaderProgram = new ShaderProgram();
        rayTracingShaderProgram.attachShader(GL_COMPUTE_SHADER, "shaders/raytracing.cs.glsl");
        rayTracingShaderProgram.link();
    }

    private void createSimpleShaderProgram() {
        simpleShaderProgram = new ShaderProgram();
        simpleShaderProgram.attachShader(GL_VERTEX_SHADER, "shaders/simple.vs.glsl");
        simpleShaderProgram.attachShader(GL_FRAGMENT_SHADER, "shaders/simple.fs.glsl");
        simpleShaderProgram.link();
        simpleShaderProgram.setUniform1f("u_exposure", 1.0f);
        simpleShaderProgram.setUniform1f("u_gamma", 2.2f);
    }

    private void createSimpleVertexBuffer() {
        final FloatBuffer data = BufferUtils.floats(
                -1, -1, 0, 1,
                +1, -1, 1, 1,
                +1, +1, 1, 0,

                +1, +1, 1, 0,
                -1, +1, 0, 0,
                -1, -1, 0, 1);
        simpleVertexBuffer = new VertexBuffer(data.limit() * 4, GL_STATIC_DRAW);
        simpleVertexBuffer.update(data);
    }

    private void createSimpleVertexArray() {
        simpleVertexArray = new VertexArray();
        simpleVertexArray.setAttributes(
                vec2(simpleVertexBuffer),
                vec2(simpleVertexBuffer));
    }

    private void renderRayTracingShaderProgram() {
        rayTracingShaderProgram.bind();
        rayTracingShaderProgram.setUniformMatrix4f("u_view", view);
        glDispatchCompute(viewport.x / (8 * RESOLUTION), viewport.y / (8 * RESOLUTION), 1);
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT | GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
        rayTracingShaderProgram.unbind();
    }

    private void renderSimpleShaderProgram() {
        rayTracingTexture.bind();
        simpleShaderProgram.bind();
        simpleVertexArray.bind();
        glDrawArrays(GL_TRIANGLES, 0, 6);
        simpleVertexArray.unbind();
        simpleShaderProgram.unbind();
        rayTracingTexture.unbind();
    }
}
