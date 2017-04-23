#version 400 core

in vec3 fragPosition;
in vec3 fragNormal;
in vec3 fragColor;

out vec4 out_Color;

void main(void){
    vec3 lightPos = normalize(vec3(64, 72, 128));
    float cosTheta = dot(fragNormal, lightPos);

//    cosTheta = floor(cosTheta*4)/4;

//    vec3 multiplier = vec3(1, 1, 1) * cosTheta + vec3(0.6, 0.7, 0.1) * (1  - cosTheta);

     float brightness = clamp(cosTheta, 0.2, 1.0);

    out_Color = vec4(fragColor * brightness, 1.0);
}