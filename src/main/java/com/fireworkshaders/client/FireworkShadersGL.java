package com.fireworkshaders.client;

import com.fireworkshaders.FireworkShaders;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

/**
 * Registra el shader de la explosión (assets/fireworkshaders/shaders/core/burst.*).
 *
 * Los uniforms son GLOBALES: el estado POR ENTIDAD viaja en los atributos
 * del vértice — el color en Color (rgb) y la edad/semilla en UV0 (x=edad 0..1,
 * y=semilla). El shader nunca lee texturas ni lightmap.
 */
@Mod.EventBusSubscriber(modid = FireworkShaders.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class FireworkShadersGL {

    private static ShaderInstance burst;

    public static ShaderInstance getBurst() {
        return burst;
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                        new ResourceLocation(FireworkShaders.MOD_ID, "burst"),
                        DefaultVertexFormat.NEW_ENTITY),
                instance -> burst = instance);
    }
}
