package com.tonmatsu.gles3raytracing.core;

import org.joml.*;

import java.nio.*;

public class Box extends Primitive {
    public final Vector3f position = new Vector3f();
    public final Vector3f size = new Vector3f();
    public final Vector3f color = new Vector3f();
    public float reflection;
    public float refraction;

    @Override
    protected void write(ByteBuffer buffer) {
        buffer.putInt(2);
        buffer.putFloat(position.x);
        buffer.putFloat(position.y);
        buffer.putFloat(position.z);
        buffer.putFloat(size.x);
        buffer.putFloat(size.y);
        buffer.putFloat(size.z);
        buffer.putFloat(color.x);
        buffer.putFloat(color.y);
        buffer.putFloat(color.z);
        buffer.putFloat(reflection);
        buffer.putFloat(refraction);
    }
}
