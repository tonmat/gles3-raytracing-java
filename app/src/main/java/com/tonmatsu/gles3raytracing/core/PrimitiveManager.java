package com.tonmatsu.gles3raytracing.core;

import com.tonmatsu.gles3raytracing.gles.*;
import com.tonmatsu.gles3raytracing.utils.*;

import java.nio.*;

import static android.opengl.GLES20.*;

public class PrimitiveManager {
    public final ByteBuffer data;

    private final ShaderStorageBuffer ssbo;
    private final Primitive[] primitives;
    private int size;

    public PrimitiveManager(int capacity) {
        data = BufferUtils.allocBytes(12 * 4 * capacity + 4);
        ssbo = new ShaderStorageBuffer(data.capacity(), GL_DYNAMIC_DRAW);
        primitives = new Primitive[capacity];
        ssbo.bindBufferBase(1);
    }

    public void add(Primitive primitive) {
        primitives[size] = primitive;
        size++;
    }

    public void update() {
        data.clear();
        data.putInt(size);
        for (int i = 0; i < size; i++) {
            primitives[i].write(data);
        }
        data.flip();
        ssbo.update(data);
    }
}
