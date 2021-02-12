#version 310 es
precision highp image2D;

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;
layout (rgba16f, binding = 0) uniform writeonly image2D u_img;

void main() {
    ivec2 pixcoords = ivec2(gl_GlobalInvocationID.xy);
    vec2 img_size = vec2(imageSize(u_img));

    vec2 pos = vec2(2 * pixcoords) / img_size - 1.0;
    vec4 pix = vec4(pos.x, 0.0, pos.y, 1.0);

    imageStore(u_img, pixcoords, pix);
}