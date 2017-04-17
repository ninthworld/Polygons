#version 400 core

in vec3 position;
in vec3 normal;
in vec3 color;

out vec3 fragPosition;
out vec3 fragNormal;
out vec3 fragColor;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform float clock;

void main(void){
    vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPosition;
    vec4 waves = vec4(0, sin( (position.z * position.x * 5.4978) + (clock * 3.14159 * 2.0)) * 0.3, 0, 0);
    gl_Position = projectionMatrix * positionRelativeToCam + waves;

    fragPosition = position;
    fragNormal = normal;
    fragColor = color;
}