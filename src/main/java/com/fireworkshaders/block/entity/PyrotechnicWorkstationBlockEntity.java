package com.fireworkshaders.block.entity;

import com.fireworkshaders.ModBlockEntities;
import com.fireworkshaders.menu.PyrotechnicWorkstationMenu;
import com.fireworkshaders.rocket.RocketDesign;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class PyrotechnicWorkstationBlockEntity extends BlockEntity implements net.minecraft.world.MenuProvider {
    private final ItemStackHandler ingredients = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            PyrotechnicWorkstationBlockEntity.this.setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (stack.isEmpty()) return true;
            return switch (slot) {
                case 0 -> RocketDesign.isColorPowder(stack);
                case 1 -> RocketDesign.isShapeModule(stack);
                case 2 -> RocketDesign.isModifier(stack, RocketDesign.Modifier.TRAIL);
                case 3 -> RocketDesign.isModifier(stack, RocketDesign.Modifier.CRACKLE);
                case 4 -> RocketDesign.isModifier(stack, RocketDesign.Modifier.SMOKE);
                default -> false;
            };
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    public PyrotechnicWorkstationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PYROTECHNIC_WORKSTATION.get(), pos, state);
    }

    public ItemStackHandler getIngredients() {
        return this.ingredients;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Ingredients", this.ingredients.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Ingredients", Tag.TAG_COMPOUND)) {
            this.ingredients.deserializeNBT(tag.getCompound("Ingredients"));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.fireworkshaders.pyrotechnic_workstation");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new PyrotechnicWorkstationMenu(containerId, playerInventory, this);
    }
}
