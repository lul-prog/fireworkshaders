package com.fireworkshaders;

import com.fireworkshaders.item.CustomRocketItem;
import com.fireworkshaders.item.ShaderRocketItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** All rocket ingredients are separate items so datapacks can add recipes later. */
public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, FireworkShaders.MOD_ID);

    public static final RegistryObject<Item> SHADER_ROCKET = ITEMS.register("shader_rocket",
            () -> new ShaderRocketItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> CUSTOM_ROCKET = ITEMS.register("custom_rocket",
            () -> new CustomRocketItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> PYROTECHNIC_WORKSTATION = ITEMS.register("pyrotechnic_workstation",
            () -> new BlockItem(ModBlocks.PYROTECHNIC_WORKSTATION.get(), new Item.Properties()));

    // Color chemistry powders.
    public static final RegistryObject<Item> POWDER_STRONTIUM = simple("powder_strontium");
    public static final RegistryObject<Item> POWDER_COPPER = simple("powder_copper");
    public static final RegistryObject<Item> POWDER_BARIUM = simple("powder_barium");
    public static final RegistryObject<Item> POWDER_SODIUM = simple("powder_sodium");
    public static final RegistryObject<Item> POWDER_POTASSIUM = simple("powder_potassium");
    public static final RegistryObject<Item> POWDER_MAGNESIUM = simple("powder_magnesium");

    // Burst geometry modules.
    public static final RegistryObject<Item> SHAPE_SPHERE = simple("shape_sphere");
    public static final RegistryObject<Item> SHAPE_RING = simple("shape_ring");
    public static final RegistryObject<Item> SHAPE_CASCADE = simple("shape_cascade");

    // Optional payload modifiers, each with an independent workstation slot.
    public static final RegistryObject<Item> MODIFIER_TRAIL = simple("modifier_trail");
    public static final RegistryObject<Item> MODIFIER_CRACKLE = simple("modifier_crackle");
    public static final RegistryObject<Item> MODIFIER_SMOKE = simple("modifier_smoke");

    private ModItems() {
    }

    private static RegistryObject<Item> simple(String name) {
        return ITEMS.register(name, () -> new Item(new Item.Properties()));
    }
}
