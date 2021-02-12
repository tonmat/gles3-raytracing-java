#version 310 es
precision highp image2D;

#define EPSILON     1e-3
#define REFLECTIONS 4
#define SPHERE      1
#define BOX         2

struct Primitive {
    int type;
    float[3] pos;
    float[3] size;
    float[3] color;
    float reflection;
    float refraction;
};

struct Light {
    float[3] pos;
    float[3] color;
    float[2] attenuation;
};

struct Ray {
    vec3 pos;
    vec3 dir;
};

struct Hit {
    float t;
    vec3 pos;
    vec3 normal;
    Primitive primitive;
};

layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;
layout (rgba16f, binding = 0) uniform writeonly image2D u_img;
layout (std430, binding = 1) readonly buffer Primitives {
    int primitives_length;
    Primitive primitives[];
};
layout (std430, binding = 2) readonly buffer Lights {
    int lights_length;
    Light lights[];
};

Hit hit_sphere(Ray ray, Primitive sphere) {
    Hit hit;
    hit.t = -1.0;
    vec3 sphere_pos = vec3(sphere.pos[0], sphere.pos[1], sphere.pos[2]);
    float radius2 = sphere.size[0] * sphere.size[0];
    vec3 distance = sphere_pos - ray.pos;
    float tca = dot(distance, ray.dir);
    float d2 = dot(distance, distance) - tca * tca;
    if (d2 > radius2) {
        return hit;
    }
    float thc = sqrt(radius2 - d2);
    float t0 = tca - thc;
    float t1 = tca + thc;
    if (t0 > t1) {
        float tempT0 = t0;
        t0 = t1;
        t1 = tempT0;
    }
    if (t0 < 0.0) {
        t0 = t1;
        if (t0 < 0.0) {
            return hit;
        }
    }
    hit.t = t0;
    hit.pos = ray.pos + t0 * ray.dir;
    hit.normal = normalize(hit.pos - sphere_pos);
    hit.primitive = sphere;
    return hit;
}

Hit hit_box(Ray ray, Primitive box) {
    Hit hit;
    hit.t = -1.0;
    vec3 box_pos = vec3(box.pos[0], box.pos[1], box.pos[2]);
    vec3 box_size = vec3(box.size[0], box.size[1], box.size[2]);
    vec3 invDir = 1.0f / ray.dir;
    vec3 signum = sign(invDir);

    float tmin = (box_pos.x - signum.x * box_size.x - ray.pos.x) * invDir.x;
    float tmax = (box_pos.x + signum.x * box_size.x - ray.pos.x) * invDir.x;
    float tymin = (box_pos.y - signum.y * box_size.y - ray.pos.y) * invDir.y;
    float tymax = (box_pos.y + signum.y * box_size.y - ray.pos.y) * invDir.y;

    if (tmin > tymax || tymin > tmax) {
        return hit;
    }
    hit.normal = vec3(-signum.x, 0.0, 0.0);
    if (tymin > tmin) {
        tmin = tymin;
        hit.normal = vec3(0.0, -signum.y, 0.0);
    }
    if (tymax < tmax) {
        tmax = tymax;
    }

    float tzmin = (box_pos.z - signum.z * box_size.z - ray.pos.z) * invDir.z;
    float tzmax = (box_pos.z + signum.z * box_size.z - ray.pos.z) * invDir.z;

    if (tmin > tzmax || tzmin > tmax) {
        return hit;
    }
    if (tzmin > tmin) {
        tmin = tzmin;
        hit.normal = vec3(0.0, 0.0, -signum.z);
    }
    if (tzmax < tmax) {
        tmax = tzmax;
    }

    float t = tmin;
    if (t < 0.0) {
        t = tmax;
        if (t < 0.0) {
            return hit;
        }
    }

    hit.t = t;
    hit.pos = ray.pos + t * ray.dir;
    hit.primitive = box;
    return hit;
}

Hit cast_ray(Ray ray) {
    Hit hit;
    hit.t = -1.0;
    for (int i = 0; i < primitives_length; i++) {
        Hit hit2;
        hit2.t = -1.0;
        Primitive primitive = primitives[i];
        switch (primitive.type) {
            case SPHERE:
            hit2 = hit_sphere(ray, primitive);
            break;
            case BOX:
            hit2 = hit_box(ray, primitive);
            break;
        }
        if (hit2.t >= 0.0) {
            if (hit.t < 0.0 || hit.t > hit2.t) {
                hit = hit2;
            }
        }
    }
    return hit;
}

vec3 cast_shadow_ray(vec3 ray_pos, vec3 normal) {
    Ray ray = Ray(vec3(ray_pos), vec3(0.0));
    vec3 color = vec3(0.0);
    for (int i = 0; i < lights_length; i++) {
        Light light = lights[i];
        vec3 light_pos = vec3(light.pos[0], light.pos[1], light.pos[2]);
        vec3 light_color = vec3(light.color[0], light.color[1], light.color[2]);
        ray.dir = light_pos - ray.pos;
        float distance = length(ray.dir);
        ray.dir = normalize(ray.dir);
        float attenuation = 1.0 / (1.0 + light.attenuation[0] * distance + light.attenuation[1] * distance * distance);
        color += 0.01 * light_color * attenuation;
        Hit hit = cast_ray(ray);
        if (hit.t < 0.0) {
            float lambert = max(dot(ray.dir, normal), 0.0);
            if (lambert > 0.0)
            color += light_color * attenuation * lambert;
        }
    }
    return color;
}

vec3 cast_primary_ray(Ray ray) {
    vec3 color = vec3(0.0);
    float intensity = 1.0;
    int r = 0;
    while (true) {
        Hit hit = cast_ray(ray);
        if (hit.t < 0.0) {
            break;
        }

        vec3 primitive_color = vec3(hit.primitive.color[0], hit.primitive.color[1], hit.primitive.color[2]);
        vec3 shadow_color = cast_shadow_ray(hit.pos - EPSILON * ray.dir, hit.normal);
        color += intensity * primitive_color * shadow_color;

        if (r >= REFLECTIONS) {
            break;
        }

        ray.pos = hit.pos - EPSILON * ray.dir;
        ray.dir = reflect(ray.dir, hit.normal);
        intensity *= hit.primitive.reflection;
        r++;
    }
    return color;
}

void main() {
    ivec2 pixcoords = ivec2(gl_GlobalInvocationID.xy);
    ivec2 img_size = ivec2(imageSize(u_img));
    float ratio = float(img_size.x) / float(img_size.y);
    vec2 pos = vec2(2 * pixcoords - img_size) / vec2(img_size);
    pos.x *= ratio;
    pos.y = -pos.y;

    vec3 ray_dir = normalize(vec3(pos, 4.0));
    vec3 color = cast_primary_ray(Ray(vec3(0.0), ray_dir));
    vec4 pix = vec4(color, 1.0);

    imageStore(u_img, pixcoords, pix);
}