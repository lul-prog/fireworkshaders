package com.fireworkshaders.client;

import com.fireworkshaders.FireworkShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Efecto de pantalla de la explosión (post-processing vanilla).
 *
 * trigger() carga shaders/post/firework_flash.json con
 * GameRenderer.loadEffect; el uniform Time del post-shader se anima
 * solo (lo actualiza PostPass cada frame) y al cumplirse los ticks
 * se descarga con shutdownEffect. Recargar reinicia Time en 0.
 *
 * Nota: vanilla solo permite un post-effect a la vez; si el jugador
 * tiene activo otro (araña, creeper), este lo reemplaza ~1 segundo
 * y vanilla lo restaura solo al siguiente tick si sigue aplicando.
 */
@OnlyIn(Dist.CLIENT)
public class FireworkScreenFx {
    private static final ResourceLocation FLASH =
            new ResourceLocation(FireworkShaders.MOD_ID, "shaders/post/firework_flash.json");

    /** Ticks que le quedan al efecto; <= 0 = inactivo. */
    private static int ticksLeft = 0;

    public static void trigger() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.gameRenderer == null) {
            return;
        }
        mc.gameRenderer.loadEffect(FLASH);
        ticksLeft = 24; // ~1.2 s, a juego con la duración del Time en el shader
    }

    public static void clientTick() {
        if (ticksLeft <= 0) {
            return;
        }
        ticksLeft--;
        if (ticksLeft == 0) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameRenderer != null) {
                mc.gameRenderer.shutdownEffect();
            }
        }
    }
}
