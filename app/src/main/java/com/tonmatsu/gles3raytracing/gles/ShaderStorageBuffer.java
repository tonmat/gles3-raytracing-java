package com.tonmatsu.gles3raytracing.gles;

import java.nio.*;

import static android.opengl.GLES31.*;

public class ShaderStorageBuffer {
    private static ShaderStorageBuffer binded;
    private final int[] buffers = new int[1];

    public ShaderStorageBuffer(int size, int usage) {
        glGenBuffers(1, buffers, 0);
        bind();
        glBufferData(GL_SHADER_STORAGE_BUFFER, size, null, usage);
        unbind();
    }

    public void bind() {
        if (binded == this)
            return;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffers[0]);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, GL_NONE);
        binded = null;
    }

    public void update(ByteBuffer data) {
        bind();
        glBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, data.limit(), data);
        unbind();
    }

    public void bindBufferBase(int index) {
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, index, buffers[0]);
    }
}

