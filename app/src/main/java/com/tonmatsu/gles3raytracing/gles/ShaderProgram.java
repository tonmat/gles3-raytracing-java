package com.tonmatsu.gles3raytracing.gles;

import android.util.*;

import com.tonmatsu.gles3raytracing.utils.*;

import org.joml.*;

import java.nio.*;
import java.util.*;

import static android.opengl.GLES31.*;

public class ShaderProgram {
    private static ShaderProgram binded;
    private final int program;
    private final ArrayList<Integer> shaders;
    private final HashMap<String, Integer> uniformsLocations;
    private final FloatBuffer tempBuffer;

    public ShaderProgram() {
        program = glCreateProgram();
        shaders = new ArrayList<>();
        uniformsLocations = new HashMap<>();
        tempBuffer = BufferUtils.allocFloats(16);
    }

    public void attachShader(int type, String asset) {
        final String source = AssetUtils.getString(asset);
        final int shader = glCreateShader(type);
        glShaderSource(shader, source);
        glCompileShader(shader);
        final int[] status = new int[1];
        glGetShaderiv(shader, GL_COMPILE_STATUS, status, 0);
        if (status[0] == GL_FALSE) {
            Log.w("ShaderProgram", "could not compile shader: " + asset);
            Log.w("ShaderProgram", glGetShaderInfoLog(shader));
            glDeleteShader(shader);
            return;
        }
        shaders.add(shader);
        glAttachShader(program, shader);
    }

    public void link() {
        glLinkProgram(program);
        final int[] status = new int[1];
        glGetProgramiv(program, GL_LINK_STATUS, status, 0);
        if (status[0] == GL_FALSE) {
            Log.w("ShaderProgram", "could not link program");
            Log.w("ShaderProgram", glGetProgramInfoLog(program));
            return;
        }
        for (final int shader : shaders)
            glDeleteShader(shader);
        shaders.clear();
    }

    public void bind() {
        if (binded == this)
            return;
        glUseProgram(program);
        binded = this;
    }

    public void unbind() {
        if (binded == null)
            return;
        glUseProgram(GL_NONE);
        binded = null;
    }

    private int getUniformLocation(String name) {
        Integer uniformLocation = uniformsLocations.get(name);
        if (uniformLocation == null) {
            uniformLocation = glGetUniformLocation(program, name);
            uniformsLocations.put(name, uniformLocation);
            if (uniformLocation == -1)
                Log.w("ShaderProgram", "could not get uniform location: " + name);
        }
        return uniformLocation;
    }

    public void setUniform1b(String name, boolean x) {
        glProgramUniform1i(program, getUniformLocation(name), x ? GL_TRUE : GL_FALSE);
    }

    public void setUniform1i(String name, int x) {
        glProgramUniform1i(program, getUniformLocation(name), x);
    }

    public void setUniform1f(String name, float x) {
        glProgramUniform1f(program, getUniformLocation(name), x);
    }

    public void setUniform2f(String name, Vector2f vec2) {
        glProgramUniform2f(program, getUniformLocation(name), vec2.x, vec2.y);
    }

    public void setUniform3f(String name, Vector3f vec3) {
        glProgramUniform3f(program, getUniformLocation(name), vec3.x, vec3.y, vec3.z);
    }

    public void setUniform4f(String name, Vector4f vec4) {
        glProgramUniform4f(program, getUniformLocation(name), vec4.x, vec4.y, vec4.z, vec4.w);
    }

    public void setUniformMatrix3f(String name, Matrix3f mat3) {
        glProgramUniformMatrix3fv(program, getUniformLocation(name), 1, false, mat3.get(tempBuffer));
        tempBuffer.clear();
    }

    public void setUniformMatrix4f(String name, Matrix4f mat4) {
        glProgramUniformMatrix4fv(program, getUniformLocation(name), 1, false, mat4.get(tempBuffer));
        tempBuffer.clear();
    }
}
