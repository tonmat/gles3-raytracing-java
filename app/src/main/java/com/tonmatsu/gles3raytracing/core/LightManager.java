package com.tonmatsu.gles3raytracing.core;

import com.tonmatsu.gles3raytracing.gles.*;
import com.tonmatsu.gles3raytracing.utils.*;

import java.nio.*;

import static android.opengl.GLES20.GL_DYNAMIC_DRAW;

public class LightManager {
    public final ByteBuffer data;

    private final ShaderStorageBuffer ssbo;
    private final Light[] lights;
    private int size;

    public LightManager(int capacity) {
        data = BufferUtils.allocBytes(8 * 4 * capacity + 4);
        ssbo = new ShaderStorageBuffer(data.capacity(), GL_DYNAMIC_DRAW);
        lights = new Light[capacity];
        ssbo.bindBufferBase(2);
    }

    public void add(Light light) {
        lights[size] = light;
        size++;
    }

    public void update() {
        data.clear();
        data.putInt(size);
        for (int i = 0; i < size; i++) {
            final Light light = lights[i];
            data.putFloat(light.position.x);
            data.putFloat(light.position.y);
            data.putFloat(light.position.z);
            data.putFloat(light.color.x);
            data.putFloat(light.color.y);
            data.putFloat(light.color.z);
            data.putFloat(light.attenuation.x);
            data.putFloat(light.attenuation.y);
        }
        data.flip();
        ssbo.update(data);
    }
}
