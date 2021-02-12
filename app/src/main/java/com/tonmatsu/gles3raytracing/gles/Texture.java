package com.tonmatsu.gles3raytracing.gles;

import static android.opengl.GLES31.*;

public class Texture {
    private static Texture binded;
    private final int[] textures = new int[1];

    public Texture() {
        glGenTextures(1, textures, 0);
        bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        unbind();
    }

    public void bind() {
        if (binded == this)
            return;
        glBindTexture(GL_TEXTURE_2D, textures[0]);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glBindTexture(GL_TEXTURE_2D, GL_NONE);
        binded = null;
    }

    public void setParameteri(int pname, int param) {
        bind();
        glTexParameteri(GL_TEXTURE_2D, pname, param);
        unbind();
    }

    public void setStorage(int levels, int internalFormat, int width, int height) {
        bind();
        glTexStorage2D(GL_TEXTURE_2D, levels, internalFormat, width, height);
        unbind();
    }

    public void bindImageTexture(int unit, int access, int format) {
        glBindImageTexture(unit, textures[0], 0, false, 0, access, format);
    }
}
