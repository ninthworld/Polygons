#version 400 core

in vec3 fragPosition;
in vec3 fragNormal;
in vec3 fragColor;

out vec4 out_Color;

void main(void){

    vec4 color0 = vec4(0.2, 0.5, 0.9, 0.7);
    vec4 color1 = vec4(0, 0.1, 1, 1);

    out_Color = mix(color1, color0, fragColor.r);
}