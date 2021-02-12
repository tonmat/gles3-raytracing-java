package com.tonmatsu.gles3raytracing.utils;

import java.nio.*;

public final class BufferUtils {
    private BufferUtils() {
    }

    public static ByteBuffer allocBytes(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    public static IntBuffer allocInts(int capacity) {
        return allocBytes(capacity * 4).asIntBuffer();
    }

    public static FloatBuffer allocFloats(int capacity) {
        return allocBytes(capacity * 4).asFloatBuffer();
    }

    public static IntBuffer ints(int... i) {
        final IntBuffer buffer = allocInts(i.length);
        buffer.put(i).flip();
        return buffer;
    }

    public static FloatBuffer floats(float... f) {
        final FloatBuffer buffer = allocFloats(f.length);
        buffer.put(f).flip();
        return buffer;
    }
}
