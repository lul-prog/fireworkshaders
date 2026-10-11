package com.fireworkshaders;

import com.fireworkshaders.block.entity.PyrotechnicWorkstationBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FireworkShaders.MOD_ID);

    public static final RegistryObject<BlockEntityType<PyrotechnicWorkstationBlockEntity>> PYROTECHNIC_WORKSTATION =
            BLOCK_ENTITIES.register("pyrotechnic_workstation", () -> BlockEntityType.Builder
                    .of(PyrotechnicWorkstationBlockEntity::new, ModBlocks.PYROTECHNIC_WORKSTATION.get())
                    .build(null));

    private ModBlockEntities() {
    }
}
