package com.tonmatsu.gles3raytracing.core;

import java.nio.*;

public abstract class Primitive {
    protected abstract void write(ByteBuffer buffer);
}
