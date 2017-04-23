#version 400 core

in vec2 textureCoords;

out vec4 out_Color;

uniform vec3 cameraPos;
uniform mat4 invProjectionMatrix;
uniform vec2 screenSize;
uniform float[32] samples;
uniform sampler2D terrainColorTexture;
uniform sampler2D terrainDepthTexture;
uniform sampler2D terrainNormalTexture;

vec3 reconstructPosition(vec2 coord, float depth){
    highp vec4 vec = vec4(coord.x, coord.y, depth, 1.0);
    vec = vec * 2.0 - 1.0;
    highp vec4 r = invProjectionMatrix * vec;
    return r.xyz / r.w;
}

const int numSamples = 5; //15;
const float kRadius = 0.004; //0.006;
const float kDistanceThreshold = 4.0; //4.0;

vec4 applySSAO(vec4 color, sampler2D depthTexture, sampler2D normalTexture){
    highp ivec2 texsize = textureSize(depthTexture, 0);
    highp vec3 normal = texture(normalTexture, textureCoords).rgb;
    normal = normalize(normal * 2.0 - 1.0);
    highp float depth = texture(depthTexture, textureCoords).r;
    highp vec3 position = reconstructPosition(textureCoords, depth);

    highp float occlusion = 0.0;
    for(int i=0; i<numSamples; ++i){
        highp vec2 sampleTexture = textureCoords + (samples[i] * kRadius);
        highp float sampleDepth = texture(depthTexture, sampleTexture).r;
        highp vec3 samplePos = reconstructPosition(sampleTexture, sampleDepth);
        highp vec3 diffVec = samplePos - position;
        highp float dist = length(diffVec);
        highp vec3 sampleDir = diffVec * 1.0/dist;
        highp float cosine = max(dot(normal, sampleDir), 0.0);

        highp float a = 1.0 - smoothstep(kDistanceThreshold, kDistanceThreshold * 2.0, dist);
        highp float b = cosine;
        occlusion += (b*a);
    }

    occlusion = 1.0 - occlusion / float(numSamples);
    return vec4(color.rgb * occlusion, color.a);
}

float linearizedDepth(sampler2D txture, vec2 coords){
    float n = 0.1; // Camera Z-Near
    float f = 1000.0; // Camera Z-Far
    float depth = texture(txture, coords).x;
    return (2.0 * n)/(f + n - depth * (f - n));
}

vec4 vMultiply(vec4 v0, vec4 v1){
    return vec4(v0.x * v1.x, v0.y * v1.y, v0.z * v1.z, v0.a * v1.a);
}

void main(void){

    vec4 terrainColor = texture(terrainColorTexture, textureCoords).rgba;
    out_Color = terrainColor;

//    vec4 textureColor = applySSAO(terrainColor, terrainDepthTexture, terrainNormalTexture);
//
//    vec4 normalColor = texture(terrainNormalTexture, textureCoords);
//    float m = 0.00;
//    if(normalColor.r <= m && normalColor.g <= m && normalColor.b <= m){
//        out_Color = vMultiply(textureColor, normalColor) + vMultiply(terrainColor, (vec4(1) - normalColor));
//    }else{
//        out_Color = textureColor;
//    }

    if(cameraPos.y < 0){
        out_Color = vec4(out_Color.xy * 0.6, out_Color.z * 1.8, 1.0);
    }
}
