package com.fireworkshaders.menu;

import com.fireworkshaders.ModMenus;
import com.fireworkshaders.block.entity.PyrotechnicWorkstationBlockEntity;
import com.fireworkshaders.rocket.RocketDesign;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

/** Server-authoritative modular rocket designer with a live output preview. */
public class PyrotechnicWorkstationMenu extends AbstractContainerMenu {
    private final ItemStackHandler ingredients;
    private final net.minecraft.world.SimpleContainer result = new net.minecraft.world.SimpleContainer(1);
    private final Level level;
    private final BlockPos stationPos;
    private String rocketName = RocketDesign.DEFAULT_NAME;

    /** Client factory used by IForgeMenuType. The station position comes from openScreen(). */
    public PyrotechnicWorkstationMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, resolve(playerInventory.player.level(), data.readBlockPos()));
    }

    /** Server constructor called by the workstation block entity. */
    public PyrotechnicWorkstationMenu(int containerId, Inventory playerInventory,
                                      PyrotechnicWorkstationBlockEntity workstation) {
        this(containerId, playerInventory, new StationAccess(workstation.getIngredients(),
                workstation.getLevel(), workstation.getBlockPos()));
    }

    private PyrotechnicWorkstationMenu(int containerId, Inventory playerInventory, StationAccess access) {
        super(ModMenus.PYROTECHNIC_WORKSTATION.get(), containerId);
        this.ingredients = access.handler;
        this.level = access.level;
        this.stationPos = access.pos;

        this.addSlot(new SlotItemHandler(this.ingredients, 0, 12, 48));  // color powder
        this.addSlot(new SlotItemHandler(this.ingredients, 1, 48, 48));  // shape module
        this.addSlot(new SlotItemHandler(this.ingredients, 2, 84, 48));  // trail modifier
        this.addSlot(new SlotItemHandler(this.ingredients, 3, 120, 48)); // crackle modifier
        this.addSlot(new SlotItemHandler(this.ingredients, 4, 156, 48)); // smoke modifier
        this.addSlot(new Slot(this.result, 0, 204, 48) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                consumeIngredients();
                super.onTake(player, stack);
            }
        });

        // Player inventory and hotbar.
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9,
                        37 + column * 18, 116 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 37 + column * 18, 174));
        }
        this.refreshOutput();
    }

    private static StationAccess resolve(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof PyrotechnicWorkstationBlockEntity workstation) {
            return new StationAccess(workstation.getIngredients(), level, pos);
        }
        // The client can build the menu before its block-entity packet arrives. Server slot sync is authoritative.
        return new StationAccess(new ItemStackHandler(5), level, pos);
    }

    @Override
    public void broadcastChanges() {
        if (!this.level.isClientSide) this.refreshOutput();
        super.broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.level.getBlockEntity(this.stationPos) instanceof PyrotechnicWorkstationBlockEntity
                && this.stationPos.distToCenterSqr(player.position()) <= 64.0D;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= this.slots.size()) return ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        boolean craftingOutput = index == 5;
        if (craftingOutput) {
            if (!this.moveItemStackTo(stack, 6, 42, true)) return ItemStack.EMPTY;
            slot.onQuickCraft(stack, original);
        } else if (index >= 6) {
            int targetSlot = RocketDesign.isColorPowder(stack) ? 0
                    : RocketDesign.isShapeModule(stack) ? 1
                    : RocketDesign.isModifier(stack, RocketDesign.Modifier.TRAIL) ? 2
                    : RocketDesign.isModifier(stack, RocketDesign.Modifier.CRACKLE) ? 3
                    : RocketDesign.isModifier(stack, RocketDesign.Modifier.SMOKE) ? 4 : -1;
            if (targetSlot < 0 || !this.moveItemStackTo(stack, targetSlot, targetSlot + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(stack, 6, 42, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        if (!craftingOutput && stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, craftingOutput ? original : stack);
        return original;
    }

    public String getRocketName() {
        return this.rocketName;
    }

    /** Called only after the server validates the name packet and open container id. */
    public void setRocketName(String name) {
        this.rocketName = RocketDesign.sanitizeName(name);
        this.refreshOutput();
        this.broadcastChanges();
    }

    private void consumeIngredients() {
        for (int slot = 0; slot < 5; slot++) {
            ItemStack input = this.ingredients.getStackInSlot(slot);
            if (input.isEmpty()) continue;
            ItemStack remainder = input.copy();
            remainder.shrink(1);
            this.ingredients.setStackInSlot(slot, remainder);
        }
        this.refreshOutput();
    }

    private void refreshOutput() {
        if (this.level.isClientSide) return;
        ItemStack output = RocketDesign.fromIngredients(
                        this.ingredients.getStackInSlot(0),
                        this.ingredients.getStackInSlot(1),
                        this.ingredients.getStackInSlot(2),
                        this.ingredients.getStackInSlot(3),
                        this.ingredients.getStackInSlot(4),
                        this.rocketName)
                .map(RocketDesign::createRocketStack)
                .orElse(ItemStack.EMPTY);
        if (!ItemStack.matches(this.result.getItem(0), output)) this.result.setItem(0, output);
    }

    private static final class StationAccess {
        private final ItemStackHandler handler;
        private final Level level;
        private final BlockPos pos;

        private StationAccess(ItemStackHandler handler, Level level, BlockPos pos) {
            this.handler = handler;
            this.level = level;
            this.pos = pos;
        }
    }
}
