#version 310 es
precision highp float;

in vec2 v_texcoords;

uniform sampler2D u_tex;
uniform float u_exposure;
uniform float u_gamma;

out vec4 f_color;

void main() {
    vec3 color = (texture(u_tex, v_texcoords)).xyz;
    color = vec3(1.0) - exp(-color * u_exposure);
    color = pow(color, vec3(1.0 / u_gamma));
    f_color = vec4(color, 1.0);
}