#version 150

// Explosión del cohete de shaders. Estado por entidad en los atributos:
//   Color.rgb = color del fuego artificial
//   UV0.x     = edad 0..1 (la escribe el renderer cada frame)
//   UV0.y     = semilla aleatoria por explosión
// La esfera unitaria CRECE con la edad (0.45 -> ~3.85 de radio).

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec4 vColor;
out vec2 vUv;
out vec3 vNormal;
out vec3 vViewPos;
out float vObjR;

void main() {
    float age = clamp(UV0.x, 0.0, 1.0);
    vColor = Color;
    vUv = UV0;
    vNormal = Normal;
    vObjR = length(Position);

    vec3 p = Position * (0.45 + age * 3.4);

    vec4 viewPos = ModelViewMat * vec4(p, 1.0);
    vViewPos = viewPos.xyz;
    gl_Position = ProjMat * viewPos;
}
