package com.tonmatsu.gles3raytracing.core;

import org.joml.*;

import java.nio.*;

public class Sphere extends Primitive {
    public final Vector3f position = new Vector3f();
    public float radius;
    public final Vector3f color = new Vector3f();
    public float reflection;
    public float refraction;

    @Override
    protected void write(ByteBuffer buffer) {
        buffer.putInt(1);
        buffer.putFloat(position.x);
        buffer.putFloat(position.y);
        buffer.putFloat(position.z);
        buffer.putFloat(radius);
        buffer.putFloat(radius);
        buffer.putFloat(radius);
        buffer.putFloat(color.x);
        buffer.putFloat(color.y);
        buffer.putFloat(color.z);
        buffer.putFloat(reflection);
        buffer.putFloat(refraction);
    }
}
