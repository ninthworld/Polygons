#version 400 core

in vec3 position;
in vec3 normal;
in float material;

out vec3 fragPosition;
out vec3 fragNormal;
out float fragMaterial;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;
    gl_Position = projectionMatrix * positionRelativeToCam;

    fragPosition = position;
    fragNormal = normal;
    fragMaterial = material;
}