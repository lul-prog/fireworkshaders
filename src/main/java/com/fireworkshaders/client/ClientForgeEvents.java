package com.fireworkshaders.client;

import com.fireworkshaders.FireworkShaders;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Tick de cliente para apagar el efecto de pantalla (bus de Forge). */
@Mod.EventBusSubscriber(modid = FireworkShaders.MOD_ID, value = Dist.CLIENT)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            FireworkScreenFx.clientTick();
        }
    }
}
