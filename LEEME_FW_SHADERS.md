# Firework Shaders — LEEME

Mod Forge 1.20.1 (47.2.0, Java 17): fuegos artificiales **sin sistema de partículas**.
La explosión es una esfera renderizada con un shader GLSL propio + un efecto
de pantalla post-processing. Dos caminos en un solo mod, como pediste.

## Qué agrega

- **Item "Cohete de shaders"** (pestaña Herramientas y utilidades del creativo):
  clic derecho lo lanza. Sube con leve bamboleo ~1.6 s y explota al terminar
  la mecha (o al chocar con un bloque).
- **Explosión con shader propio** (`fireworkshaders:burst`): esfera que crece,
  borde brillante por fresnel, anillos que viajan hacia afuera, chispas
  moteadas y desvanecido. Blending aditivo, sin texturas, sin lightmap.
  El color se elige al azar por lanzamiento (dorado, menta, malva, celeste,
  coral, crema) y viaja en el color del vértice; la edad (0..1) viaja en
  UV0.x — los uniforms son globales, así que el estado por entidad va en
  los atributos, igual que el posture_glass del parry.
- **Efecto de pantalla** (`shaders/post/firework_flash.json`): destello cálido
  que decae + onda de choque con distorsión radial, ~1.2 s. El uniform `Time`
  lo anima PostPass solo (verificado contra el wobble vanilla); recargar el
  efecto reinicia `Time` en 0.
- Sonidos vanilla: lanzamiento y explosión del cohete normal.

## Compilar

```
cd fireworkshaders
./gradlew build
```

El jar queda en `build/libs/fireworkshaders-1.0.0.jar`. Requiere Forge
1.20.1-47.2.0. No necesita otras dependencias.

## Probar

1. Mundo nuevo, date el item (`/give @s fireworkshaders:shader_rocket`) o
   búscalo en el creativo.
2. Clic derecho mirando al cielo: el cohete sube como un diamante brillante
   y explota en la esfera de luz + el flash en pantalla.
3. Prueba varios seguidos para ver los colores y que el efecto de pantalla
   se reinicia sin pegarse.

## Notas técnicas

- Vanilla solo permite **un post-effect a la vez**: si tienes activo el de
  araña/creeper, la explosión lo reemplaza ~1.2 s; vanilla lo restaura solo
  al tick siguiente si sigue aplicando. Comportamiento aceptado para la v1.
- `GameRenderer.loadEffect` / `shutdownEffect` / `currentEffect()` y el
  formato de los JSON de post/program se verificaron contra el jar y los
  mappings oficiales de 1.20.1 (no de memoria).
- La esfera se reconstruye cada frame (12x18 segmentos, ~1300 vértices);
  es trivial para la GPU.

## Ideas para v2 (cuando quieras)

- Formas de explosión: anillo plano, estrella, doble esfera (semilla en UV0.y
  ya está lista para variar el patrón).
- Estela del cohete con el mismo shader.
- Tinte del flash de pantalla según el color de la explosión (hoy es
  dorado fijo; requeriría setear el uniform por código con reflexión
  sobre los passes, o un post JSON por color).
- Config de duración/intensidad.
