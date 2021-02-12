package com.tonmatsu.gles3raytracing.gles;

import java.nio.*;

import static android.opengl.GLES31.*;

public class VertexBuffer {
    private static VertexBuffer binded;
    private final int[] buffers = new int[1];

    public VertexBuffer(int size, int usage) {
        glGenBuffers(1, buffers, 0);
        bind();
        glBufferData(GL_ARRAY_BUFFER, size, null, usage);
        unbind();
    }

    public void bind() {
        if (binded == this)
            return;
        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);
        binded = null;
    }

    public void update(FloatBuffer data) {
        bind();
        glBufferSubData(GL_ARRAY_BUFFER, 0, data.limit() * 4, data);
        unbind();
    }
}

