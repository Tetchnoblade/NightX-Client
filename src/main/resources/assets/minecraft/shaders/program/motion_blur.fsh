#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;

varying vec2 texCoord;
varying vec2 oneTexel;

uniform vec2 InSize;

uniform vec3 Phosphor = vec3(0.7, 0.0, 0.0);
uniform float LerpFactor = 1.0;

void main() {
    vec4 CurrTexel = texture2D(DiffuseSampler, texCoord);
    vec4 PrevTexel = texture2D(PrevSampler, texCoord);
    float factor = Phosphor.r;

    if (Phosphor.g == 1) {
        gl_FragColor = vec4(max(PrevTexel.rgb * vec3(factor), CurrTexel.rgb), 1.0);
    } else {
        gl_FragColor = vec4(mix(PrevTexel.rgb, CurrTexel.rgb, factor), 1.0);
    }
}