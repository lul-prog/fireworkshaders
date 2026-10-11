package com.fireworkshaders.block;

import com.fireworkshaders.ModBlockEntities;
import com.fireworkshaders.ModBlocks;
import com.fireworkshaders.block.entity.PyrotechnicWorkstationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class PyrotechnicWorkstationBlock extends BaseEntityBlock {
    public PyrotechnicWorkstationBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PyrotechnicWorkstationBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof PyrotechnicWorkstationBlockEntity workstation) {
            NetworkHooks.openScreen(serverPlayer, workstation, buffer -> buffer.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !level.isClientSide
                && level.getBlockEntity(pos) instanceof PyrotechnicWorkstationBlockEntity workstation) {
            SimpleContainer drops = new SimpleContainer(5);
            for (int slot = 0; slot < 5; slot++) {
                ItemStack stack = workstation.getIngredients().getStackInSlot(slot);
                if (!stack.isEmpty()) drops.setItem(slot, stack.copy());
            }
            Containers.dropContents(level, pos, drops);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
