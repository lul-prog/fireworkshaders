package com.fireworkshaders.client;

import com.fireworkshaders.ModEntities;
import com.fireworkshaders.ModMenus;
import com.fireworkshaders.client.screen.PyrotechnicWorkstationScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {
    }

    public static void init(IEventBus bus) {
        bus.addListener(ClientSetup::onRegisterRenderers);
        bus.addListener(ClientSetup::onClientSetup);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.SHADER_ROCKET.get(), ShaderRocketRenderer::new);
        event.registerEntityRenderer(ModEntities.CUSTOM_ROCKET.get(), CustomRocketRenderer::new);
        event.registerEntityRenderer(ModEntities.SHADER_BURST.get(), ShaderBurstRenderer::new);
    }

    /**
     * Forge 1.20.1 has no RegisterMenuScreensEvent: container screens are bound with
     * MenuScreens#register inside FMLClientSetupEvent (deferred to the main thread).
     */
    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                MenuScreens.register(ModMenus.PYROTECHNIC_WORKSTATION.get(), PyrotechnicWorkstationScreen::new));
    }
}
