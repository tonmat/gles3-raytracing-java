package com.tonmatsu.gles3raytracing.gles;

import static android.opengl.GLES31.*;

public class VertexArrayAttribute {
    public final VertexBuffer vertexBuffer;
    public final int size;
    public final int type;
    public final boolean normalized;
    public final int stride;

    public VertexArrayAttribute(VertexBuffer vertexBuffer, int size, int type, boolean normalized, int stride) {
        this.vertexBuffer = vertexBuffer;
        this.size = size;
        this.type = type;
        this.normalized = normalized;
        this.stride = stride;
    }

    public static VertexArrayAttribute float1(VertexBuffer vertexBuffer) {
        return new VertexArrayAttribute(vertexBuffer, 1, GL_FLOAT, false, 4);
    }

    public static VertexArrayAttribute vec2(VertexBuffer vertexBuffer) {
        return new VertexArrayAttribute(vertexBuffer, 2, GL_FLOAT, false, 8);
    }

    public static VertexArrayAttribute vec3(VertexBuffer vertexBuffer) {
        return new VertexArrayAttribute(vertexBuffer, 3, GL_FLOAT, false, 12);
    }

    public static VertexArrayAttribute vec4(VertexBuffer vertexBuffer) {
        return new VertexArrayAttribute(vertexBuffer, 4, GL_FLOAT, false, 16);
    }
}
