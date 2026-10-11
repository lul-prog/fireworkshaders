package com.fireworkshaders;

import com.fireworkshaders.client.ClientSetup;
import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

/**
 * Firework Shaders: fuegos artificiales sin sistema de partículas.
 * La explosión es una esfera renderizada con un shader GLSL propio
 * (fireworkshaders:burst) + un efecto de pantalla post-processing
 * (fireworkshaders:shaders/post/firework_flash.json).
 */
@Mod(FireworkShaders.MOD_ID)
public class FireworkShaders {
    public static final String MOD_ID = "fireworkshaders";
    private static final Logger LOGGER = LogUtils.getLogger();

    public FireworkShaders() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        ModEntities.ENTITIES.register(bus);
        bus.addListener(this::addCreative);
        // Registro explícito del lado cliente (no depende del escaneo de
        // @EventBusSubscriber): renderers de las entidades del mod.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientSetup.init(bus);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.SHADER_ROCKET);
        }
    }
}
