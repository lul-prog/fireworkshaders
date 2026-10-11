package com.fireworkshaders;

import com.fireworkshaders.client.ClientSetup;
import com.fireworkshaders.network.ModNetwork;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

/** Main Forge entry point and registry wiring for the pyrotechnics mod. */
@Mod(FireworkShaders.MOD_ID)
public class FireworkShaders {
    public static final String MOD_ID = "fireworkshaders";

    public FireworkShaders() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEntities.ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModNetwork.register();
        modBus.addListener(this::addCreative);

        // Client-only renderers, screen registrations and the existing custom shader.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientSetup.init(modBus);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.SHADER_ROCKET);
            event.accept(ModItems.CUSTOM_ROCKET);
            event.accept(ModItems.PYROTECHNIC_WORKSTATION);
            event.accept(ModItems.POWDER_STRONTIUM);
            event.accept(ModItems.POWDER_COPPER);
            event.accept(ModItems.POWDER_BARIUM);
            event.accept(ModItems.POWDER_SODIUM);
            event.accept(ModItems.POWDER_POTASSIUM);
            event.accept(ModItems.POWDER_MAGNESIUM);
            event.accept(ModItems.SHAPE_SPHERE);
            event.accept(ModItems.SHAPE_RING);
            event.accept(ModItems.SHAPE_CASCADE);
            event.accept(ModItems.MODIFIER_TRAIL);
            event.accept(ModItems.MODIFIER_CRACKLE);
            event.accept(ModItems.MODIFIER_SMOKE);
        }
    }
}
