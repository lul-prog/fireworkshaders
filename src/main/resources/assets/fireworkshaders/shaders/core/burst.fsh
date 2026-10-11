#version 150

// Explosión: fresnel (borde brillante), anillos que viajan hacia afuera,
// chispas moteadas y desvanecido con la edad. Blending aditivo.

in vec4 vColor;
in vec2 vUv;
in vec3 vNormal;
in vec3 vViewPos;
in float vObjR;

uniform vec4 ColorModulator;
uniform float Time;

out vec4 fragColor;

void main() {
    float age = clamp(vUv.x, 0.0, 1.0);
    float seed = vUv.y;

    vec3 V = normalize(-vViewPos);
    vec3 N = normalize(vNormal);
    float fres = pow(1.0 - abs(dot(N, V)), 2.0);

    // Anillos que viajan hacia afuera desde el centro.
    float bands = 0.5 + 0.5 * sin(vObjR * 16.0 - age * 30.0 + seed * 6.2831);
    bands *= bands;

    // Chispas: moteado estable por dirección + semilla.
    vec3 cellDir = floor(N * 7.0 + seed * 13.0);
    float spark = fract(sin(dot(cellDir, vec3(12.9898, 78.233, 37.719))) * 43758.5453);
    spark = step(0.72, spark) * (1.0 - age);

    vec3 base = vColor.rgb;
    vec3 col = base * (0.30 + fres * 1.7);
    col += base * bands * 0.45 * (1.0 - age * 0.6);
    col += vec3(1.0) * spark * 0.9;

    float alpha = (1.0 - age) * clamp(fres * 1.35 + 0.12 + spark * 0.4, 0.0, 1.0);
    fragColor = vec4(col * ColorModulator.rgb, alpha * ColorModulator.a);
}
