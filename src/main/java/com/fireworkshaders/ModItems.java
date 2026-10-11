package com.fireworkshaders;

import com.fireworkshaders.item.ShaderRocketItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Registro de items del mod. */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, FireworkShaders.MOD_ID);

    public static final RegistryObject<Item> SHADER_ROCKET =
            ITEMS.register("shader_rocket",
                    () -> new ShaderRocketItem(new Item.Properties().stacksTo(16)));
}
