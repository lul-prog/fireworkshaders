# Firework Shaders — LEEME

Mod para Forge 1.20.1 (47.2.0, Java 17). Esta versión conserva el renderizador GLSL existente y añade la base funcional del creador modular de cohetes.

## Novedades de esta base

- **Estación de pirotecnia**: GUI propia con ranuras para polvo químico, módulo de forma y modificadores independientes de estela, crepitación y humo, más campo para nombrar el cohete.
- **Cohete personalizado**: el servidor valida y consume los ingredientes; el diseño (color, forma, modificador y nombre) se serializa en el objeto y se sincroniza al proyectil.
- **Formas**: esfera, anillo toroidal y silueta de cascada, dibujadas con el shader aditivo del mod.
- **Física inicial**: elevación, arrastre y deriva de viento determinista de baja amplitud; el cohete detona por mecha o colisión.
- **Recetas de supervivencia** para la estación, polvos, formas y modificadores.

## Probar

1. Ejecuta `./gradlew build` y usa el JAR de `build/libs/` en Forge 1.20.1.
2. Fabrica la estación o usa `/give @s fireworkshaders:pyrotechnic_workstation`.
3. Coloca polvo + módulo de forma + modificador opcional, nombra el diseño y recoge el cohete del slot de salida.
4. Clic derecho con el cohete para lanzarlo.

Los componentes también están en Herramientas y utilidades del creativo. El item heredado `/give @s fireworkshaders:shader_rocket` sigue funcionando.

## Dependencias

Solo Forge; el shader se implementa con el pipeline del juego. No se añade Lodestone ni GeckoLib en esta primera fase para evitar dependencias de cliente innecesarias con Oculus/Rubidium.

## Siguiente fase

Los cinco cohetes premium (patrones de vuelo y explosiones dedicados), partículas propias de humo/chispa, cámara/audio espacial, además de Remote Igniter y Launch Pad, todavía requieren implementación. El flash de pantalla existente sí permanece activo.
