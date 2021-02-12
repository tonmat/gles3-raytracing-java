#version 310 es

layout (location = 0) in vec2 a_pos;
layout (location = 1) in vec2 a_texcoords;

out vec2 v_texcoords;

void main() {
    gl_Position = vec4(a_pos, 0.0, 1.0);
    v_texcoords = a_texcoords;
}