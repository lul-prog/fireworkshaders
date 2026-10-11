package com.fireworkshaders.client;

import com.fireworkshaders.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Inicialización explícita del cliente, llamada desde el constructor
 * del mod (solo en Dist.CLIENT). Registra los renderers de las
 * entidades sin pasar por @EventBusSubscriber.
 */
@OnlyIn(Dist.CLIENT)
public class ClientSetup {

    public static void init(IEventBus bus) {
        bus.addListener(ClientSetup::onRegisterRenderers);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHADER_ROCKET.get(), ShaderRocketRenderer::new);
        event.registerEntityRenderer(ModEntities.SHADER_BURST.get(), ShaderBurstRenderer::new);
    }
}
