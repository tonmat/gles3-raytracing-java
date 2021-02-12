package com.tonmatsu.gles3raytracing.core;

import com.tonmatsu.gles3raytracing.commons.*;
import com.tonmatsu.gles3raytracing.gles.*;
import com.tonmatsu.gles3raytracing.utils.*;

import org.joml.*;

import static android.opengl.GLES31.*;
import static com.tonmatsu.gles3raytracing.gles.VertexArrayAttribute.*;

public class Scene {
    private final Vector2i viewport = new Vector2i();

    private Texture rayTracingTexture;
    private ShaderProgram rayTracingShaderProgram;

    private VertexBuffer simpleVertexBuffer;
    private VertexArray simpleVertexArray;
    private ShaderProgram simpleShaderProgram;

    public void onCreate() {
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
        simpleVertexBuffer = new VertexBuffer(6 * 4 * 4, GL_STATIC_DRAW);
        simpleVertexBuffer.update(BufferUtils.floats(
                -1, -1, 0, 1,
                +1, -1, 1, 1,
                +1, +1, 1, 0,

                +1, +1, 1, 0,
                -1, +1, 0, 0,
                -1, -1, 0, 1));
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
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
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
