#version 400 core

in vec3 fragPosition;
in vec3 fragNormal;
in vec3 fragColor;

out vec4 out_Color;

void main(void){

    vec4 color0 = vec4(0.4f, 0.9f, 0.9f, 0.8f);
    vec4 color1 = vec4(0f, 0.1f, 1f, 1f);

    out_Color = mix(color1, color0, fragColor.r);
}