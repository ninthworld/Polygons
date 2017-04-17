#version 400 core

in vec3 fragPosition;
in vec3 fragNormal;
in vec3 fragColor;

out vec4 out_Color;

void main(void){
    vec3 lightPos = normalize(vec3(64, 72, 128));
    float cosTheta = dot(fragNormal, lightPos);

    cosTheta = clamp(cosTheta, 0.4, 1.0);
    vec3 multiplier = vec3(1, 1, 1) * cosTheta + vec3(0.3, 0.4, 0.3) * (1  - cosTheta);

    out_Color = vec4(fragColor * multiplier, 1.0);
}