package com.fireworkshaders;

import com.fireworkshaders.entity.ShaderBurstEntity;
import com.fireworkshaders.entity.ShaderRocketEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Registro de entidades del mod. */
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, FireworkShaders.MOD_ID);

    public static final RegistryObject<EntityType<ShaderRocketEntity>> SHADER_ROCKET =
            ENTITIES.register("shader_rocket", () -> EntityType.Builder
                    .<ShaderRocketEntity>of(ShaderRocketEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build("shader_rocket"));

    public static final RegistryObject<EntityType<ShaderBurstEntity>> SHADER_BURST =
            ENTITIES.register("shader_burst", () -> EntityType.Builder
                    .<ShaderBurstEntity>of(ShaderBurstEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .build("shader_burst"));
}
