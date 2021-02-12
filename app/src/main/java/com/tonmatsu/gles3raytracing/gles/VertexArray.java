package com.tonmatsu.gles3raytracing.gles;

import static android.opengl.GLES31.*;

public class VertexArray {
    private static VertexArray binded;
    private final int[] arrays = new int[1];

    public VertexArray() {
        glGenVertexArrays(1, arrays, 0);
    }

    public void bind() {
        if (binded == this)
            return;
        glBindVertexArray(arrays[0]);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindVertexArray(GL_NONE);
        binded = null;
    }

    public void setAttributes(VertexArrayAttribute... attributes) {
        int stride = 0;
        for (final VertexArrayAttribute attribute : attributes)
            stride += attribute.stride;
        int offset = 0;
        bind();
        for (int i = 0; i < attributes.length; i++) {
            final VertexArrayAttribute attribute = attributes[i];
            attribute.vertexBuffer.bind();
            glVertexAttribPointer(i, attribute.size, attribute.type, attribute.normalized, stride, offset);
            glEnableVertexAttribArray(i);
            offset += attribute.stride;
        }
        unbind();
        glBindBuffer(GL_ARRAY_BUFFER, GL_NONE);
    }
}
