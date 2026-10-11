package com.fireworkshaders.client;

import com.fireworkshaders.ModEntities;
import com.fireworkshaders.ModMenus;
import com.fireworkshaders.client.screen.PyrotechnicWorkstationScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterMenuScreensEvent;
import net.minecraftforge.eventbus.api.IEventBus;

@OnlyIn(Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {
    }

    public static void init(IEventBus bus) {
        bus.addListener(ClientSetup::onRegisterRenderers);
        bus.addListener(ClientSetup::onRegisterMenuScreens);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHADER_ROCKET.get(), ShaderRocketRenderer::new);
        event.registerEntityRenderer(ModEntities.CUSTOM_ROCKET.get(), CustomRocketRenderer::new);
        event.registerEntityRenderer(ModEntities.SHADER_BURST.get(), ShaderBurstRenderer::new);
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.PYROTECHNIC_WORKSTATION.get(), PyrotechnicWorkstationScreen::new);
    }
}
