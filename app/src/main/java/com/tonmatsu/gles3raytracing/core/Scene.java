package com.tonmatsu.gles3raytracing.core;

import com.tonmatsu.gles3raytracing.commons.Ticker;
import com.tonmatsu.gles3raytracing.gles.ShaderProgram;
import com.tonmatsu.gles3raytracing.gles.ShaderStorageBuffer;
import com.tonmatsu.gles3raytracing.gles.Texture;
import com.tonmatsu.gles3raytracing.gles.VertexArray;
import com.tonmatsu.gles3raytracing.gles.VertexBuffer;
import com.tonmatsu.gles3raytracing.utils.BufferUtils;

import org.joml.Vector2i;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static android.opengl.GLES31.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES31.GL_COMPUTE_SHADER;
import static android.opengl.GLES31.GL_FRAGMENT_SHADER;
import static android.opengl.GLES31.GL_RGBA16F;
import static android.opengl.GLES31.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;
import static android.opengl.GLES31.GL_SHADER_STORAGE_BARRIER_BIT;
import static android.opengl.GLES31.GL_STATIC_DRAW;
import static android.opengl.GLES31.GL_TRIANGLES;
import static android.opengl.GLES31.GL_VERTEX_SHADER;
import static android.opengl.GLES31.GL_WRITE_ONLY;
import static android.opengl.GLES31.glClear;
import static android.opengl.GLES31.glClearColor;
import static android.opengl.GLES31.glDispatchCompute;
import static android.opengl.GLES31.glDrawArrays;
import static android.opengl.GLES31.glMemoryBarrier;
import static android.opengl.GLES31.glViewport;
import static com.tonmatsu.gles3raytracing.gles.VertexArrayAttribute.vec2;

public class Scene {
    private final Vector2i viewport = new Vector2i();

    private ShaderStorageBuffer primitivesBuffer;
    private ShaderStorageBuffer lightsBuffer;
    private Texture rayTracingTexture;
    private ShaderProgram rayTracingShaderProgram;

    private VertexBuffer simpleVertexBuffer;
    private VertexArray simpleVertexArray;
    private ShaderProgram simpleShaderProgram;

    public void onCreate() {
        createPrimitivesBuffer();
        createLightsBuffer();

        createRayTracingTexture();
        createRayTracingShaderProgram();

        createSimpleVertexBuffer();
        createSimpleVertexArray();
        createSimpleShaderProgram();

        glClearColor(0.1f, 0.12f, 0.14f, 1.0f);
    }

    public void onUpdate(Ticker ticker) {
    }

    public void onViewportResized(int width, int height) {
        glViewport(0, 0, width, height);
        viewport.set(width, height);
        rayTracingTexture.setStorage(1, GL_RGBA16F, viewport.x, viewport.y);
        rayTracingTexture.bindImageTexture(0, GL_WRITE_ONLY, GL_RGBA16F);
    }

    public void onRender() {
        glClear(GL_COLOR_BUFFER_BIT);

        renderRayTracingShaderProgram();
        renderSimpleShaderProgram();
    }

    private void createPrimitivesBuffer() {
        final ByteBuffer data = BufferUtils.allocBytes(12 * 4 * 3 + 4);

        data.putInt(3);

        data.putInt(1);
        data.putFloat(-2.0f).putFloat(-0.1f).putFloat(7.0f);
        data.putFloat(1.0f).putFloat(1.0f).putFloat(1.0f);
        data.putFloat(1.0f).putFloat(0.1f).putFloat(0.1f);
        data.putFloat(0.9f);
        data.putFloat(0.0f);

        data.putInt(1);
        data.putFloat(0.0f).putFloat(0.0f).putFloat(8.0f);
        data.putFloat(1.0f).putFloat(1.0f).putFloat(1.0f);
        data.putFloat(0.1f).putFloat(1.0f).putFloat(0.1f);
        data.putFloat(0.9f);
        data.putFloat(0.0f);

        data.putInt(1);
        data.putFloat(2.0f).putFloat(0.1f).putFloat(9.0f);
        data.putFloat(1.0f).putFloat(1.0f).putFloat(1.0f);
        data.putFloat(0.1f).putFloat(0.1f).putFloat(1.0f);
        data.putFloat(0.9f);
        data.putFloat(0.0f);

        data.flip();

        primitivesBuffer = new ShaderStorageBuffer(data.limit(), GL_STATIC_DRAW);
        primitivesBuffer.update(data);
        primitivesBuffer.bindBufferBase(1);
    }

    private void createLightsBuffer() {
        final ByteBuffer data = BufferUtils.allocBytes(8 * 4 * 3 + 4);

        data.putInt(3);

        data.putFloat(-8.0f).putFloat(4.0f).putFloat(4.0f);
        data.putFloat(0.9f).putFloat(0.5f).putFloat(0.1f);
        data.putFloat(0.09f).putFloat(0.032f);

        data.putFloat(0.0f).putFloat(8.0f).putFloat(4.0f);
        data.putFloat(0.9f).putFloat(0.9f).putFloat(0.9f);
        data.putFloat(0.09f).putFloat(0.032f);

        data.putFloat(8.0f).putFloat(4.0f).putFloat(4.0f);
        data.putFloat(0.1f).putFloat(0.5f).putFloat(0.9f);
        data.putFloat(0.09f).putFloat(0.032f);

        data.flip();

        lightsBuffer = new ShaderStorageBuffer(data.limit(), GL_STATIC_DRAW);
        lightsBuffer.update(data);
        lightsBuffer.bindBufferBase(2);
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
        glDispatchCompute(viewport.x / 8, viewport.y / 8, 1);
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
