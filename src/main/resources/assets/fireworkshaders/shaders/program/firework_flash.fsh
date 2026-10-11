#version 150

// Efecto de pantalla de la explosión: destello cálido que decae +
// onda de choque (anillo que se expande desde el centro con distorsión
// radial). Time lo anima PostPass solo; empieza en 0 al cargar.

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform vec2 InSize;
uniform float Time;
uniform vec3 FlashTint;

out vec4 fragColor;

void main() {
    float life = 1.1; // duración visible, en segundos
    float k = clamp(Time / life, 0.0, 1.0);

    vec2 centered = texCoord - vec2(0.5);
    float r = length(centered) * 2.0;

    // Onda de choque: anillo que se expande desde el centro.
    float front = k * 1.7;
    float wave = smoothstep(0.16, 0.0, abs(r - front)) * (1.0 - k);

    // Distorsión radial empujada por el frente de la onda.
    vec2 dir = r > 0.0001 ? centered / r : vec2(0.0);
    vec2 duv = texCoord + dir * wave * 0.05;

    vec3 col = texture(DiffuseSampler, duv).rgb;

    // Destello cálido que decae cuadráticamente.
    float flash = (1.0 - k) * (1.0 - k) * 0.5;
    col = mix(col, FlashTint, flash * 0.55);
    col += FlashTint * wave * 0.30;

    fragColor = vec4(col, 1.0);
}
