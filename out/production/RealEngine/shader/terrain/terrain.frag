#version 400 core

in vec3 fragPosition;
in vec3 fragNormal;
in float fragMaterial;

out vec4 out_Color;

uniform vec3 lightPosition;
uniform vec3 lightDiffuse;
uniform vec3 lightAmbient;
uniform vec3 lightSpecular;

uniform sampler2D textures[6];

void main(void){
    // Texture Attributes
    float textureScale = pow(0.5, 1);

    // Texture Coords
    vec2 texCoord = fragPosition.xz * textureScale;

    // Texture Color
    vec3 dirtDiffuseColor = texture(textures[0], texCoord).rgb;
    vec3 transitionDiffuseColor = texture(textures[2], texCoord).rgb;
    vec3 grassDiffuseColor = texture(textures[4], texCoord).rgb;

    vec3 diffuseColor = mix(dirtDiffuseColor, mix(transitionDiffuseColor, grassDiffuseColor, max(fragMaterial * 2.0 - 1.0, 0.0)), min(fragMaterial * 2.0, 1.0));

    // Texture Normal
    vec3 dirtNormalColor = texture(textures[1], texCoord).rgb;
    vec3 transitionNormalColor = texture(textures[3], texCoord).rgb;
    vec3 grassNormalColor = texture(textures[5], texCoord).rgb;

    vec3 normalColor = mix(dirtNormalColor, mix(transitionNormalColor, grassNormalColor, max(fragMaterial * 2.0 - 1.0, 0.0)), min(fragMaterial * 2.0, 1.0));

    vec3 normalTanSpace = normalize(normalColor * 2.0 - 1.0) * mat3(1, 0, 0, 0, 0, 1, 0, -1, 0);

    // Lighting
    float cosTheta = dot(normalize((normalTanSpace - vec3(0, 1, 0)) + fragNormal), normalize(lightPosition));
    float brightness = clamp(cosTheta, 0, 1);

    out_Color = vec4(diffuseColor * lightDiffuse * brightness + lightAmbient , 1.0);
}